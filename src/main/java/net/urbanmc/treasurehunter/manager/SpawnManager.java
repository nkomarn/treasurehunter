package net.urbanmc.treasurehunter.manager;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Coord;
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

	public int[] generateLocationAsync(String worldName, double[] worldBorder, TreasureChestType type) {
		String prefix = type.name().toLowerCase() + ".";

		Random random = ThreadLocalRandom.current();

		// Get config values
		int xRange = data.getInt(prefix + "range-x");
		int zRange = data.getInt(prefix + "range-z");
		int chunkRange = data.getInt(prefix + "range-chunk");

		int noZoneX = data.getInt(prefix + "nozone-x");
		int noZoneZ = data.getInt(prefix + "nozone-z");
		int noZoneNegativeX = -noZoneX;
		int noZoneNegativeZ = -noZoneZ;

		int[] xZ = new int[2];

		//Set up worldborder
		boolean hasWorldBorder;
		double wbMaxX = 0, wbMaxZ = 0, wbMinX = 0, wbMinZ = 0;

		if ((hasWorldBorder = worldBorder != null)) {
			wbMaxX = worldBorder[0];
			wbMinX = worldBorder[1];
			wbMaxZ = worldBorder[2];
			wbMinZ = worldBorder[3];
		}

		// Check if a towny world exists for the world
		TownyWorld townyWorld = TownyAPI.getInstance().getTownyWorld(worldName);

		// Establish loop vars
		boolean validPosFound = false;
		int timeout;

		for (timeout = 0; timeout < 51 && !validPosFound; ++timeout) {
			// Choose a random x and z position from within the range.
			int x = random.nextInt(xRange * 2) - xRange;
			int z = random.nextInt(zRange * 2) - zRange;

			// Validate that location is inside worldborder
			if (hasWorldBorder && (x > wbMaxX || x < wbMinX || z > wbMaxZ || x < wbMinZ))
				continue;

			//Check that x and z are not within the no-zone
			if ((x > noZoneNegativeX && x < noZoneX) ||
					(z > noZoneNegativeZ && z < noZoneZ))
				continue;

			if (chunkRange > 0) {
				int chunkX = x >> 4;
				int chunkZ = z >> 4;

				// Make sure no town within range of the chosen coord.
				if (isInTownChunkRange(townyWorld, chunkX, chunkZ, chunkRange)) {
					continue;
				}
			}

			// Set x z coords.
			xZ[0] = x;
			xZ[1] = z;

			validPosFound = true;
		}

		if (timeout == 50) {
			Bukkit.getLogger().warning("[TreasureHunter] Location generation timed out!");
			return null;
		}

		return xZ;
	}

	/**
	 * Determines whether the passed in position is near a town within
	 * the passed-in chunk range.
	 *
	 * @param townyWorld Towny World to check upon. Returns false if this is null.
	 * @param cx Starting x-pos of chunk
	 * @param cz Starting z-pos of chunk
	 * @param chunkRange Chunk range to check within
	 * @return true if a town is within range of the position, else false.
	 */
	private boolean isInTownChunkRange(TownyWorld townyWorld, int cx, int cz, int chunkRange) {
		if (townyWorld == null)
			return false;

		final Coord centerCoord = new Coord(cx, cz);
		// Check initial coord
		try {
			 if(townyWorld.hasTownBlock(centerCoord))
			 	return true;
		} catch (Exception ignore) {}

		for (int i = 1; i < chunkRange + 1; ++i) {
			for(int facX = -1; facX < 2; facX++) {
				for (int facZ = -1; facZ < 2; facZ++) {
					if (facX == 0 && facZ == 0) {
						continue;
					}

					// #add returns a new coord
					final Coord offsetCoord = centerCoord.add((i * facX), (i * facZ));
					try {
						// TownyBlocks are stored in a hashtable which is synchronized
						if (townyWorld.hasTownBlock(offsetCoord)) {
							return true;
						}
					} catch (Exception e) {
						// Check for any exceptions
						Bukkit.getLogger().warning("[TreasureHunter] Exception caught while async checking town block location.");
					}
				}
			}
		}

		return false;
	}

}
