package net.urbanmc.treasurehunter.gson;

import com.google.gson.*;
import net.urbanmc.treasurehunter.object.TreasureChest;
import net.urbanmc.treasurehunter.object.TreasureChest.TreasureChestType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

		JsonArray hunting = new JsonArray(), cancelled = new JsonArray();

		for (UUID id : chest.getHunting()) {
			hunting.add(id.toString());
		}

		for (UUID id : chest.getCancelled()) {
			cancelled.add(id.toString());
		}

		obj.add("hunting", hunting);
		obj.add("cancelled", cancelled);

		obj.addProperty("found", chest.isFound());

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

		List<UUID> hunting = new ArrayList<>(), cancelled = new ArrayList<>();

		for (JsonElement je : obj.getAsJsonArray("hunting")) {
			hunting.add(UUID.fromString(je.getAsString()));
		}

		for (JsonElement je : obj.getAsJsonArray("cancelled")) {
			cancelled.add(UUID.fromString(je.getAsString()));
		}

		boolean found = obj.get("found").getAsBoolean();

		TreasureChest chest = new TreasureChest(type, b);

		chest.getHunting().addAll(hunting);
		chest.getCancelled().addAll(cancelled);

		if (found) {
			chest.setFound();
		}

		return chest;
	}
}
