package net.urbanmc.treasurehunter.command.subcommands;

import net.urbanmc.treasurehunter.TreasureHunter;
import net.urbanmc.treasurehunter.manager.ConfigManager;
import net.urbanmc.treasurehunter.manager.ItemManager;
import net.urbanmc.treasurehunter.manager.SpawnManager;
import net.urbanmc.treasurehunter.object.Permission;
import net.urbanmc.treasurehunter.object.SubCommand;
import org.bukkit.command.CommandSender;

public class ReloadSub extends SubCommand{

    private TreasureHunter plugin;

    public ReloadSub(TreasureHunter plugin) {
        super("reload", Permission.RELOAD, false, false);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ConfigManager.getInstance().reloadConfig();
        SpawnManager.getInstance().reloadConfig();
        ItemManager.getInstance().checkError(plugin);

        sendPropMessage(sender, "command.reload");
    }
}
