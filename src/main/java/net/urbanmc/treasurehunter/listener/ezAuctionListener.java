package net.urbanmc.treasurehunter.listener;

import net.urbanmc.ezauctions.event.AuctionQueueEvent;
import net.urbanmc.treasurehunter.command.subcommands.StartSub;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ezAuctionListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onAuctionStart(AuctionQueueEvent event) {

        try {
            if (event.getAuction().getItem().equals(StartSub.compass)) {
                event.getAuction().getAuctioneer().getOnlinePlayer().sendMessage(
                        ChatColor.RED + "You can't auction this item!"
                );
                event.setCancelled(true);
            }
        } catch (Exception ex) {
        }

    }

}
