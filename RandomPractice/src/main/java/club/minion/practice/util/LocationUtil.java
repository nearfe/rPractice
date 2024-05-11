package club.minion.practice.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtil {

    public static final String SPAWN = "spawn";


    public static Location getSpawnLocation(String worldName, double x, double y, double z, float yaw, float pitch) {
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            return new Location(world, x, y, z, yaw, pitch);
        }
        return null;
    }
    public static boolean isPlayerInSpawn(Location playerLocation, Location spawnMin, Location spawnMax) {
        if (spawnMin != null && spawnMax != null) {
            double playerX = playerLocation.getX();
            double playerY = playerLocation.getY();
            double playerZ = playerLocation.getZ();

            double minX = Math.min(spawnMin.getX(), spawnMax.getX());
            double minY = Math.min(spawnMin.getY(), spawnMax.getY());
            double minZ = Math.min(spawnMin.getZ(), spawnMax.getZ());

            double maxX = Math.max(spawnMin.getX(), spawnMax.getX());
            double maxY = Math.max(spawnMin.getY(), spawnMax.getY());
            double maxZ = Math.max(spawnMin.getZ(), spawnMax.getZ());

            return playerX >= minX && playerX <= maxX &&
                    playerY >= minY && playerY <= maxY &&
                    playerZ >= minZ && playerZ <= maxZ;
        }
        return false;
    }
}
