package com.conaxgames.gson.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PotionEffectType {

	private final String type;
	private final int duration;
	private final int amplifier;
}
