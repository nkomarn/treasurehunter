package net.urbanmc.treasurehunter;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.spawn.EssentialsSpawn;
import net.urbanmc.treasurehunter.command.THCommand;
import net.urbanmc.treasurehunter.listener.*;
import net.urbanmc.treasurehunter.manager.ConfigManager;
import net.urbanmc.treasurehunter.manager.ItemManager;
import net.urbanmc.treasurehunter.runnable.SpawnTask;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class TreasureHunter extends JavaPlugin {

	private static Essentials essentials;
	private String isError;
	private static TreasureHunter instance;
	private TaskChainFactory taskChainFactory;

	public static Essentials getEssentials() {
		return essentials;
	}

	public static TreasureHunter getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		if (!checkDependencies())
			return;

		instance = this;
		taskChainFactory = BukkitTaskChainFactory.create(this);

		initializeManagers();

		registerListeners();
		registerCommand();
		registerRepeatingTasks();

		if (isError()) { // The manager class should print the error reason
			getLogger().info("Cannot start task due to errors. " + isError);
		}
	}

	@Override
	public void onDisable() {
		// Cleanup
		instance = null;
	}

	/*
	 * This method initializes the managers before registering/starting so that we know if there are errors.
	 * If there are any errors, we will not start the task.
	 */
	private void initializeManagers() {
		isError = null;

		ItemManager.getInstance().checkError();
		ConfigManager.checkError();
	}

	public void resetError() { isError = null; }

	public void error(String error, Throwable... throwable) {
		isError = error;

		if (throwable == null)
			Bukkit.getLogger().log(Level.SEVERE, "[TreasureHunter] " + error);
		else
			Bukkit.getLogger().log(Level.SEVERE, "[TreasureHunter] " + error, throwable);
	}

	public boolean isError() {
		return isError != null;
	}

	public void throwError() {
		Bukkit.getLogger().log(Level.SEVERE, "[TreasureHunter] " + isError);
	}

	private void registerListeners() {
		registerListener(new ChestListener());
		registerListener(new CommandListener());
		registerListener(new CompassListener());
		registerListener(new FlyListener());
		registerListener(new GodListener());
		registerListener(new TeleportListener());
	}

	private void registerListener(Listener l) {
		Bukkit.getPluginManager().registerEvents(l, this);
	}

	private void registerCommand() {
		getCommand("treasurehunter").setExecutor(new THCommand());
	}

	private void registerRepeatingTasks() {
		var interval = TimeUnit.MINUTES.toSeconds(ConfigManager.getConfig().getInt("interval", 60)) * 20L;
		getServer().getScheduler().runTaskTimer(this, new SpawnTask(), 0L, interval);
	}

	private boolean checkDependencies() {
		if (Bukkit.getPluginManager().getPlugin("ezAuctions") != null) {
			registerListener(new ezAuctionListener());
		}

		essentials = getPlugin(Essentials.class);

		if (essentials == null || getPlugin(EssentialsSpawn.class) == null) {
			getLogger().severe("Essentials and EssentialsSpawn are needed for Treasure Hunter! Disabling plugin..");
			setEnabled(false);

			return false;
		} else {
			return true;
		}
	}

	public <T> TaskChain<T> newChain() {
		return taskChainFactory.newChain();
	}
}
