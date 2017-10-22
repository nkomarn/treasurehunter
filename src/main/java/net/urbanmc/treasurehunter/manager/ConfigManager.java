package net.urbanmc.treasurehunter.manager;

import net.urbanmc.treasurehunter.TreasureHunter;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.List;

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

		if (!getConfig().getBoolean("ready")) {
			plugin.getLogger().severe("The config file has not been properly edited! Please make sure all " +
					                          "configurations are to your liking, and change \"ready\" to true in " +
					                          "config.yml.");
			plugin.error();
		}
	}

	public static FileConfiguration getConfig() {
		return instance.getData();
	}

	public static ConfigManager getInstance() {
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

	private void loadConfig() {
		data = YamlConfiguration.loadConfiguration(FILE);
	}

	public void reloadConfig() {
		data = YamlConfiguration.loadConfiguration(FILE);
	}

	private FileConfiguration getData() {
		return data;
	}

	public List<String> getBlockedCommands() {
		return getConfig().getStringList("blocked-commands");
	}
}
