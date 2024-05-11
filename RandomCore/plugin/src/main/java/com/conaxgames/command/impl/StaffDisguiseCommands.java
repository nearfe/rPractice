package com.conaxgames.command.impl;

import com.conaxgames.CorePlugin;
import com.conaxgames.api.abstr.AbstractBukkitCallback;
import com.conaxgames.api.impl.DisguiseRequest;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.Param;
import com.conaxgames.util.cmd.annotation.commandTypes.BaseCommand;
import com.conaxgames.util.cmd.annotation.commandTypes.SubCommand;
import com.conaxgames.util.finalutil.CC;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StaffDisguiseCommands implements CommandHandler {
    @BaseCommand(name = "staffdisguise", rank = Rank.MANAGER)
    public void staffdisguise(Player player) {
        player.sendMessage(CC.RED + "Usage: /staffdisguise <list|add <name> <skin>|remove <id>>");
    }

    @SubCommand(baseCommand = "staffdisguise", name = "list", rank = Rank.MANAGER)
    public void list(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () ->
                CorePlugin.getInstance().getRequestProcessor().sendRequest(
                        new DisguiseRequest.DisguiseListRequest(),
                        new AbstractBukkitCallback() {
                            public void callback(JsonElement jsonElement) {
                                JsonArray array = jsonElement.getAsJsonArray();
                                array.forEach(element -> {
                                    JsonObject object = element.getAsJsonObject();
                                    player.sendMessage("ID: " + object.get("id").getAsInt()
                                            + ", Name: " + object.get("name").getAsString()
                                            + ", Skin: " + object.get("skin").getAsString());
                                });
                            }
                        }
        ));
    }

    @SubCommand(baseCommand = "staffdisguise", name = "add", rank = Rank.MANAGER)
    public void add(Player player,
                    @Param(name = "name") String name,
                    @Param(name = "skin") String skin) {
        if (name.length() > 16 || skin.length() > 16) {
            player.sendMessage(CC.RED + "Name and skin name must be 16 characters long or less.");
            return;
        }

        CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(
                new DisguiseRequest.DisguiseAddRequest(name, skin),
                new AbstractBukkitCallback() {
                    public void callback(JsonElement jsonElement) {
                        JsonObject data = jsonElement.getAsJsonObject();
                        switch (data.get("response").getAsString()) {
                            case "already-have-disguise":
                                player.sendMessage(CC.RED + "A disguise with that name already exists.");
                                break;
                            case "success":
                                player.sendMessage(CC.GREEN + "Created disguise with the name " + name + " and skin " + skin + ".");
                                break;
                        }
                    }
                }
        );
    }

    @SubCommand(baseCommand = "staffdisguise", name = "remove", rank = Rank.MANAGER)
    public void remove(Player player,
                    @Param(name = "id") int id) {
        CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(
                new DisguiseRequest.DisguiseDeleteRequest(id));
        player.sendMessage(CC.GREEN + "If a disguise with id " + id + " exists, it will be deleted.");
    }
}
