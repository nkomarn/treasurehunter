package net.urbanmc.treasurehunter.manager;

import com.palmergames.bukkit.towny.TownyAPI;
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

	public int[] generateLocationAsync(String worldName, double[] worldBorder, TreasureChestType type) {
		String prefix = type.name().toLowerCase() + ".";

		Random random = ThreadLocalRandom.current();

		// Get config values
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

		if ((hasWorldBorder = worldBorder != null)) {
			wbMaxX = worldBorder[0];
			wbMinX = worldBorder[1];
			wbMaxZ = worldBorder[2];
			wbMinZ = worldBorder[3];
		}

		// Check if a towny world exists for the world
		boolean hasTownyWorld = true;

		TownyWorld townyWorld = null;
		try {
			townyWorld = TownyAPI.getInstance().getDataSource().getWorld(worldName);
		} catch (NotRegisteredException var24) {
			hasTownyWorld = false;
		}

		// Establish loop variables
		boolean cont = true;
		int timeout = 0;

		do {
			// Increment time out
			++timeout;

			int x = random.nextInt(xRange * 2) - xRange;
			int z = random.nextInt(zRange * 2) - zRange;

			//Check that x and z are not within the no-zone
			if ((x < noZoneNegativeX || x > noZoneX) && (z < noZoneNegativeZ || z > noZoneZ)) {
				//Generate a new location
				if (chunkRange > 0) {
					int cx = x >> 4;
					int cz = z >> 4;
					ArrayList<Coord> borders = new ArrayList<>(chunkRange * 8);
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

					xZ[0] = x;
					xZ[1] = z;

					//Location inside worldborder
					if (hasWorldBorder && (x > wbMaxX || x < wbMinX || z > wbMaxZ || x < wbMinZ)) continue;

					if (!hasTownyWorld) continue;

					boolean nearTown = false;

					for (Coord key : borders) {
						try {
							// TownyBlocks are stored in a hashtable which is synchronized
							if (townyWorld.hasTownBlock(key)) {
								nearTown = true;
								break;
							}
						} catch (Exception e) {
							// Check for any exceptions
							Bukkit.getLogger().warning("[TreasureHunter] Exception caught while async checking town block location.");
						}
					}

					if (nearTown) continue;
				}

				cont = false;
			}
		} while (cont && timeout < 50);

		if (timeout == 50) {
			Bukkit.getLogger().warning("[TreasureHunter] Location generation timed out!");
			return null;
		}

		return xZ;
	}





}
