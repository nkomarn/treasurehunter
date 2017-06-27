package net.urbanmc.treasurehunter.command;

import net.urbanmc.treasurehunter.command.subcommands.StartSub;
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

    public THCommand() {
        registerSubs();
    }

    private void registerSubs() {
        subList.add(new StartSub());
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission(Permission.COMMAND_BASE.toString())) {
            sendPropMessage(sender, "command.no-perm");
            return true;
        }

        if (args.length == 0) {
            sendPropMessage(sender, "command.base.help");
            return true;
        }

        SubCommand sub = matchSub(args[0]);

        if (sub == null) {
            sendPropMessage(sender, "command.invalid-sub");
            return true;
        }

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
        

        return null;
    }
}
