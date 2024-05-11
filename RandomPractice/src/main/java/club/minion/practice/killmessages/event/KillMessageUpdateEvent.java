package club.minion.practice.killmessages.event;

import club.minion.practice.killmessages.KillMessages;
import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public final class KillMessageUpdateEvent
extends PlayerEvent {
    private static HandlerList handlerList = new HandlerList();
    private final KillMessages setting;

    public KillMessageUpdateEvent(Player player, KillMessages messages) {
        super(player);
        this.setting = (KillMessages)((Object)Preconditions.checkNotNull((Object)((Object)messages), (Object)"killmessage"));
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public KillMessages getSetting() {
        return this.setting;
    }
}

