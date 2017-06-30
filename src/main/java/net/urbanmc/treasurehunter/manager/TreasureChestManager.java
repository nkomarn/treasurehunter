package net.urbanmc.treasurehunter.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.urbanmc.treasurehunter.gson.TreasureChestSerializer;
import net.urbanmc.treasurehunter.object.TreasureChest;
import net.urbanmc.treasurehunter.object.TreasureChestList;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class TreasureChestManager {

	private static TreasureChestManager instance = new TreasureChestManager();

	private final File FILE = new File("plugins/TreasureHunter", "chest.json");
	private final Gson gson =
			new GsonBuilder().registerTypeAdapter(TreasureChest.class, new TreasureChestSerializer()).create();

	private List<TreasureChest> chest;

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

			chest = gson.fromJson(scanner.nextLine(), TreasureChestList.class).getChest();

			scanner.close();
		} catch (Exception e) {
			if (!(e instanceof NoSuchElementException)) {
				e.printStackTrace();
			}

			chest = new ArrayList<>();
		}
	}

	public TreasureChest getCurrentChest() {
		return chest.isEmpty() ? null : chest.get(0);
	}

	public void setCurentChest(TreasureChest treasureChest) {
		if (!chest.isEmpty()) {
			chest.remove(0);
		}

		chest.add(treasureChest);
	}
}
