package net.urbanmc.treasurehunter.listener;

import net.ess3.api.events.GodStatusChangeEvent;
import net.urbanmc.treasurehunter.TreasureHunter;
import net.urbanmc.treasurehunter.manager.ConfigManager;
import net.urbanmc.treasurehunter.manager.Messages;
import net.urbanmc.treasurehunter.manager.TreasureChestManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GodListener implements Listener {

	@EventHandler
	public void onGodEnable(GodStatusChangeEvent e) {
		if (TreasureChestManager.getInstance().getCurrentChest() == null)
			return;

		Player p = e.getAffected().getBase();

		if (!TreasureChestManager.getInstance().getCurrentChest().isHunting(p))
			return;

		if (!ConfigManager.getConfig().getBoolean("disable-god"))
			return;

		TreasureHunter.getEssentials().getUser(p).setGodModeEnabled(false);

		p.sendMessage(Messages.getString("hunting.no-god"));

		e.setCancelled(true);
	}

}
