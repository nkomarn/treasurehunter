package net.urbanmc.treasurehunter.command.subcommands;

import net.urbanmc.treasurehunter.object.Permission;
import net.urbanmc.treasurehunter.object.SubCommand;
import org.bukkit.command.CommandSender;

public class StartSub extends SubCommand{


    public StartSub() {
        super("start", Permission.START_SUB, true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

    }
}
