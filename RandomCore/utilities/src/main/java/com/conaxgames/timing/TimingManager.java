package com.conaxgames.timing;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TimingManager {

	public static final TimingManager INSTANCE = new TimingManager();

	private final Map<String, Timer> timerMap = new ConcurrentHashMap<>();

	public Timer getTimer(String name) {
		return this.timerMap.computeIfAbsent(name, s -> timerMap.put(s, new Timer(s)));
	}

	public void start(String name) {
		this.getTimer(name).start();
	}

	public long stop(String name) {
		return this.getTimer(name).stop();
	}

}
