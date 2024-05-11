package club.minion.practice.util;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class BlockUtil {
    public static boolean isStandingOn(Player player, Material material) {
        Block legs = player.getLocation().getBlock();
        Block head = legs.getRelative(BlockFace.UP);
        return (legs.getType() == material || head.getType() == material);
    }

    public static boolean isSameLocation(Location location, Location check) {
        return (location.getWorld().getName().equalsIgnoreCase(check.getWorld().getName()) && location.getBlockX() == check.getBlockX() && location.getBlockY() == check.getBlockY() && location.getBlockZ() == check.getBlockZ());
    }

    public static boolean isOnStairs(Location location, int down) {
        return isUnderBlock(location, blockStairsSet, down);
    }

    public static boolean isOnLiquid(Location location, int down) {
        return isUnderBlock(location, blockLiquidsSet, down);
    }

    public static boolean isOnWeb(Location location, int down) {
        return isUnderBlock(location, blockWebsSet, down);
    }

    public static boolean isOnIce(Location location, int down) {
        return isUnderBlock(location, blockIceSet, down);
    }

    public static boolean isOnCarpet(Location location, int down) {
        return isUnderBlock(location, blockCarpetSet, down);
    }

    private static boolean isUnderBlock(Location location, Set<Byte> itemIDs, int down) {
        double posX = location.getX();
        double posZ = location.getZ();
        double fracX = (posX % 1.0D > 0.0D) ? Math.abs(posX % 1.0D) : (1.0D - Math.abs(posX % 1.0D));
        double fracZ = (posZ % 1.0D > 0.0D) ? Math.abs(posZ % 1.0D) : (1.0D - Math.abs(posZ % 1.0D));
        int blockX = location.getBlockX();
        int blockY = location.getBlockY() - down;
        int blockZ = location.getBlockZ();
        World world = location.getWorld();
        if (itemIDs.contains(Byte.valueOf((byte) world.getBlockAt(blockX, blockY, blockZ).getTypeId())))
            return true;
        if (fracX < 0.3D) {
            if (itemIDs.contains(Byte.valueOf((byte) world.getBlockAt(blockX - 1, blockY, blockZ).getTypeId())))
                return true;
            if (fracZ < 0.3D) {
                if (itemIDs.contains(Byte.valueOf((byte) world.getBlockAt(blockX - 1, blockY, blockZ - 1).getTypeId())))
                    return true;
                if (itemIDs.contains(Byte.valueOf((byte) world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())))
                    return true;
                if (itemIDs.contains(Byte.valueOf((byte) world.getBlockAt(blockX + 1, blockY, blockZ - 1).getTypeId())))
                    return true;
            } else if (fracZ > 0.7D) {
                if (itemIDs.contains(Byte.valueOf((byte) world.getBlockAt(blockX - 1, blockY, blockZ + 1).getTypeId())))
                    return true;
                if (itemIDs.contains(Byte.valueOf((byte) world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())))
                    return true;
                if (itemIDs.contains(Byte.valueOf((byte) world.getBlockAt(blockX + 1, blockY, blockZ + 1).getTypeId())))
                    return true;
            }
        } else if (fracX > 0.7D) {
            if (itemIDs.contains(Byte.valueOf((byte) world.getBlockAt(blockX + 1, blockY, blockZ).getTypeId())))
                return true;
            if (fracZ < 0.3D) {
                if (itemIDs.contains(Byte.valueOf((byte) world.getBlockAt(blockX - 1, blockY, blockZ - 1).getTypeId())))
                    return true;
                if (itemIDs.contains(Byte.valueOf((byte) world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())))
                    return true;
                if (itemIDs.contains(Byte.valueOf((byte) world.getBlockAt(blockX + 1, blockY, blockZ - 1).getTypeId())))
                    return true;
            } else if (fracZ > 0.7D) {
                if (itemIDs.contains(Byte.valueOf((byte) world.getBlockAt(blockX - 1, blockY, blockZ + 1).getTypeId())))
                    return true;
                if (itemIDs.contains(Byte.valueOf((byte) world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())))
                    return true;
                if (itemIDs.contains(Byte.valueOf((byte) world.getBlockAt(blockX + 1, blockY, blockZ + 1).getTypeId())))
                    return true;
            }
        } else if (fracZ < 0.3D) {
            if (itemIDs.contains(Byte.valueOf((byte) world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())))
                return true;
        } else if (fracZ > 0.7D && itemIDs.contains(Byte.valueOf((byte) world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId()))) {
            return true;
        }
        return false;
    }

    public static boolean isOnGround(Location location, int down) {
        double posX = location.getX();
        double posZ = location.getZ();
        double fracX = (posX % 1.0D > 0.0D) ? Math.abs(posX % 1.0D) : (1.0D - Math.abs(posX % 1.0D));
        double fracZ = (posZ % 1.0D > 0.0D) ? Math.abs(posZ % 1.0D) : (1.0D - Math.abs(posZ % 1.0D));
        int blockX = location.getBlockX();
        int blockY = location.getBlockY() - down;
        int blockZ = location.getBlockZ();
        World world = location.getWorld();
        if (!blockSolidPassSet.contains(Byte.valueOf((byte) world.getBlockAt(blockX, blockY, blockZ).getTypeId())))
            return true;
        if (fracX < 0.3D) {
            if (!blockSolidPassSet.contains(Byte.valueOf((byte) world.getBlockAt(blockX - 1, blockY, blockZ).getTypeId())))
                return true;
            if (fracZ < 0.3D) {
                if (!blockSolidPassSet.contains(Byte.valueOf((byte) world.getBlockAt(blockX - 1, blockY, blockZ - 1).getTypeId())))
                    return true;
                if (!blockSolidPassSet.contains(Byte.valueOf((byte) world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())))
                    return true;
                if (!blockSolidPassSet.contains(Byte.valueOf((byte) world.getBlockAt(blockX + 1, blockY, blockZ - 1).getTypeId())))
                    return true;
            } else if (fracZ > 0.7D) {
                if (!blockSolidPassSet.contains(Byte.valueOf((byte) world.getBlockAt(blockX - 1, blockY, blockZ + 1).getTypeId())))
                    return true;
                if (!blockSolidPassSet.contains(Byte.valueOf((byte) world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())))
                    return true;
                if (!blockSolidPassSet.contains(Byte.valueOf((byte) world.getBlockAt(blockX + 1, blockY, blockZ + 1).getTypeId())))
                    return true;
            }
        } else if (fracX > 0.7D) {
            if (!blockSolidPassSet.contains(Byte.valueOf((byte) world.getBlockAt(blockX + 1, blockY, blockZ).getTypeId())))
                return true;
            if (fracZ < 0.3D) {
                if (!blockSolidPassSet.contains(Byte.valueOf((byte) world.getBlockAt(blockX - 1, blockY, blockZ - 1).getTypeId())))
                    return true;
                if (!blockSolidPassSet.contains(Byte.valueOf((byte) world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())))
                    return true;
                if (!blockSolidPassSet.contains(Byte.valueOf((byte) world.getBlockAt(blockX + 1, blockY, blockZ - 1).getTypeId())))
                    return true;
            } else if (fracZ > 0.7D) {
                if (!blockSolidPassSet.contains(Byte.valueOf((byte) world.getBlockAt(blockX - 1, blockY, blockZ + 1).getTypeId())))
                    return true;
                if (!blockSolidPassSet.contains(Byte.valueOf((byte) world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())))
                    return true;
                if (!blockSolidPassSet.contains(Byte.valueOf((byte) world.getBlockAt(blockX + 1, blockY, blockZ + 1).getTypeId())))
                    return true;
            }
        } else if (fracZ < 0.3D) {
            if (!blockSolidPassSet.contains(Byte.valueOf((byte) world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())))
                return true;
        } else if (fracZ > 0.7D && !blockSolidPassSet.contains(Byte.valueOf((byte) world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId()))) {
            return true;
        }
        return false;
    }

    private static Set<Byte> blockSolidPassSet = new HashSet<>();

    private static Set<Byte> blockStairsSet = new HashSet<>();

    private static Set<Byte> blockLiquidsSet = new HashSet<>();

    private static Set<Byte> blockWebsSet = new HashSet<>();

    private static Set<Byte> blockIceSet = new HashSet<>();

    private static Set<Byte> blockCarpetSet = new HashSet<>();

    static {
        blockSolidPassSet.add(Byte.valueOf((byte) 0));
        blockSolidPassSet.add(Byte.valueOf((byte) 6));
        blockSolidPassSet.add(Byte.valueOf((byte) 8));
        blockSolidPassSet.add(Byte.valueOf((byte) 9));
        blockSolidPassSet.add(Byte.valueOf((byte) 10));
        blockSolidPassSet.add(Byte.valueOf((byte) 11));
        blockSolidPassSet.add(Byte.valueOf((byte) 27));
        blockSolidPassSet.add(Byte.valueOf((byte) 28));
        blockSolidPassSet.add(Byte.valueOf((byte) 30));
        blockSolidPassSet.add(Byte.valueOf((byte) 31));
        blockSolidPassSet.add(Byte.valueOf((byte) 32));
        blockSolidPassSet.add(Byte.valueOf((byte) 37));
        blockSolidPassSet.add(Byte.valueOf((byte) 38));
        blockSolidPassSet.add(Byte.valueOf((byte) 39));
        blockSolidPassSet.add(Byte.valueOf((byte) 40));
        blockSolidPassSet.add(Byte.valueOf((byte) 50));
        blockSolidPassSet.add(Byte.valueOf((byte) 51));
        blockSolidPassSet.add(Byte.valueOf((byte) 55));
        blockSolidPassSet.add(Byte.valueOf((byte) 59));
        blockSolidPassSet.add(Byte.valueOf((byte) 63));
        blockSolidPassSet.add(Byte.valueOf((byte) 66));
        blockSolidPassSet.add(Byte.valueOf((byte) 68));
        blockSolidPassSet.add(Byte.valueOf((byte) 69));
        blockSolidPassSet.add(Byte.valueOf((byte) 70));
        blockSolidPassSet.add(Byte.valueOf((byte) 72));
        blockSolidPassSet.add(Byte.valueOf((byte) 75));
        blockSolidPassSet.add(Byte.valueOf((byte) 76));
        blockSolidPassSet.add(Byte.valueOf((byte) 77));
        blockSolidPassSet.add(Byte.valueOf((byte) 78));
        blockSolidPassSet.add(Byte.valueOf((byte) 83));
        blockSolidPassSet.add(Byte.valueOf((byte) 90));
        blockSolidPassSet.add(Byte.valueOf((byte) 104));
        blockSolidPassSet.add(Byte.valueOf((byte) 105));
        blockSolidPassSet.add(Byte.valueOf((byte) 115));
        blockSolidPassSet.add(Byte.valueOf((byte) 119));
        blockSolidPassSet.add(Byte.valueOf((byte) -124));
        blockSolidPassSet.add(Byte.valueOf((byte) -113));
        blockSolidPassSet.add(Byte.valueOf((byte) -81));
        blockStairsSet.add(Byte.valueOf((byte) 53));
        blockStairsSet.add(Byte.valueOf((byte) 67));
        blockStairsSet.add(Byte.valueOf((byte) 108));
        blockStairsSet.add(Byte.valueOf((byte) 109));
        blockStairsSet.add(Byte.valueOf((byte) 114));
        blockStairsSet.add(Byte.valueOf((byte) -122));
        blockStairsSet.add(Byte.valueOf((byte) -121));
        blockStairsSet.add(Byte.valueOf((byte) -120));
        blockStairsSet.add(Byte.valueOf((byte) -100));
        blockStairsSet.add(Byte.valueOf((byte) -93));
        blockStairsSet.add(Byte.valueOf((byte) -92));
        blockStairsSet.add(Byte.valueOf((byte) -76));
        blockStairsSet.add(Byte.valueOf((byte) 126));
        blockStairsSet.add(Byte.valueOf((byte) -74));
        blockStairsSet.add(Byte.valueOf((byte) 44));
        blockStairsSet.add(Byte.valueOf((byte) 78));
        blockStairsSet.add(Byte.valueOf((byte) 99));
        blockStairsSet.add(Byte.valueOf((byte) -112));
        blockStairsSet.add(Byte.valueOf((byte) -115));
        blockStairsSet.add(Byte.valueOf((byte) -116));
        blockStairsSet.add(Byte.valueOf((byte) -105));
        blockStairsSet.add(Byte.valueOf((byte) -108));
        blockStairsSet.add(Byte.valueOf((byte) 100));
        blockLiquidsSet.add(Byte.valueOf((byte) 8));
        blockLiquidsSet.add(Byte.valueOf((byte) 9));
        blockLiquidsSet.add(Byte.valueOf((byte) 10));
        blockLiquidsSet.add(Byte.valueOf((byte) 11));
        blockWebsSet.add(Byte.valueOf((byte) 30));
        blockIceSet.add(Byte.valueOf((byte) 79));
        blockIceSet.add(Byte.valueOf((byte) -82));
        blockCarpetSet.add(Byte.valueOf((byte) -85));
    }
}