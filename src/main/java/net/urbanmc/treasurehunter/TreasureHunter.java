package net.urbanmc.treasurehunter;

import net.urbanmc.treasurehunter.command.THCommand;
import net.urbanmc.treasurehunter.manager.ConfigManager;
import net.urbanmc.treasurehunter.runnable.StartTask;
import org.bukkit.plugin.java.JavaPlugin;

public class TreasureHunter extends JavaPlugin {

	private boolean isError = false;

	@Override
	public void onEnable() {
		initializeManagers();

		registerListeners();
		registerCommand();

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

	}

	private void registerCommand() {
		getCommand("treasurehunter").setExecutor(new THCommand());
	}

	private void start() {
		new StartTask(this);
	}
}
