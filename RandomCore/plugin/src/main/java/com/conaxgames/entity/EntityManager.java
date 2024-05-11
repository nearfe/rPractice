package com.conaxgames.entity;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;
import com.conaxgames.CorePlugin;
import com.conaxgames.entity.wrapper.PlayerWrapper;
import com.conaxgames.util.CustomLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles spawning for our custom entities.
 * Also tracks and handles custom NPC's as they're different
 * when compared to other entities.
 */
public class EntityManager {

	@Getter
	private Map<Integer, PlayerWrapper> fakePlayers = new HashMap<>();

	/**
	 * Spawn custom entity at specified location with assigned name tag.
	 * Note: Method is static as it doesn't require the fakePlayers map,
	 * and can be used as a Util method,
	 *
	 * @param entityType - Type of entity you wish to spawn.
	 * @param location   - Location you wish the entity to spawn at.
	 * @param name       - Name tag to display above the entity (leave null for no name tag).
	 * @return - Returns the entity created.
	 */
	public static Entity spawnEntity(EntityType entityType, CustomLocation location, String name) {
		Location bukkitLocation = location.toBukkitLocation();

		Entity entity = bukkitLocation.getWorld().spawnEntity(bukkitLocation, entityType);

		entity.setMetadata("custom", new FixedMetadataValue(CorePlugin.getInstance(), true));

		if (name != null) {
			entity.setCustomNameVisible(true);
			entity.setCustomName(name);
		}

		return entity;
	}

	/**
	 * Spawn a player NPC at the specified location and assign it a name.
	 *
	 * @param location - Location you wish the NPC to spawn at.
	 * @param name     - Custom name of the NPC.
	 * @return - PlayerWrapper object for the NPC.
	 */
	public PlayerWrapper spawnPlayer(CustomLocation location, String name) {
		if (name.length() > 16) {
			throw new IllegalArgumentException(String.format("Name (%s) is longer than the maximum 16 characters.", name));
		}

		World world = location.toBukkitWorld();

		PlayerWrapper wrapper = new PlayerWrapper(world, name);
		wrapper.setLocation(location);

		this.fakePlayers.put(wrapper.getId(), wrapper);

		wrapper.spawn();

		return wrapper;
	}

}
