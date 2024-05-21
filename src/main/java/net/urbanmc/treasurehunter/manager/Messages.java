package net.urbanmc.treasurehunter.manager;

import net.urbanmc.treasurehunter.TreasureHunter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class Messages {
	private static final String BUNDLE_NAME = "messages";
	private static Messages instance = new Messages();

	private ResourceBundle bundle;

	private Messages() {
		loadBundle();
	}

	public static String getString(String key, Object... args) {
		try {
			return instance.format(instance.bundle.getString(key), args);
		} catch (Exception e) {
			return key;
		}
	}

	private void loadBundle() {
		File dataDirectory = JavaPlugin.getPlugin(TreasureHunter.class).getDataFolder();
		File resource = new File(dataDirectory, BUNDLE_NAME + ".properties");

		if (resource.exists()) {
			try {
				bundle = new PropertyResourceBundle(new FileInputStream(resource));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			return;
		}

		/* fall back to included messages bundle */
		bundle = ResourceBundle.getBundle("messages");
	}

	private String format(String message, Object... args) {
		message = message.replace("{prefix}", bundle.getString("prefix"));

		if (args != null) {
			message = MessageFormat.format(message, args);
		}

		message = ChatColor.translateAlternateColorCodes('&', message);

		return message;
	}
}
