package net.urbanmc.treasurehunter.runnable;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import io.papermc.lib.PaperLib;
import net.urbanmc.treasurehunter.TreasureHunter;
import net.urbanmc.treasurehunter.manager.*;
import net.urbanmc.treasurehunter.object.TreasureChest;
import net.urbanmc.treasurehunter.object.TreasureChest.TreasureChestType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;


public class SpawnTask implements Runnable {
	private final TreasureChestType desiredType;

	public SpawnTask() {
		this(null);
	}

	public SpawnTask(@Nullable TreasureChestType desiredType) {
		this.desiredType = desiredType;
	}

	@Override
	public void run() {
		if (TreasureHunter.getInstance().isError()) {
			TreasureHunter.getInstance().throwError();
			return;
		}

		TaskChain<?> chain = TreasureHunter.getInstance().newChain();

		chain.sync(() -> {
			World world = Bukkit.getWorld(ConfigManager.getConfig().getString("world"));

			if (world == null) {
				Bukkit.getLogger().severe("[TreasureHunter] Invalid world for spawn location!");
				return;
			}

			final double[] worldBorder;

			if (world.getWorldBorder() != null && world.getWorldBorder().getSize() > 0) {
				Location centerLoc = world.getWorldBorder().getCenter();
				int centerX = centerLoc.getBlockX(), centerZ = centerLoc.getBlockZ();
				double size = world.getWorldBorder().getSize();

				worldBorder = new double[4];
				worldBorder[0] = centerX + size;
				worldBorder[1] = centerX - size;
				worldBorder[2] = centerZ + size;
				worldBorder[3] = centerZ - size;
			} else
				worldBorder = null;

			chain.setTaskData("world", world);
			chain.setTaskData("worldName", world.getName());
			chain.setTaskData("worldborder", worldBorder);
		}).async(() -> {
			TreasureChestType type = randomType();


			//Get x and z cords
			int[] xZ = SpawnManager.getInstance().generateLocationAsync(
					chain.getTaskData("worldName"), chain.getTaskData("worldBorder"), type);

			// Handle null location array
			if (xZ == null) {
				chain.abortChain();
				return;
			}

			chain.removeTaskData("worldName");
			chain.removeTaskData("worldBorder");

			//Get items list
			ItemStack[] itemArray = getItems(type).toArray(new ItemStack[0]);

			chain.setTaskData("type", type);
			chain.setTaskData("x", xZ[0]);
			chain.setTaskData("z", xZ[1]);
			chain.setTaskData("items", itemArray);
		}).sync(() -> {
			PaperLib.getChunkAtAsync(
					chain.getTaskData("world"),
					chain.getTaskData("x"),
					chain.getTaskData("z"))
					.thenRun(() -> {
						runSyncTask(chain.getTaskData("world"),
								chain.getTaskData("x"),
								chain.getTaskData("z"),
								chain.getTaskData("type"),
								chain.getTaskData("items"));
					});
		}).execute();
	}

	private TreasureChestType randomType() {
		return desiredType == null
				? ItemManager.getInstance().randomChestType()
				: desiredType;
	}

	private List<ItemStack> getItems(TreasureChestType type) {
		return ItemManager.getInstance().getRandomItemsForChestType(type);
	}

	private void runSyncTask(World world, int x, int z, TreasureChestType type, ItemStack[] items) {
		int y;

		// Get highest block using world method with a fail-safe if that throws an NPE.
		try {
		 y = world.getHighestBlockYAt(x, z);
		} catch (NullPointerException ex) {
			y = 256;
			for (Material mat = world.getBlockAt(x, y, z).getType();
				 mat.equals(Material.AIR) || mat.equals(Material.VOID_AIR) || mat.equals(Material.CAVE_AIR);
				 mat = world.getBlockAt(x, y, z).getType()) {
				y -= 1;
			}

			y++;
		}

		//Get the location from the cords and world
		Location loc = new Location(world, x, y, z);

		if (loc == null) {
			Bukkit.getLogger().severe("[TreasureHunter] Error generating location for chest!");
			return;
		}

		TreasureChestManager.getInstance().removeCurrentChest();

		Block b = loc.getBlock();

		TreasureChest chest = new TreasureChest(type, b);

		TreasureChestManager.getInstance().setCurrentChest(chest);

		b.setType(Material.CHEST, true);

		if (!(b.getState() instanceof Chest)) {
			Bukkit.getLogger().severe("[TreasureHunter] Error spawning chest. Blockstate is not a chest! Location: " + loc.toString());
			return;
		}

		Chest c = (Chest) b.getState();

		c.getBlockInventory().addItem(items);

		String typeName = type.getDisplayName().toLowerCase();

		typeName = typeName.substring(0,2) + ChatColor.BOLD + typeName.substring(2);

		Bukkit.broadcastMessage(Messages.getString("broadcast.start", type == TreasureChestType.EPIC ? "n" : "", typeName));
	}
}
