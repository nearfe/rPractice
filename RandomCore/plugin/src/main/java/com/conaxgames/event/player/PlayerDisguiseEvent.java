package com.conaxgames.event.player;

import com.conaxgames.event.MinemanEvent;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import lombok.Getter;

@Getter
public class PlayerDisguiseEvent extends MinemanEvent {
    private final String disguisedName;
    private final Rank disguisedRank;

    public PlayerDisguiseEvent(Mineman mineman, String disguisedName, Rank disguisedRank) {
        super(mineman);
        this.disguisedName = disguisedName;
        this.disguisedRank = disguisedRank;
    }
}
