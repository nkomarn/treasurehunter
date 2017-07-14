package net.urbanmc.treasurehunter.command.subcommands;

import net.urbanmc.treasurehunter.TreasureHunter;
import net.urbanmc.treasurehunter.manager.Messages;
import net.urbanmc.treasurehunter.manager.TreasureChestManager;
import net.urbanmc.treasurehunter.object.Permission;
import net.urbanmc.treasurehunter.object.SubCommand;
import net.urbanmc.treasurehunter.object.TreasureChest;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CancelSub extends SubCommand{

    public CancelSub() {
        super("cancel", Permission.CANCEL_SUB, true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        //Todo Allows the player to cancel. Takes away their compass. They cannot start the hunt again.

        if(TreasureChestManager.getInstance().getCurrentChest() == null) {
            sender.sendMessage(Messages.getString("command.no-chest"));
            return;
        }

        TreasureChest chest = TreasureChestManager.getInstance().getCurrentChest();

        Player p = (Player) sender;

        if(!chest.getHunting().contains(p.getUniqueId())) {
            sender.sendMessage(Messages.getString("command.cancel.not-hunting"));
            return;
        }

        chest.getHunting().remove(p.getUniqueId());

        p.getInventory().remove(StartSub.compass);

        p.setCompassTarget(p.getWorld().getSpawnLocation());

        sender.sendMessage(Messages.getString("command.cancel.cancelled"));
    }
}
