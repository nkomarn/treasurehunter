package net.urbanmc.treasurehunter.gson;

import com.google.gson.*;
import net.urbanmc.treasurehunter.object.TreasureChest;
import net.urbanmc.treasurehunter.object.TreasureChest.TreasureChestType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.lang.reflect.Type;

public class TreasureChestSerializer implements JsonSerializer<TreasureChest>, JsonDeserializer<TreasureChest> {

	public JsonElement serialize(TreasureChest chest, Type t, JsonSerializationContext context) {
		JsonObject obj = new JsonObject();

		obj.addProperty("type", chest.getType().name());

		JsonObject blockObj = new JsonObject();
		Block b = chest.getBlock();

		blockObj.addProperty("x", b.getX());
		blockObj.addProperty("y", b.getY());
		blockObj.addProperty("z", b.getZ());
		blockObj.addProperty("world", b.getWorld().getName());

		obj.add("block", blockObj);

		return obj;
	}

	public TreasureChest deserialize(JsonElement element, Type t,
	                                 JsonDeserializationContext context) throws JsonParseException {
		JsonObject obj = (JsonObject) element;

		TreasureChestType type = TreasureChestType.valueOf(obj.get("type").getAsString());

		JsonObject blockObj = obj.getAsJsonObject("block");

		int x = blockObj.get("x").getAsInt(), y = blockObj.get("y").getAsInt(), z = blockObj.get("z").getAsInt();
		String worldName = blockObj.get("world").getAsString();

		World world = Bukkit.getWorld(worldName);

		if (world == null)
			throw new IllegalStateException("World for current chest is not loaded!");

		Block b = world.getBlockAt(x, y, z);

		return new TreasureChest(type, b);
	}
}
