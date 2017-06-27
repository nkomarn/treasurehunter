package net.urbanmc.treasurehunter.object;

import net.urbanmc.treasurehunter.manager.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class SubCommand {

    private String sub;
    private Permission perm;
    private boolean isPlayerOnly;
    private String alias;

    public SubCommand(String sub, Permission perm, boolean playerOnly, String... alias) {
        this.sub = sub;
        this.perm = perm;
        this.isPlayerOnly = playerOnly;

        if(alias != null)
            this.alias = alias[0];
    }


    public void preCommand(CommandSender sender, String[] args) {
        if(!(sender instanceof Player) && isPlayerOnly) {
            sendPropMessage(sender, "command.player-only");
            return;
        }

        if(!sender.hasPermission(perm.toString())) {
            sendPropMessage(sender, "command.no-perm");
            return;
        }

        execute(sender, args);
    }

    public abstract void execute(CommandSender sender, String[] args);


    public void sendPropMessage(CommandSender sender, String property) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(Messages.getString(property));
            return;
        }

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Messages.getString(property)));
    }


    public boolean matchSub(String match) {

        if(sub.equalsIgnoreCase(match))
            return true;

        if(alias != null)
            if(match.equalsIgnoreCase(alias))
                return true;

        return false;
    }

}
