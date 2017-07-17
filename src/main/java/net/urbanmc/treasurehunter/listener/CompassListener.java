package net.urbanmc.treasurehunter.listener;

import net.urbanmc.treasurehunter.command.subcommands.StartSub;
import net.urbanmc.treasurehunter.manager.Messages;
import net.urbanmc.treasurehunter.manager.TreasureChestManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class CompassListener implements Listener {

	@EventHandler
	public void onCompassClick(PlayerInteractEvent e) {
		if (TreasureChestManager.getInstance().getCurrentChest() == null)
			return;

		if (!e.getPlayer().getInventory().getItemInMainHand().equals(StartSub.compass) &&
				!e.getPlayer().getInventory().getItemInOffHand().equals(StartSub.compass))
			return;

		e.setCancelled(true);

		//The code below ensures that even if they give their compass to another player it won't work :>

		if (!TreasureChestManager.getInstance().getCurrentChest().getHunting().contains(e.getPlayer().getUniqueId())) {
			e.getPlayer().sendMessage(Messages.getString("compass.invalid-finder"));

			e.getPlayer().getInventory().remove(StartSub.compass);
			return;
		}

		e.getPlayer().setCompassTarget(TreasureChestManager.getInstance().getCurrentChest().getBlock().getLocation());
	}

}
