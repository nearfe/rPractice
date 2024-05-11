package com.conaxgames.event.player;

import com.conaxgames.event.BaseEvent;
import com.conaxgames.rank.Rank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class RankChangeEvent extends BaseEvent {
	private final UUID uuid;
	private final Rank from;
	private final Rank to;
}
