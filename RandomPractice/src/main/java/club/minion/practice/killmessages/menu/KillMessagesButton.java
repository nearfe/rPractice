package club.minion.practice.killmessages.menu;

import club.minion.practice.Practice;
import club.minion.practice.killmessages.KillMessages;
import club.minion.practice.menu.Button;
import club.minion.practice.player.PlayerData;
import club.minion.practice.util.ItemUtil;
import com.google.common.base.Preconditions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class KillMessagesButton
extends Button {
    private final KillMessages killMessages;

    public KillMessagesButton(KillMessages killMessages) {
        this.killMessages = (KillMessages)((Object)Preconditions.checkNotNull((Object)((Object)killMessages), (Object)"killMessages"));
    }

    @NotNull
    public String getName(Player player) {
        return ChatColor.AQUA + this.killMessages.getName();
    }

    @NotNull
    public List<String> getDescription(Player player) {
        ArrayList<String> description = new ArrayList<String>();
        description.add(ChatColor.YELLOW + "[Click to equip]");
        return description;
    }

    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        PlayerData playerData = Practice.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData.getKillMessage() == this.killMessages) {
            player.sendMessage(ChatColor.RED + "This kill message is already in use.");
        } else {
            playerData.setKillMessage(this.killMessages);
            player.sendMessage(ChatColor.GREEN + "You have selected " + ChatColor.YELLOW + this.killMessages.getName() + ChatColor.GREEN + " kill messages!");
        }
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return ItemUtil.createItem(this.killMessages.getIcon().getType(), this.killMessages.getName());
    }
}

