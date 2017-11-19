package net.urbanmc.treasurehunter.command.subcommands;

import net.urbanmc.treasurehunter.manager.TreasureChestManager;
import net.urbanmc.treasurehunter.object.Permission;
import net.urbanmc.treasurehunter.object.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatusSub extends SubCommand {

	public StatusSub() {
		super("status", Permission.STATUS, false, false);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {

		if (TreasureChestManager.getInstance().getCurrentChest() == null) {
			sendPropMessage(sender, "command.no-chest");
			return;
		}

		String message = ChatColor.GREEN + "The " +
				TreasureChestManager.getInstance().getCurrentChest().getType().toString().toLowerCase() + " chest" +
				(TreasureChestManager.getInstance().getCurrentChest()
						.isFound() ? " has been found" : "has not been found") + "!";

		if (sender instanceof Player)
			sender.sendMessage(message);
		else
			sender.sendMessage(ChatColor.stripColor(message));

	}
}
