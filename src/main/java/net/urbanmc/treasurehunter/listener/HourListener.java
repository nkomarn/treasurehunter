package net.urbanmc.treasurehunter.listener;

import me.Silverwolfg11.TimingLib.events.NewHourEvent;
import net.urbanmc.treasurehunter.TreasureHunter;
import net.urbanmc.treasurehunter.runnable.SpawnTask;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HourListener implements Listener {

    @EventHandler
    public void onHourEvent(NewHourEvent event) {
        if (!TreasureHunter.getInstance().isError()) {
            new SpawnTask().run();
        } else {
            TreasureHunter.getInstance().throwError();
        }
    }

}
