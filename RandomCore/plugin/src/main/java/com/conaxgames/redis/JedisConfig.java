package com.conaxgames.redis;

import com.conaxgames.util.Config;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author TehNeon
 * @since 8/29/2017
 */
public class JedisConfig extends Config {

	public JedisConfig(JavaPlugin plugin) {
		super("jedis", plugin);

		// If the file was just created, save a default config
		if (this.wasCreated) {
			this.getConfig().set("jedis.host", "localhost");
			this.getConfig().set("jedis.port", 6379);
			this.getConfig().set("jedis.password", "asdf");
			this.save();
		}
	}

	public JedisSettings toJedisSettings() {
		return new JedisSettings(this.getConfig().getString("jedis.host"), this.getConfig().getInt("jedis.port"), this.getConfig().getString("jedis.password"));
	}
}
