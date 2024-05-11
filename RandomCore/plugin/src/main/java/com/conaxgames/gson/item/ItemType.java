package com.conaxgames.gson.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ItemType {

	private final String type;
	private final short durability;
	private final int amount;
	private final List<EnchantType> enchants;
	private final ItemMetaType meta;
}
