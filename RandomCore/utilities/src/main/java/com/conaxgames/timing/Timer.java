package com.conaxgames.timing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class Timer {

	@Getter private final Map<Long, Long> timings = new HashMap<>();

	@Getter private final String name;

	private boolean running;
	private long startTime;

	public void start() {
		if (this.running) {
			throw new IllegalStateException("Timer already running!");
		}

		this.running = true;
		this.startTime = System.nanoTime();
	}

	public long stop() {
		if (!this.running) {
			throw new IllegalStateException("Timer is not running!");
		}

		this.running = false;

		long time = System.nanoTime() - this.startTime;
		this.timings.put(System.currentTimeMillis(), time);
		return time;
	}

}
