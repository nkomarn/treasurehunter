package net.urbanmc.treasurehunter.command.subcommands;

import net.urbanmc.treasurehunter.object.Permission;
import net.urbanmc.treasurehunter.object.SubCommand;
import org.bukkit.command.CommandSender;

public class SpawnSub extends SubCommand{
    public SpawnSub() {
        super("spawn", Permission.SPAWN_SUB, false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        //Todo Basically this command is for admins to force spawn a treasure chest. I guess you could parse some arguments about chest type but mainly just force spawning a chest.


    }
}
