package net.urbanmc.treasurehunter.command.subcommands;

import net.urbanmc.treasurehunter.TreasureHunter;
import net.urbanmc.treasurehunter.manager.ConfigManager;
import net.urbanmc.treasurehunter.manager.Messages;
import net.urbanmc.treasurehunter.manager.TreasureChestManager;
import net.urbanmc.treasurehunter.object.Permission;
import net.urbanmc.treasurehunter.object.SubCommand;
import net.urbanmc.treasurehunter.object.TreasureChest;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class StartSub extends SubCommand {

	public static ItemStack compass;
	private List<UUID> warned = new ArrayList<>();
	private TreasureHunter plugin;

	public StartSub(TreasureHunter plugin) {
		super("start", Permission.START_SUB, true, false);

		this.plugin = plugin;

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
		TreasureChest chest = TreasureChestManager.getInstance().getCurrentChest();
		Player p = (Player) sender;

		if (chest == null) {
			sendPropMessage(p, "command.start.none");
			return;
		}

		if (chest.getCancelled().contains(p.getUniqueId())) {
			sendPropMessage(p, "command.start.cancelled");
			return;
		}

		if (chest.isHunting(p)) {
			sendPropMessage(p, "command.start.hunting");
			return;
		}

		if (!warned.contains(p.getUniqueId())) {
			sendPropMessage(p, "command.start.warning");

			warned.add(p.getUniqueId());

			timeOut(p.getUniqueId());
			return;
		}

		if (!checkSpace(p, compass)) {
			sendPropMessage(p, "command.start.no_space");
			return;
		}

		start(p, chest);
	}

	private void timeOut(UUID p) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			if (warned.contains(p))
				warned.remove(p);
		}, 500);
	}

	private void start(Player p, TreasureChest chest) {
		warned.remove(p.getUniqueId());

		p.teleport(Bukkit.getWorld(ConfigManager.getConfig().getString("world")).getSpawnLocation());

		chest.getHunting().add(p.getUniqueId());
		TreasureChestManager.getInstance().saveChest();

		sendPropMessage(p, "command.start.start-hunt");

		p.getInventory().addItem(compass.clone());
		p.setCompassTarget(chest.getBlock().getLocation());

		if (ConfigManager.getConfig().getBoolean("disable-fly")) {
			p.setFlying(false);
		}

		if (ConfigManager.getConfig().getBoolean("disable-god")) {
			TreasureHunter.getEssentials().getUser(p).setGodModeEnabled(false);
		}
	}

	@SuppressWarnings("deprecation")
	private boolean checkSpace(Player p, ItemStack is) {
		if (p.getInventory().firstEmpty() < 36 && p.getInventory().firstEmpty() != -1)
			return true;

		for (ItemStack item : p.getInventory()) {
			if (item == null)
				continue;

			if (item.getType() != is.getType())
				continue;

			if (item.getData().getData() != is.getData().getData())
				continue;

			if (item.getAmount() + is.getAmount() > 64)
				continue;

			return true;
		}

		return false;
	}
}
