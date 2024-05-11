package com.conaxgames.util.finalutil;

public final class MathUtil {
	private MathUtil() {
		throw new RuntimeException("Cannot instantiate a utility class.");
	}

	public static double roundOff(double x, int places) {
		double pow = Math.pow(10, places);
		return Math.round(x * pow) / pow;
	}
}
