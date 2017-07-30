package net.urbanmc.treasurehunter.command;

import net.urbanmc.treasurehunter.TreasureHunter;
import net.urbanmc.treasurehunter.command.subcommands.*;
import net.urbanmc.treasurehunter.manager.Messages;
import net.urbanmc.treasurehunter.object.Permission;
import net.urbanmc.treasurehunter.object.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class THCommand implements CommandExecutor {

    private ArrayList<SubCommand> subList;
    private TreasureHunter plugin;

    public THCommand(TreasureHunter plugin) {
        this.plugin = plugin;
        registerSubs();
    }

    private void registerSubs() {
        subList.add(new StartSub(plugin));
        subList.add(new SpawnSub(plugin));
        subList.add(new CancelSub());
        subList.add(new ChestsSub());
        subList.add(new TpSub());
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission(Permission.COMMAND_BASE.toString())) {
            sendPropMessage(sender, "command.no-perm");
            return true;
        }

        if (args.length == 0) {

            if(sender.hasPermission(Permission.ADMIN.toString()))
                sendPropMessage(sender, "command.base.help.admin");

            else
                sendPropMessage(sender, "command.base.help.default");

            return true;
        }

        SubCommand sub = matchSub(args[0]);

        if (sub == null) {
            sendPropMessage(sender, "command.invalid-sub");
            return true;
        }

        sub.preCommand(sender, args);

        return true;
    }


    public void sendPropMessage(CommandSender sender, String property) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.getString(property));
            return;
        }

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Messages.getString(property)));
    }

    private SubCommand matchSub(String args0) {

        for(SubCommand sub : subList)
            if(sub.matchSub(args0))
                return sub;

        return null;
    }
}
