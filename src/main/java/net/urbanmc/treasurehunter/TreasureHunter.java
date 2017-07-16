package net.urbanmc.treasurehunter;

import com.earth2me.essentials.Essentials;
import net.urbanmc.treasurehunter.command.THCommand;
import net.urbanmc.treasurehunter.listeners.CompassListener;
import net.urbanmc.treasurehunter.listeners.FlyListener;
import net.urbanmc.treasurehunter.listeners.GodListener;
import net.urbanmc.treasurehunter.manager.ConfigManager;
import net.urbanmc.treasurehunter.runnable.StartTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class TreasureHunter extends JavaPlugin {

	private boolean isError = false;
	private static Essentials essentials;

	@Override
	public void onEnable() {
		initializeManagers();

		registerListeners();
		registerCommand();

		checkEssentials();

		if (isError) { // The manager class should print the error reason
			getLogger().info("Cannot start task due to errors.");
			return;
		}

		start();
	}

	@Override
	public void onDisable() {

	}

	/*
	 * This method initializes the managers before registering/starting so that we know if there are errors.
	 * If there are any errors, we will not start the task.
	 */
	private void initializeManagers() {
		ConfigManager.checkError(this);
	}

	public void error() {
		isError = true;
	}

	private void registerListeners() {
		Bukkit.getPluginManager().registerEvents(new CompassListener(), this);
		Bukkit.getPluginManager().registerEvents(new FlyListener(), this);
		Bukkit.getPluginManager().registerEvents(new GodListener(), this);
	}

	private void registerCommand() {
		getCommand("treasurehunter").setExecutor(new THCommand(this));
	}

	private void start() {
		new StartTask(this);
	}

	private void checkEssentials() {
		if(Bukkit.getServer().getPluginManager().getPlugin("Essentials") == null) {
			getLogger().log(Level.SEVERE, "Essentials needed for Treasure Hunter!");

			Bukkit.getServer().getPluginManager().disablePlugin(this);

			return;
		}

		essentials = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
	}

	public static Essentials getEssentials() {
		return essentials;
	}
}
