package net.urbanmc.treasurehunter.object;

import org.bukkit.block.Block;

public class TreasureChest {

	private Block block;

	private TreasureChest(Block block) {
		this.block = block;
	}

	public Block getBlock() {
		return block;
	}
}
