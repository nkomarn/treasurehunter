package net.urbanmc.treasurehunter.manager;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import net.urbanmc.treasurehunter.TreasureHunter;
import net.urbanmc.treasurehunter.object.TreasureChest.TreasureChestType;
import net.urbanmc.treasurehunter.util.ItemUtil;
import org.apache.commons.io.IOUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class ItemManager {

	private static ItemManager instance = new ItemManager();

	private final File FILE = new File("plugins/TreasureHunter", "items.yml");

	private RangeMap<Double, TreasureChestType> percentageMap;
	private Map<TreasureChestType, List<ItemStack>> itemMap;

	private ItemManager() {
		createFile();
	}

	public static ItemManager getInstance() {
		return instance;
	}

	public void checkError(TreasureHunter plugin) {
		FileConfiguration data = YamlConfiguration.loadConfiguration(FILE);

		try {
			loadPercentages(data);
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "Error loading percentages!", e);
			plugin.error();
		}

		try {
			loadItems(data);
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "Error loading items!", e);
			plugin.error();
		}
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	private void createFile() {
		if (!FILE.getParentFile().exists()) {
			FILE.getParentFile().mkdir();
		}

		if (!FILE.exists()) {
			try {
				FILE.createNewFile();

				InputStream input = getClass().getClassLoader().getResourceAsStream("config.yml");
				OutputStream output = new FileOutputStream(FILE);

				IOUtils.copy(input, output);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadPercentages(FileConfiguration data) {
		percentageMap = TreeRangeMap.create();

		double lowerBound = 0;

		for (TreasureChestType type : TreasureChestType.values()) {
			double chance = data.getDouble("percentages." + type.name().toLowerCase());

			if (chance == 0)
				continue;

			Range<Double> range = Range.closedOpen(lowerBound, lowerBound + chance);

			percentageMap.put(range, type);

			lowerBound = lowerBound + chance;
		}
	}

	private void loadItems(FileConfiguration data) {
		for (TreasureChestType type : TreasureChestType.values()) {
			List<String> itemStringList = data.getStringList("items." + type.name().toLowerCase());
			List<ItemStack> items = ItemUtil.getItemList(itemStringList);

			itemMap.put(type, items);
		}
	}

	public double getPercentage(TreasureChestType type) {
		Map<Range<Double>, TreasureChestType> map = percentageMap.asMapOfRanges();

		for (Entry<Range<Double>, TreasureChestType> entry : map.entrySet()) {
			if (entry.getValue() == type) {
				Range<Double> range = entry.getKey();
				return range.upperEndpoint() - range.lowerEndpoint();
			}
		}

		return 0;
	}

	public TreasureChestType randomChestType() {
		double r = ThreadLocalRandom.current().nextDouble() * 100;
		return percentageMap.get(r);
	}

	public List<ItemStack> getItemsForChestType(TreasureChestType type) {
		return itemMap.get(type);
	}
}
