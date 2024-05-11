package club.minion.practice.killmessages.menu;

import club.minion.practice.killmessages.KillMessages;
import club.minion.practice.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class KillMessagesMenu
extends Menu {
    private static final club.minion.practice.menu.Button BLACK_PANE = club.minion.practice.menu.Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15);

    public KillMessagesMenu() {
        this.setUpdateAfterClick(true);
    }

    @NotNull
    public String getTitle(@NotNull Player player) {
        return ChatColor.YELLOW + "Kill Messages";
    }

    @NotNull
    public Map<Integer, club.minion.practice.menu.Button> getButtons(@NotNull Player player) {
        HashMap<Integer, club.minion.practice.menu.Button> buttons = new HashMap<Integer, club.minion.practice.menu.Button>();
        int y = 1;
        int x = 1;
        for (KillMessages messages : KillMessages.values()) {
            buttons.put(this.getSlot(x++, y), new KillMessagesButton(messages));
            if (x != 8) continue;
            ++y;
            x = 1;
        }
        for (int i = 0; i < 36; ++i) {
            buttons.putIfAbsent(i, BLACK_PANE);
        }
        return buttons;
    }
}

