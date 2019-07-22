package net.urbanmc.treasurehunter.listener;

import net.urbanmc.treasurehunter.command.subcommands.StartSub;
import net.urbanmc.treasurehunter.manager.Messages;
import net.urbanmc.treasurehunter.manager.TreasureChestManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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

		p.sendMessage(Messages.getString("compass.track"));
		p.setCompassTarget(TreasureChestManager.getInstance().getCurrentChest().getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryClick(InventoryClickEvent e) {
		ItemStack current = e.getCurrentItem(), cursor = e.getCursor();
		Inventory inventory = e.getClickedInventory();

		boolean currentHand;

		if (((currentHand = StartSub.compass.isSimilar(current)) || (StartSub.compass.isSimilar(cursor))) &&
				(inventory == null || inventory.getType() != InventoryType.PLAYER || e.getClick().isShiftClick())) {

			Player player = (Player) e.getWhoClicked();

			if (player.hasPermission("treasurehunter.admin")) return;

			if (e.getView().getTopInventory().getType().equals(InventoryType.CRAFTING)) return;

			if (currentHand) {
				e.setCurrentItem(null);
			} else {
				e.getView().setCursor(null);
			}
			player.getInventory().addItem(StartSub.compass.clone());

			e.setCancelled(true);
		}
	}
}
