package net.urbanmc.treasurehunter.object;

import java.util.List;

public class TreasureChestList {

	private List<TreasureChest> chest;

	public TreasureChestList(List<TreasureChest> chest) {
		this.chest = chest;
	}

	public List<TreasureChest> getChest() {
		return chest;
	}
}
