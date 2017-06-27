package net.urbanmc.treasurehunter.command.subcommands;

import net.urbanmc.treasurehunter.object.Permission;
import net.urbanmc.treasurehunter.object.SubCommand;
import org.bukkit.command.CommandSender;

public class CancelSub extends SubCommand{


    public CancelSub() {
        super("cancel", Permission.CANCEL_SUB, true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        //Todo Allows the player to cancel. Takes away their compass. They cannot start the hunt again.
    }
}
