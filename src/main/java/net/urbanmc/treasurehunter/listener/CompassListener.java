package net.urbanmc.treasurehunter.listener;

import net.urbanmc.treasurehunter.command.subcommands.StartSub;
import net.urbanmc.treasurehunter.manager.Messages;
import net.urbanmc.treasurehunter.manager.TreasureChestManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CompassListener implements Listener {

	@EventHandler
	public void onCompassClick(PlayerInteractEvent e) {
		if (TreasureChestManager.getInstance().getCurrentChest() == null)
			return;

		Player p = e.getPlayer();

		if (!p.getInventory().getItemInMainHand().equals(StartSub.compass) &&
				!p.getInventory().getItemInOffHand().equals(StartSub.compass))
			return;

		e.setCancelled(true);

		//The code below ensures that even if they give their compass to another player it won't work :>

		if (!TreasureChestManager.getInstance().getCurrentChest().isHunting(p)) {
			p.sendMessage(Messages.getString("compass.invalid-finder"));
			p.getInventory().remove(StartSub.compass);

			return;
		}

		p.setCompassTarget(TreasureChestManager.getInstance().getCurrentChest().getBlock().getLocation());
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		ItemStack current = e.getCurrentItem(), cursor = e.getCursor();
		Inventory inventory = e.getClickedInventory();

		if ((StartSub.compass.isSimilar(current) || StartSub.compass.isSimilar(cursor)) &&
				(inventory == null || inventory.getType() != InventoryType.PLAYER)) {
			e.setCancelled(true);
		}
	}
}
