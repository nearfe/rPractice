package com.conaxgames.rank;

import com.conaxgames.util.finalutil.CC;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Rank {
	NORMAL("", CC.GREEN, "Normal"),
	BASIC(CC.GRAY + "[" + CC.DARK_GREEN + "Basic" + CC.GRAY + "] ", CC.YELLOW, "Basic"),
	PRIME(CC.GRAY + "[" + CC.LIGHT_PURPLE + "Prime" + CC.GRAY + "] ", CC.DARK_GREEN, "Prime"),
	ELITE(CC.GRAY + "[" + CC.B_AQUA + "Elite" + CC.GRAY + "] ", CC.GOLD, "Elite"),
	MASTER(CC.GRAY + "[" + CC.B_GREEN + "Master" + CC.GRAY + "] ", CC.BLUE, "Master"),
	YOUTUBER(CC.GRAY + "[" + CC.LIGHT_PURPLE + "YouTuber" + CC.GRAY  + "] ", CC.LIGHT_PURPLE, "YouTuber"),
	TWITCH(CC.GRAY + "[" + CC.D_PURPLE + "Twitch" + CC.GRAY  + "] ", CC.D_PURPLE, "Twitch"),
	FAMOUS(CC.GRAY + "[" + CC.LIGHT_PURPLE + CC.I + "Famous" + CC.GRAY  + "] ", CC.LIGHT_PURPLE + CC.I, "Famous"),
	PARTNER(CC.GRAY + "[" + CC.LIGHT_PURPLE + "Partner" + CC.GRAY  + "] ", CC.LIGHT_PURPLE, "Partner"),
	HOST(CC.GRAY + "[" + CC.D_PURPLE + "Host" + CC.GRAY  + "] ", CC.DARK_PURPLE, "Host"),
	TRAINEE(CC.GRAY + "[" + CC.YELLOW + "Minion" + CC.GRAY  + "] ", CC.YELLOW + CC.I, "Minion"),
	MOD(CC.GRAY + "[" + CC.DARK_AQUA + "Mod" + CC.GRAY  + "] ", CC.DARK_AQUA , "Mod"),
	SENIORHOST(CC.GRAY + "[" + CC.D_PURPLE + CC.I + "Senior Host" + CC.GRAY  + "] ", CC.DARK_PURPLE + CC.I, "Senior-Host"),
	SENIORMOD(CC.GRAY + "[" + CC.DARK_AQUA + CC.I + "Senior Mod" + CC.GRAY  + "] ", CC.DARK_AQUA + CC.I, "Senior-Mod"),
	ADMIN(CC.GRAY + "[" + CC.RED + "Admin" + CC.GRAY  + "] ", CC.RED, "Admin"),
	SENIORADMIN(CC.GRAY + "[" + CC.RED + CC.I + "Senior Admin" + CC.GRAY  + "] ", CC.RED + CC.I, "Senior-Admin"),
	MANAGER(CC.GRAY + "[" + CC.RED + "Manager" + CC.GRAY  + "] ", CC.RED, "Manager"),
	DEVELOPER(CC.GRAY + "[" + CC.AQUA + "Developer" + CC.GRAY  + "] ", CC.AQUA, "Developer"),
	OWNER(CC.GRAY + "[" + CC.DARK_RED + "Owner" + CC.GRAY  + "] ", CC.DARK_RED, "Owner");

	public static final Rank[] RANKS = Rank.values();

	private final String prefix;
	private final String color;
	private final String name;

	Rank(String prefix, String color, String name) {
		this.prefix = prefix;
		this.color = color;
		this.name = name;
	}

	Rank(String color, String name) {
		this(CC.D_GRAY + "[" + color + name + CC.D_GRAY + "] ", color, name);
	}

	public static Rank getByName(String name) {
		return Arrays.stream(Rank.RANKS)
				.filter(rank -> rank.getName().equalsIgnoreCase(name) || rank.name().equalsIgnoreCase(name))
				.findFirst().orElse(null);
	}

	public Rank max(Rank rank) {
		return this.getPriority() >= rank.getPriority() ? this : rank;
	}

	public int getPriority() {
		return this.ordinal();
	}

	public boolean hasRank(Rank requiredRank) {
		if (requiredRank == null) {
			return false;
		}

		return getPriority() >= requiredRank.getPriority();
	}

	public boolean isAbove(Rank rank) {
		return this.ordinal() > rank.ordinal();
	}
}