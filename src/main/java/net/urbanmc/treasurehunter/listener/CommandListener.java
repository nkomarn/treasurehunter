package net.urbanmc.treasurehunter.listener;

import net.urbanmc.treasurehunter.manager.ConfigManager;
import net.urbanmc.treasurehunter.manager.Messages;
import net.urbanmc.treasurehunter.manager.TreasureChestManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		if (TreasureChestManager.getInstance().getCurrentChest() == null)
			return;

		if (!TreasureChestManager.getInstance().getCurrentChest().isHunting(e.getPlayer()))
			return;

		if (ConfigManager.getInstance().getBlockedCommands().isEmpty())
			return;

		String label = e.getMessage().split(" ")[0];

		boolean cancel = false;

		for (String command : ConfigManager.getInstance().getBlockedCommands()) {
			if (!command.isEmpty() && command.equalsIgnoreCase(label)) {
				cancel = true;
				break;
			}

			if (command.equalsIgnoreCase(e.getMessage())) {
				cancel = true;
				break;
			}
		}

		if (!cancel)
			return;

		e.setCancelled(true);

		e.getPlayer().sendMessage(Messages.getString("hunting.blocked-command"));
	}

}
