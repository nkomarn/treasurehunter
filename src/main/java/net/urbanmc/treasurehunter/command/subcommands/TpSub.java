package net.urbanmc.treasurehunter.command.subcommands;

import net.urbanmc.treasurehunter.manager.TreasureChestManager;
import net.urbanmc.treasurehunter.object.Permission;
import net.urbanmc.treasurehunter.object.SubCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpSub extends SubCommand {


	public TpSub() {
		super("teleport", Permission.TP_SUB, true, true, "tp");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Location loc = TreasureChestManager.getInstance().getCurrentChest().getBlock().getLocation();

		loc.add(0, 2, 0);

		((Player) sender).teleport(loc);

		sendPropMessage(sender, "command.tp");
	}
}
