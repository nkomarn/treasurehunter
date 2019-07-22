package net.urbanmc.treasurehunter.manager;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.TownyWorld;
import net.urbanmc.treasurehunter.object.TreasureChest.TreasureChestType;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class SpawnManager {

	private static SpawnManager instance = new SpawnManager();

	private final File FILE = new File("plugins/TreasureHunter", "spawns.yml");
	private FileConfiguration data;

	private SpawnManager() {
		createFile();
		loadConfig();
	}

	public static SpawnManager getInstance() {
		return instance;
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	private void createFile() {
		if (!FILE.getParentFile().exists()) {
			FILE.getParentFile().mkdir();
		}

		if (!FILE.exists()) {
			try {
				FILE.createNewFile();

				InputStream input = getClass().getClassLoader().getResourceAsStream("spawns.yml");

				Files.copy(input, FILE.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadConfig() {
		data = YamlConfiguration.loadConfiguration(FILE);
	}

	public void reloadConfig() {
		data = YamlConfiguration.loadConfiguration(FILE);
	}

	public Location generateLocation(World world, TreasureChestType type) {
		String prefix = type.name().toLowerCase() + ".";
		Location loc = null;
		boolean cont = true;
		int timeout = 0;
		Random random = ThreadLocalRandom.current();
		int xRange = data.getInt(prefix + "range-x");
		int zRange = data.getInt(prefix + "range-z");
		int chunkRange = data.getInt(prefix + "range-chunk");
		int noZoneX = data.getInt(prefix + "nozone-x");
		int noZoneZ = data.getInt(prefix + "nozone-z");
		int noZoneNegativeX = 0 - noZoneX;
		int noZoneNegativeZ = 0 - noZoneZ;

		do {
			++timeout;
			if (timeout == 50) {
				return null;
			}

			int x = random.nextInt(xRange * 2) - xRange;
			int y = 256;
			int z = random.nextInt(zRange * 2) - zRange;

			//Check that x and z are not within the no-zone
			if ((x < noZoneNegativeX || x > noZoneX) && (z < noZoneNegativeZ || z > noZoneZ)) {
				//Generate a new location
				loc = new Location(world, (double) x, (double) y, (double) z);
				if (chunkRange > 0) {
					Chunk locChunk = loc.getChunk();
					int cx = locChunk.getX();
					int cz = locChunk.getZ();
					ArrayList<Chunk> borders = new ArrayList<>();
					borders.add(locChunk);

					for (int i = 1; i < chunkRange + 1; ++i) {
						borders.add(world.getChunkAt(cx + i, cz));
						borders.add(world.getChunkAt(cx - i, cz));
						borders.add(world.getChunkAt(cx, cz + i));
						borders.add(world.getChunkAt(cx, cz - i));
						borders.add(world.getChunkAt(cx + i, cz + i));
						borders.add(world.getChunkAt(cx + i, cz - i));
						borders.add(world.getChunkAt(cx - i, cz - i));
						borders.add(world.getChunkAt(cx - i, cz + i));
					}

					boolean nearTown = false;

					TownyWorld townyWorld;
					try {
						townyWorld = TownyUniverse.getDataSource().getWorld(world.getName());
					} catch (NotRegisteredException var24) {
						return null;
					}

					Iterator var21 = borders.iterator();

					while (var21.hasNext()) {
						Chunk chunk = (Chunk) var21.next();
						Coord key = new Coord(chunk.getX(), chunk.getZ());
						if (townyWorld.hasTownBlock(key)) {
							nearTown = true;
							break;
						}
					}

					if (nearTown) {
						continue;
					}

					if(world.getWorldBorder() != null) {
						WorldBorder worldBorder = world.getWorldBorder();
						if (worldBorder.getSize() > 0) {
							if (!worldBorder.isInside(loc))
								continue;
						}
					}
				}

				cont = false;
			}
		} while (cont);

		for (Material mat = loc.getBlock().getType(); mat.equals(Material.AIR) || mat.equals(Material.VOID_AIR) || mat.equals(Material.CAVE_AIR); mat = loc.getBlock().getType()) {
			loc.subtract(0.0D, 1.0D, 0.0D);
		}

		loc.add(0.0D, 1.0D, 0.0D);
		return loc;
	}

	public int[] generateLocationAsync(World world, TreasureChestType type) {
		//Initialize these before async
		String prefix = type.name().toLowerCase() + ".";
		//Location loc = null;
		boolean cont = true;
		int timeout = 0;
		Random random = ThreadLocalRandom.current();
		int xRange = data.getInt(prefix + "range-x");
		int zRange = data.getInt(prefix + "range-z");
		int chunkRange = data.getInt(prefix + "range-chunk");
		int noZoneX = data.getInt(prefix + "nozone-x");
		int noZoneZ = data.getInt(prefix + "nozone-z");
		int noZoneNegativeX = 0 - noZoneX;
		int noZoneNegativeZ = 0 - noZoneZ;
		int[] xZ = new int[2];

		//Set up worldborder
		boolean hasWorldBorder = false;
		double wbMaxX = 0,wbMaxZ = 0,wbMinX = 0,wbMinZ = 0;

		if (world.getWorldBorder() != null) {
			WorldBorder worldBorder = world.getWorldBorder();
			if (worldBorder.getSize() > 0) {
				hasWorldBorder = true;
				double size = worldBorder.getSize();
				int centerX = worldBorder.getCenter().getBlockX() , centerZ = worldBorder.getCenter().getBlockZ();
				wbMaxX = centerX + size;
				wbMaxZ = centerZ + size;
				wbMinX = centerX - size;
				wbMinZ = centerZ - size;
			}
		}

		do {
			++timeout;
			if (timeout == 50) {
				Bukkit.getLogger().warning("[TreasureHunter] Location generation timed out!");
				return null;
			}

			int x = random.nextInt(xRange * 2) - xRange;
			int z = random.nextInt(zRange * 2) - zRange;

			//Check that x and z are not within the no-zone
			if ((x < noZoneNegativeX || x > noZoneX) && (z < noZoneNegativeZ || z > noZoneZ)) {
				//Generate a new location
				if (chunkRange > 0) {
					int cx = x >> 4;
					int cz = z >> 4;
					ArrayList<Coord> borders = new ArrayList<>();
					borders.add(new Coord(cx, cz));

					for (int i = 1; i < chunkRange + 1; ++i) {
						borders.add(new Coord(cx + i, cz));
						borders.add(new Coord(cx - i, cz));
						borders.add(new Coord(cx, cz + i));
						borders.add(new Coord(cx, cz - i));
						borders.add(new Coord(cx + i, cz + i));
						borders.add(new Coord(x + i, cz - i));
						borders.add(new Coord(cx - i, cz - i));
						borders.add(new Coord(cx - i, cz + i));
					}

					boolean nearTown = false;

					xZ[0] = x;
					xZ[1] = z;

					TownyWorld townyWorld;
					try {
						townyWorld = TownyUniverse.getDataSource().getWorld(world.getName());
					} catch (NotRegisteredException var24) {
						return xZ;
					}

					Iterator var21 = borders.iterator();

					while (var21.hasNext()) {
						Coord key = (Coord) var21.next();
						try {
							if (townyWorld.hasTownBlock(key)) {
								nearTown = true;
								break;
							}
						} catch (Exception e) {
							// Catch any concurrency issues. Should be relatively thread safe as we are not fetching data,
							// just checking for containment.
							Bukkit.getLogger().warning("[TreasureHunter] Exception caught while async checking town block location.");
						}
					}

					if (nearTown) {
						continue;
					}

					//Location inside worldborder
					if (hasWorldBorder && (x > wbMaxX || x < wbMinX || z > wbMaxZ || x < wbMinZ)) continue;

				}

				cont = false;
			}
		} while (cont);

		return xZ;
	}





}
