package net.urbanmc.treasurehunter.listeners;

import net.urbanmc.treasurehunter.command.subcommands.StartSub;
import net.urbanmc.treasurehunter.manager.Messages;
import net.urbanmc.treasurehunter.manager.TreasureChestManager;
import net.urbanmc.treasurehunter.object.TreasureChest;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public class CompassListener implements Listener{

    //TODO Event not registered in main class

    @EventHandler
    public void onCompassClick(PlayerInteractEvent e) {

        if(TreasureChestManager.getInstance().getCurrentChest() == null)
            return;

        if(!e.getPlayer().getInventory().getItemInMainHand().equals(StartSub.compass) && !e.getPlayer().getInventory().getItemInOffHand().equals(StartSub.compass))
            return;

        e.setCancelled(true);

        if(!TreasureChestManager.getInstance().getCurrentChest().getHunting().contains(e.getPlayer().getUniqueId())) {
            e.getPlayer().sendMessage(Messages.getString("compass.invalid-finder"));

            e.getPlayer().getInventory().remove(StartSub.compass);
            return;
        }

        e.getPlayer().setCompassTarget(TreasureChestManager.getInstance().getCurrentChest().getBlock().getLocation());
    }

}
