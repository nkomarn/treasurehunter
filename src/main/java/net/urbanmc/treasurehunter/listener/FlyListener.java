package net.urbanmc.treasurehunter.listener;

import net.urbanmc.treasurehunter.manager.ConfigManager;
import net.urbanmc.treasurehunter.manager.Messages;
import net.urbanmc.treasurehunter.manager.TreasureChestManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class FlyListener implements Listener {

	@EventHandler
	public void onFlyToggled(PlayerToggleFlightEvent e) {
		if (TreasureChestManager.getInstance().getCurrentChest() == null)
			return;

		Player p = e.getPlayer();

		if (!TreasureChestManager.getInstance().getCurrentChest().isHunting(p))
			return;

		if (!ConfigManager.getConfig().getBoolean("disable-fly"))
			return;

		p.setFlying(false);
		p.setAllowFlight(false);

		p.sendMessage(Messages.getString("hunting.no-fly"));

		e.setCancelled(true);
	}

}
