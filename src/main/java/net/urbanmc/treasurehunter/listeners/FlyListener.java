package net.urbanmc.treasurehunter.listeners;

import net.urbanmc.treasurehunter.TreasureHunter;
import net.urbanmc.treasurehunter.manager.Messages;
import net.urbanmc.treasurehunter.manager.TreasureChestManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class FlyListener implements Listener{

    @EventHandler
    public void onFly (PlayerToggleFlightEvent e) {

        if(TreasureChestManager.getInstance().getCurrentChest() == null)
            return;

        if(!TreasureChestManager.getInstance().getCurrentChest().getHunting().contains(e.getPlayer()))
            return;

        e.getPlayer().setFlying(false);

        e.getPlayer().sendMessage(Messages.getString("hunting.no-fly"));

        e.setCancelled(true);
    }

}
