package net.urbanmc.treasurehunter.object;

import net.urbanmc.treasurehunter.manager.Messages;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TreasureChest {

	private TreasureChestType type;
	private Block block;
	private List<UUID> hunting, cancelled;
	private boolean found;

	public TreasureChest(TreasureChestType type, Block block) {
		this.type = type;
		this.block = block;
		hunting = new ArrayList<>();
		cancelled = new ArrayList<>();
	}

	public TreasureChestType getType() {
		return type;
	}

	public Block getBlock() {
		return block;
	}

	public List<UUID> getHunting() {
		return hunting;
	}

	public boolean isHunting(Player p) {
		return hunting.contains(p.getUniqueId());
	}

	public List<UUID> getCancelled() {
		return cancelled;
	}

	public boolean isFound() {
		return found;
	}

	public void setFound() {
		this.found = true;
	}

	public enum TreasureChestType {
		COMMON, RARE, EPIC, LEGENDARY;

		public String getDisplayName() {
			return Messages.getString("chest." + name().toLowerCase());
		}
	}
}
