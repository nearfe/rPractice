package com.conaxgames.register;

import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

@RequiredArgsConstructor
public class UserManager {
    private final UserDatabase userDatabase;

    public boolean isRegistered(Player player) throws Exception {
        String query = "SELECT * FROM " + "user" + " WHERE uuid = ?";
        try (PreparedStatement statement = userDatabase.getConnection().prepareStatement(query)) {
            statement.setString(1, player.getUniqueId().toString());

            try (ResultSet set = statement.executeQuery()) {
                return set.next();
            }
        }
    }

    public void registerUser(Player player, String key) throws Exception {
        if (isRegistered(player)) {
            return;
        }

        String query = "INSERT INTO " + "user" + " (uuid, password, password_key) " +
                "VALUES (?, ?, ?)";
        try (PreparedStatement statement = userDatabase.getConnection().prepareStatement(query)) {
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, "NULL");
            statement.setString(3, key);

            statement.execute();
        }

        TextComponent copy = new TextComponent("[Click to Copy]");
        copy.setColor(ChatColor.AQUA);
        copy.setBold(true);
        copy.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, key));

        player.sendMessage(ChatColor.DARK_AQUA + "Thanks for registering. Copy the code below and finish registering on https://minion.lol/dev/verify");
        player.spigot().sendMessage(copy);
    }
}
