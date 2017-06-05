package net.urbanmc.treasurehunter.runnable;

import net.urbanmc.treasurehunter.TreasureHunter;
import org.bukkit.scheduler.BukkitRunnable;

public class SpawnTask extends BukkitRunnable {

	private static SpawnTask instance = new SpawnTask();

	private SpawnTask() {
	}

	public static SpawnTask getInstance() {
		return instance;
	}

	public void start(TreasureHunter plugin) {
		runTaskTimer(plugin, 0, 72000);
	}

	@Override
	public void run() {

	}
}
