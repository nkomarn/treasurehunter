package net.urbanmc.treasurehunter.runnable;

import net.urbanmc.randomtp.Util;
import net.urbanmc.treasurehunter.TreasureHunter;
import net.urbanmc.treasurehunter.manager.ConfigManager;
import net.urbanmc.treasurehunter.manager.TreasureChestManager;
import net.urbanmc.treasurehunter.object.TreasureChest;
import net.urbanmc.treasurehunter.object.TreasureChest.TreasureChestType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class SpawnTask extends BukkitRunnable {

	private static SpawnTask instance = new SpawnTask();

	private SpawnTask() {
	}

	public static SpawnTask getInstance() {
		return instance;
	}

	void start(TreasureHunter plugin) {
		runTaskTimer(plugin, 0, 72000);
	}

	@Override
	public void run() {
		Location loc = randomLocation();

		if (loc == null) {
			Bukkit.getLogger().severe("[TreasureHunter] Error generating location for chest!");
			return;
		}

		TreasureChestType type = randomType();

		TreasureChest chest = new TreasureChest(type, loc.getBlock());

		TreasureChestManager.getInstance().setCurentChest(chest);
	}

	private Location randomLocation() {
		World world = Bukkit.getWorld(ConfigManager.getConfig().getString("world"));

		return Util.generateLocation(world);
	}

	private TreasureChestType randomType() {
		Random r = ThreadLocalRandom.current();

		return null;
	}
}
