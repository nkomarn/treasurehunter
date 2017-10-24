package net.urbanmc.treasurehunter.listener;

import net.urbanmc.treasurehunter.manager.TreasureChestManager;
import net.urbanmc.treasurehunter.object.TreasureChest;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class TeleportListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		Player p = e.getPlayer();
		TreasureChest chest = TreasureChestManager.getInstance().getCurrentChest();

		if (chest == null)
			return;

		if (chest.isHunting(p) && !e.getCause().equals(TeleportCause.ENDER_PEARL)) {
			e.setCancelled(true);
			p.sendMessage(ChatColor.DARK_RED + "You cannot teleport while tagged!");
		}
	}
}
