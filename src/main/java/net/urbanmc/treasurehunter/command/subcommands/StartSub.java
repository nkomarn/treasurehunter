package net.urbanmc.treasurehunter.command.subcommands;

import net.urbanmc.treasurehunter.manager.Messages;
import net.urbanmc.treasurehunter.manager.TreasureChestManager;
import net.urbanmc.treasurehunter.object.Permission;
import net.urbanmc.treasurehunter.object.SubCommand;
import net.urbanmc.treasurehunter.object.TreasureChest;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class StartSub extends SubCommand{

    private List<UUID> warned = new ArrayList<>();

    public static ItemStack compass;

    public StartSub() {
        super("start", Permission.START_SUB, true);
        createCompass();
    }

    private void createCompass() {
        compass = new ItemStack(Material.COMPASS, 1);

        ItemMeta meta = compass.getItemMeta();

        meta.setDisplayName(Messages.getString("compass.name"));
        meta.setLore(Collections.singletonList(Messages.getString("compass.desc")));

        compass.setItemMeta(meta);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(TreasureChestManager.getInstance().getCurrentChest() == null) {
            sender.sendMessage(Messages.getString("command.no-chest"));
            return;
        }

        TreasureChest chest = TreasureChestManager.getInstance().getCurrentChest();
        Player p = (Player) sender;

        if(chest.getCancelled().contains(p.getUniqueId())) {
            sender.sendMessage(Messages.getString("command.start.cancelled"));
            return;
        }

        if(chest.getHunting().contains(p.getUniqueId())) {
            //Todo Give them another compass maybe or just deny it...
            return;
        }

        if(!warned.contains(p.getUniqueId())) {
            sender.sendMessage(Messages.getString("command.start.warning"));

            warned.add(p.getUniqueId());
            return;
        }

        warned.remove(p.getUniqueId());

        chest.getHunting().add(p.getUniqueId());

        sender.sendMessage(Messages.getString("command.start.start-hunt"));

        p.getInventory().addItem(compass.clone());
    }
}
