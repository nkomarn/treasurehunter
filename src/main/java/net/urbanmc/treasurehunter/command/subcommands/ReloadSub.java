package net.urbanmc.treasurehunter.command.subcommands;

import net.urbanmc.treasurehunter.manager.ConfigManager;
import net.urbanmc.treasurehunter.manager.SpawnManager;
import net.urbanmc.treasurehunter.object.Permission;
import net.urbanmc.treasurehunter.object.SubCommand;
import org.bukkit.command.CommandSender;

public class ReloadSub extends SubCommand{


    public ReloadSub() {
        super("reload", Permission.RELOAD, false, false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ConfigManager.getInstance().reloadConfig();
        SpawnManager.getInstance().reloadConfig();

        sendPropMessage(sender, "command.reload");
    }
}
