package net.urbanmc.treasurehunter.object;

import org.bukkit.block.Block;

public class TreasureChest {

	private TreasureChestType type;
	private Block block;

	public TreasureChest(TreasureChestType type, Block block) {
		this.type = type;
		this.block = block;
	}

	public TreasureChestType getType() {
		return type;
	}

	public Block getBlock() {
		return block;
	}

	public enum TreasureChestType {
		COMMON, RARE, EPIC, LEGENDARY
	}
}
