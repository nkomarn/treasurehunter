package net.urbanmc.treasurehunter;

import net.urbanmc.treasurehunter.runnable.StartTask;
import org.bukkit.plugin.java.JavaPlugin;

public class TreasureHunter extends JavaPlugin {

	private boolean isError = false;

	@Override
	public void onEnable() {
		initializeManagers();

		if (isError) // The manager class should print the error reason
			return;

		registerListeners();
		registerCommand();
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

	}

	public void error() {
		isError = true;
	}

	private void registerListeners() {

	}

	private void registerCommand() {

	}

	private void start() {
		new StartTask(this);
	}
}
