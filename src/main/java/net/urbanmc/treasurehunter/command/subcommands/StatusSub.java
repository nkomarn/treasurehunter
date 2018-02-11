package net.urbanmc.treasurehunter.command.subcommands;

import net.urbanmc.treasurehunter.manager.Messages;
import net.urbanmc.treasurehunter.manager.TreasureChestManager;
import net.urbanmc.treasurehunter.object.Permission;
import net.urbanmc.treasurehunter.object.SubCommand;
import net.urbanmc.treasurehunter.object.TreasureChest;
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

		TreasureChest chest = TreasureChestManager.getInstance().getCurrentChest();

		String hunters = "";

		if(!chest.isFound())
			hunters = (chest.getHunting().isEmpty() ? "No" : chest.getHunting().size()) + " players are hunting the chest!";


		String message = Messages.getString("command.status", chest.getType().getDisplayName(),
				chest.isFound() ? "" : " not", hunters);
		if (sender instanceof Player)
			sender.sendMessage(message);
		else
			sender.sendMessage(ChatColor.stripColor(message));

	}
}
