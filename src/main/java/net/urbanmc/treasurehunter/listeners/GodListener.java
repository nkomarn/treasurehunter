package net.urbanmc.treasurehunter.listeners;

import net.ess3.api.events.GodStatusChangeEvent;
import net.urbanmc.treasurehunter.TreasureHunter;
import net.urbanmc.treasurehunter.manager.Messages;
import net.urbanmc.treasurehunter.manager.TreasureChestManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GodListener implements Listener{

    @EventHandler
    public void onGodEnable(GodStatusChangeEvent e) {
        if(TreasureChestManager.getInstance().getCurrentChest() == null)
            return;

        if(!TreasureChestManager.getInstance().getCurrentChest().getHunting().contains(e.getAffected().getBase()))
            return;

        TreasureHunter.getEssentials().getUser(e.getAffected().getBase()).setGodModeEnabled(false);

        e.getAffected().sendMessage(Messages.getString("hunting.no-god"));

        e.setCancelled(true);
    }

}
