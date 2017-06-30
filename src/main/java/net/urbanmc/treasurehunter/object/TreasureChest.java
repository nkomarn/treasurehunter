package net.urbanmc.treasurehunter.object;

import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TreasureChest {

	private TreasureChestType type;
	private Block block;
	private List<UUID> hunting, cancelled;

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

	public List<UUID> getCancelled() {
		return cancelled;
	}

	public enum TreasureChestType {
		COMMON, RARE, EPIC, LEGENDARY
	}
}
