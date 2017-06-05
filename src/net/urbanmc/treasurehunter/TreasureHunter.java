package net.urbanmc.treasurehunter;

import net.urbanmc.treasurehunter.runnable.StartTask;
import org.bukkit.plugin.java.JavaPlugin;

public class TreasureHunter extends JavaPlugin {

	@Override
	public void onEnable() {
		registerListeners();
		registerCommand();
		start();
	}

	@Override
	public void onDisable() {

	}

	private void registerListeners() {

	}

	private void registerCommand() {

	}

	private void start() {
		new StartTask(this);
	}
}
