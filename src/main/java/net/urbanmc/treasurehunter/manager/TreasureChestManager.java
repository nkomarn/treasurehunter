package net.urbanmc.treasurehunter.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.urbanmc.treasurehunter.gson.TreasureChestSerializer;
import net.urbanmc.treasurehunter.object.TreasureChest;
import org.apache.commons.io.IOUtils;
import org.bukkit.Material;
import org.bukkit.block.Chest;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class TreasureChestManager {

	private static TreasureChestManager instance = new TreasureChestManager();

	private final File FILE = new File("plugins/TreasureHunter", "chest.json");
	private final Gson gson =
			new GsonBuilder().registerTypeAdapter(TreasureChest.class, new TreasureChestSerializer()).create();

	private TreasureChest chest;

	private TreasureChestManager() {
		createFile();
		loadChest();
	}

	public static TreasureChestManager getInstance() {
		return instance;
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

	private void loadChest() {
		try {
			Scanner scanner = new Scanner(FILE);

			chest = gson.fromJson(scanner.nextLine(), TreasureChest.class);

			scanner.close();
		} catch (Exception e) {
			if (!(e instanceof NoSuchElementException)) {
				e.printStackTrace();
			}
		}
	}

	private void saveChest() {
		try {
			PrintWriter writer = new PrintWriter(FILE);

			writer.write(gson.toJson(chest));

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public TreasureChest getCurrentChest() {
		return chest;
	}

	public void setCurrentChest(TreasureChest treasureChest) {
		chest = treasureChest;
		saveChest();
	}

	public void removeCurrentChest() {
		if (getCurrentChest() == null)
			return;

		Chest c = (Chest) getCurrentChest().getBlock().getState();

		c.getBlockInventory().clear();
		c.setType(Material.AIR);

		saveChest();
	}
}
