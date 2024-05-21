package net.urbanmc.treasurehunter.listener;

import net.urbanmc.treasurehunter.manager.ConfigManager;
import net.urbanmc.treasurehunter.manager.Messages;
import net.urbanmc.treasurehunter.manager.TreasureChestManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class FlyListener implements Listener {

	@EventHandler
	public void onFlyToggled(PlayerToggleFlightEvent e) {
		if (TreasureChestManager.getInstance().getCurrentChest() == null)
			return;

		Player p = e.getPlayer();

		if (!TreasureChestManager.getInstance().getCurrentChest().isHunting(p))
			return;

		if (!ConfigManager.getConfig().getBoolean("disable-fly"))
			return;

		p.setFlying(false);
		p.setAllowFlight(false);

		p.sendMessage(Messages.getString("hunting.no-fly"));

		e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void useElytra(EntityToggleGlideEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		if (TreasureChestManager.getInstance().getCurrentChest() == null)
			return;

		if (!TreasureChestManager.getInstance().getCurrentChest().isHunting(player))
			return;

		EntityEquipment equipment = player.getEquipment();
		ItemStack elytraItem = equipment.getItem(EquipmentSlot.CHEST);
		equipment.setItem(EquipmentSlot.CHEST, null);

		/* add the elytra to the player's inventory, or drop it on the ground */
		player.getInventory().addItem(elytraItem).forEach((index, stack) -> {
			player.getWorld().dropItemNaturally(player.getLocation(), elytraItem).setOwner(player.getUniqueId());
		});
	}
}
