package net.urbanmc.treasurehunter.command.subcommands;

import net.urbanmc.treasurehunter.manager.ItemManager;
import net.urbanmc.treasurehunter.manager.Messages;
import net.urbanmc.treasurehunter.object.Permission;
import net.urbanmc.treasurehunter.object.SubCommand;
import net.urbanmc.treasurehunter.object.TreasureChest;
import org.bukkit.command.CommandSender;

public class ChestsSub extends SubCommand{


    public ChestsSub() {
        super("chests", Permission.CHESTS_SUB, false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        sendPropMessage(sender, Messages.getString("command.chests.percentages",
                ItemManager.getInstance().getPercentage(TreasureChest.TreasureChestType.COMMON),
                ItemManager.getInstance().getPercentage(TreasureChest.TreasureChestType.RARE),
                ItemManager.getInstance().getPercentage(TreasureChest.TreasureChestType.EPIC),
                ItemManager.getInstance().getPercentage(TreasureChest.TreasureChestType.LEGENDARY)));
    }
}
