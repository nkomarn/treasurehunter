package net.urbanmc.treasurehunter.command.subcommands;

import net.urbanmc.treasurehunter.TreasureHunter;
import net.urbanmc.treasurehunter.manager.Messages;
import net.urbanmc.treasurehunter.manager.TreasureChestManager;
import net.urbanmc.treasurehunter.object.Permission;
import net.urbanmc.treasurehunter.object.SubCommand;
import net.urbanmc.treasurehunter.object.TreasureChest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CancelSub extends SubCommand {

	public CancelSub() {
		super("cancel", Permission.CANCEL_SUB, true, true);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		TreasureChest chest = TreasureChestManager.getInstance().getCurrentChest();
		Player p = (Player) sender;

		if (chest == null || !chest.isHunting(p)) {
			sender.sendMessage(Messages.getString("command.cancel.not-hunting"));
			return;
		}

		chest.getHunting().remove(p.getUniqueId());

		if (p.getInventory().contains(StartSub.compass)) {
			p.getInventory().remove(StartSub.compass);
		}

		p.setCompassTarget(p.getWorld().getSpawnLocation());
		removeCompass(p);

		chest.getCancelled().add(p.getUniqueId());
		TreasureChestManager.getInstance().saveChest();

		TreasureHunter.getInstance().getViewDistanceUtil().resetPlayerViewDistance(p);

		sender.sendMessage(Messages.getString("command.cancel.cancelled"));
	}

	private void removeCompass(Player p) {
		p.getInventory().removeItem(StartSub.compass);
	}
}
