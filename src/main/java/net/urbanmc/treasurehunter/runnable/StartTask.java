package net.urbanmc.treasurehunter.runnable;

import net.urbanmc.treasurehunter.TreasureHunter;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class StartTask extends BukkitRunnable {

	private TreasureHunter plugin;

	public StartTask(TreasureHunter plugin) {
		this.plugin = plugin;

		long delay = calculateDelay();

		runTaskLater(plugin, delay);
	}

	@Override
	public void run() {
		SpawnTask.start(plugin);
	}

	private long calculateDelay() {
		GregorianCalendar cal = new GregorianCalendar();

		int hour = cal.get(Calendar.HOUR_OF_DAY);

		GregorianCalendar newCal = new GregorianCalendar();

		newCal.set(Calendar.HOUR_OF_DAY, hour + 1);
		newCal.set(Calendar.MINUTE, 0);
		newCal.set(Calendar.SECOND, 0);

		long millisTime = newCal.getTimeInMillis() - cal.getTimeInMillis();

		long secondsTime = TimeUnit.MILLISECONDS.toSeconds(millisTime);

		return secondsTime * 20;
	}
}
