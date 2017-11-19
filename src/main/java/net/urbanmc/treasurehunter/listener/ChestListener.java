package net.urbanmc.treasurehunter.listener;

import net.urbanmc.treasurehunter.manager.Messages;
import net.urbanmc.treasurehunter.manager.TreasureChestManager;
import net.urbanmc.treasurehunter.object.TreasureChest;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class ChestListener implements Listener {

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (TreasureChestManager.getInstance().getCurrentChest() == null)
			return;

		Block eventBlock = e.getBlock();
		Block chestBlock = TreasureChestManager.getInstance().getCurrentChest().getBlock();

		if (eventBlock.equals(chestBlock)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e) {
		TreasureChest chest = TreasureChestManager.getInstance().getCurrentChest();

		if (chest == null)
			return;

		if (chest.isFound())
			return;

		if (!(e.getInventory().getHolder() instanceof Chest))
			return;

		Chest c = (Chest) e.getInventory().getHolder();

		if (c.getBlock().equals(chest.getBlock())) {
			chest.setFound();
			TreasureChestManager.getInstance().saveChest();

			String name = e.getPlayer().getName(), chestType = chest.getType().getDisplayName();
			Bukkit.broadcastMessage(Messages.getString("broadcast.found", name.toLowerCase(), chestType));
		}
	}
}
