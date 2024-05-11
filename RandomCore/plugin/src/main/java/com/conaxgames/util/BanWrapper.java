package com.conaxgames.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents ban data from an SQL request.
 */
@Getter
@RequiredArgsConstructor
public class BanWrapper {

	private final String message;
	private final boolean banned;

}
