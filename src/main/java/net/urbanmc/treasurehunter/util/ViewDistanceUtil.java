package net.urbanmc.treasurehunter.util;

import org.bukkit.World;
import org.bukkit.entity.Player;

public class ViewDistanceUtil {

    private int spawnWorldTickDistance = -1,
            spawnWorldNoTickDistance = -1;

    public void updateSpawnWorldDistance(World world) {
        spawnWorldNoTickDistance = getNoTickDistance(world);
        spawnWorldTickDistance = getWorldTickDistance(world);
    }

    public void setPlayerViewDistance(Player p, int viewDistance) {
        setPlayerNoTickDistance(p, viewDistance);
        setPlayerTickViewDistance(p, viewDistance);
    }

    public void resetPlayerViewDistance(Player p) {
        setPlayerNoTickDistance(p, spawnWorldNoTickDistance);
        setPlayerTickViewDistance(p, spawnWorldTickDistance);
    }

    private int getNoTickDistance(World world) {
        try {
            return world.getNoTickViewDistance();
        } catch (NoSuchMethodError ignore) {
            return -1;
        }
    }

    private int getWorldTickDistance(World world) {
        return world.getViewDistance();
    }

    private void setPlayerTickViewDistance(Player p, int distance) {
        if (distance < 2)
            return;

        try {
            p.setViewDistance(distance);
        } catch (Exception ignore) {
        }
    }

    private void setPlayerNoTickDistance(Player p, int distance) {
        if (distance < 2)
            return;

        try {
            p.setNoTickViewDistance(distance);
        } catch (Exception ignore) {
        }
    }

}
