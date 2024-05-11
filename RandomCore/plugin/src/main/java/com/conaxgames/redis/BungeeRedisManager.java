package com.conaxgames.redis;

import com.google.gson.JsonObject;
import net.minecraft.server.v1_8_R3.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import com.conaxgames.CorePlugin;
import com.conaxgames.entity.wrapper.DummyWrapper;
import com.conaxgames.redis.subscription.JedisSubscriptionHandler;

import java.util.UUID;

public class BungeeRedisManager implements JedisSubscriptionHandler<JsonObject> {
    @Override
    public void handleMessage(JsonObject object) {
        try {
            if (object.has("type")) {
                MessageType type = MessageType.valueOf(object.get("type").getAsString());
                Bukkit.getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> {
                    UUID uuid = UUID.fromString(object.get("uuid").getAsString());
                    switch (type) {
                        case ADD: {
                            String server = object.get("server").getAsString();

                            if (CorePlugin.getInstance().getServerManager().getServerName().equals(server)) {
                                CorePlugin.getInstance().getPlayerManager().getDummyPlayers().add(uuid);
                                DummyWrapper wrapper = new DummyWrapper(Bukkit.getWorld("world"), uuid);
                                wrapper.spawn();
                            }
                            break;
                        }

                        case REMOVE: {
                            String server = object.get("server").getAsString();

                            if (CorePlugin.getInstance().getServerManager().getServerName().equalsIgnoreCase(server)) {
                                Player player = Bukkit.getPlayer(uuid);
                                CorePlugin.getInstance().getPlayerManager().getDummyPlayers().remove(uuid);
                                if (player != null) {
                                    Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () -> {
                                        PlayerList playerList = ((CraftServer) Bukkit.getServer()).getHandle();
                                        playerList.disconnect(((CraftPlayer) player).getHandle());
                                    });
                                }
                            }
                            break;
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}