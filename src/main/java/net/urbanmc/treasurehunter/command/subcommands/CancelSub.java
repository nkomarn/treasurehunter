package net.urbanmc.treasurehunter.command.subcommands;

import net.urbanmc.treasurehunter.manager.Messages;
import net.urbanmc.treasurehunter.manager.TreasureChestManager;
import net.urbanmc.treasurehunter.object.Permission;
import net.urbanmc.treasurehunter.object.SubCommand;
import net.urbanmc.treasurehunter.object.TreasureChest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CancelSub extends SubCommand{

    public CancelSub() {
        super("cancel", Permission.CANCEL_SUB, true, true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        //Todo Allows the player to cancel. Takes away their compass. They cannot start the hunt again.
        TreasureChest chest = TreasureChestManager.getInstance().getCurrentChest();

        Player p = (Player) sender;

        if(!chest.getHunting().contains(p.getUniqueId())) {
            sender.sendMessage(Messages.getString("command.cancel.not-hunting"));
            return;
        }

        chest.getHunting().remove(p.getUniqueId());

        if(p.getInventory().contains(StartSub.compass))
        p.getInventory().remove(StartSub.compass);

        p.setCompassTarget(p.getWorld().getSpawnLocation());

        sender.sendMessage(Messages.getString("command.cancel.cancelled"));
    }
}
