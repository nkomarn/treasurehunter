package net.urbanmc.treasurehunter.runnable;

import net.urbanmc.treasurehunter.TreasureHunter;
import net.urbanmc.treasurehunter.manager.*;
import net.urbanmc.treasurehunter.object.TreasureChest;
import net.urbanmc.treasurehunter.object.TreasureChest.TreasureChestType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class SpawnTask extends BukkitRunnable {

	private static SpawnTask instance = new SpawnTask();

	private SpawnTask() {
	}

	public static SpawnTask getInstance() {
		return instance;
	}

	static void start(TreasureHunter plugin) {
		if (instance.hasBeenScheduled()) {
			instance = new SpawnTask();
		}

		instance.runTaskTimer(plugin, 0, 72000);
	}

	@Override
	public void run() {
		TreasureChestManager.getInstance().removeCurrentChest();

		TreasureChestType type = randomType();

		Location loc = randomLocation(type);

		if (loc == null) {
			Bukkit.getLogger().severe("[TreasureHunter] Error generating location for chest!");
			return;
		}

		Block b = loc.getBlock();

		TreasureChest chest = new TreasureChest(type, b);

		TreasureChestManager.getInstance().setCurrentChest(chest);

		b.setType(Material.CHEST);

		Chest c = (Chest) b.getState();

		List<ItemStack> items = getItems(type);

		ItemStack[] itemArray = new ItemStack[items.size()];
		itemArray = items.toArray(itemArray);

		c.getBlockInventory().addItem(itemArray);

		Bukkit.broadcastMessage(Messages.getString("broadcast.start", type.getDisplayName().toLowerCase()));
	}

	private Location randomLocation(TreasureChestType type) {
		World world = Bukkit.getWorld(ConfigManager.getConfig().getString("world"));

		return SpawnManager.getInstance().generateLocation(world, type);
	}

	private TreasureChestType randomType() {
		return ItemManager.getInstance().randomChestType();
	}

	private List<ItemStack> getItems(TreasureChestType type) {
		return ItemManager.getInstance().getRandomItemsForChestType(type);
	}

	private boolean hasBeenScheduled() {
		try {
			instance.getTaskId();
			return true;
		} catch (IllegalStateException ex) {
			return false;
		}
	}

	/*
	 * @return true if successfully; false if error (such as never started)
	 */
	private boolean cancelTask() {
		try {
			cancel();
			return true;
		} catch (IllegalStateException ex) {
			return false;
		}
	}

	public void forceSpawn(TreasureHunter plugin) {
		cancelTask();

		instance = new SpawnTask();

		instance.runTask(plugin);
	}
}
