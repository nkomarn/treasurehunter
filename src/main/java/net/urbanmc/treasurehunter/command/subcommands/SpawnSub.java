package net.urbanmc.treasurehunter.command.subcommands;

import com.google.common.base.Enums;
import net.urbanmc.treasurehunter.TreasureHunter;
import net.urbanmc.treasurehunter.object.Permission;
import net.urbanmc.treasurehunter.object.SubCommand;
import net.urbanmc.treasurehunter.object.TreasureChest;
import net.urbanmc.treasurehunter.runnable.SpawnTask;
import org.bukkit.command.CommandSender;

public class SpawnSub extends SubCommand {

	public SpawnSub() {
		super("spawn", Permission.SPAWN_SUB, false, false);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		TreasureHunter plugin = TreasureHunter.getInstance();

		if (plugin.isError()) {
			sendPropMessage(sender, "command.spawn.error_on_startup");
			plugin.throwError();
			return;
		}

		var desiredType = args.length >= 1
				? Enums.getIfPresent(TreasureChest.TreasureChestType.class, args[1].toUpperCase()).orNull()
				: null;

		new SpawnTask(desiredType).run();

        /* Basically this command is for admins to force spawn a treasure chest.
        I guess you could parse some arguments about chest type but mainly just force spawning a chest.
        */
	}
}
