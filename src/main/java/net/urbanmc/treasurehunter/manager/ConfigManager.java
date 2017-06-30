package net.urbanmc.treasurehunter.manager;

import net.urbanmc.treasurehunter.TreasureHunter;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

public class ConfigManager {

	private static ConfigManager instance = new ConfigManager();

	private final File FILE = new File("plugins/TreasureHunter", "config.yml");
	private FileConfiguration data;

	private ConfigManager() {
		createFile();
		loadConfig();
	}

	public static void checkError(TreasureHunter plugin) {
		if (getConfig() == null) {
			plugin.getLogger().severe("Error loading config! Try deleting the config file.");
			plugin.error();
		}

		World world = Bukkit.getWorld(getConfig().getString("world"));

		if (world == null) {
			plugin.getLogger().severe("World is not loaded! Cannot start without a properly loaded world!");
			plugin.error();
		}
	}

	public static FileConfiguration getConfig() {
		return instance.getData();
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

	private void loadConfig() {
		data = YamlConfiguration.loadConfiguration(FILE);
	}

	private FileConfiguration getData() {
		return data;
	}
}
