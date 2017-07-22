package net.urbanmc.treasurehunter.runnable;

import net.urbanmc.randomtp.Util;
import net.urbanmc.treasurehunter.TreasureHunter;
import net.urbanmc.treasurehunter.manager.ConfigManager;
import net.urbanmc.treasurehunter.manager.ItemManager;
import net.urbanmc.treasurehunter.manager.Messages;
import net.urbanmc.treasurehunter.manager.TreasureChestManager;
import net.urbanmc.treasurehunter.object.TreasureChest;
import net.urbanmc.treasurehunter.object.TreasureChest.TreasureChestType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class SpawnTask extends BukkitRunnable {

	private static SpawnTask instance = new SpawnTask();

	private SpawnTask() {
	}

	public static SpawnTask getInstance() {
		return instance;
	}

	void start(TreasureHunter plugin) {
		runTaskTimer(plugin, 0, 72000);
	}

	@Override
	public void run() {
		Location loc = randomLocation();

		if (loc == null) {
			Bukkit.getLogger().severe("[TreasureHunter] Error generating location for chest!");
			return;
		}

		Block b = loc.getBlock();

		TreasureChestType type = randomType();

		TreasureChest chest = new TreasureChest(type, b);

		TreasureChestManager.getInstance().setCurentChest(chest);

		b.setType(Material.CHEST);

		Chest c = (Chest) b.getState();

		List<ItemStack> items = getItems(type);

		ItemStack[] itemArray = new ItemStack[items.size()];
		itemArray = items.toArray(itemArray);

		c.getBlockInventory().addItem(itemArray);

		Bukkit.broadcastMessage(Messages.getString("hunting.new-chest", type.toString()));
	}

	private Location randomLocation() {
		World world = Bukkit.getWorld(ConfigManager.getConfig().getString("world"));

		return Util.generateLocation(world);
	}

	private TreasureChestType randomType() {
		return ItemManager.getInstance().randomChestType();
	}

	private List<ItemStack> getItems(TreasureChestType type) {
		return ItemManager.getInstance().getItemsForChestType(type);
	}
}
