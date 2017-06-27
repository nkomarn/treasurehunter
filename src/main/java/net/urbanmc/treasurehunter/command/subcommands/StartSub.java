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

        /* Todo So in my failure of a plugin I made a warning message that people had to accept before they could recieve the cordinates (no cords this time).
        Keep the warning message and when they accept give them A COMPASS. You should also figure out how to make a compass point to the treasure chest's location
        Reason for Compass: So many people will give away cords and many diamond donators will just fly to the area. (But remember once they start they cannot use /fly or /god).*/

    }
}
