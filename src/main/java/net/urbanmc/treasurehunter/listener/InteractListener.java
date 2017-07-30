package net.urbanmc.treasurehunter.listener;

import net.urbanmc.treasurehunter.manager.Messages;
import net.urbanmc.treasurehunter.manager.TreasureChestManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractListener implements Listener{

    @EventHandler
    public void onChestInteract(PlayerInteractEvent e) {

        if(TreasureChestManager.getInstance().getCurrentChest() == null)
            return;

        if(e.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        if(e.getClickedBlock().getType() != Material.CHEST)
            return;

        if(e.getClickedBlock() != TreasureChestManager.getInstance().getCurrentChest().getBlock())
            return;

        Bukkit.broadcastMessage(Messages.getString("broadcast.found", e.getPlayer().getDisplayName(), TreasureChestManager.getInstance().getCurrentChest().getType().toString()));


        //Well Idk if you want to leave the chest or make it when they click the chest, it vanishes and all the items drop on the ground.
        TreasureChestManager.getInstance().setCurrentChest(null);

    }

}
