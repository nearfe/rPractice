package com.conaxgames.event.player;

import com.conaxgames.event.MinemanEvent;
import com.conaxgames.mineman.Mineman;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;

public class PlayerPreDisguiseEvent extends MinemanEvent implements Cancellable  {
    @Getter @Setter private boolean cancelled;

    public PlayerPreDisguiseEvent(Mineman mineman) {
        super(mineman);
    }
}
