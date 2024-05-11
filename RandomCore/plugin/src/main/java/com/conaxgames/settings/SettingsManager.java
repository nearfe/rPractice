package com.conaxgames.settings;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.conaxgames.CorePlugin;

import java.util.HashSet;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class SettingsManager {

	private final Set<SettingsHandler> settingsHandlers = new HashSet<>();

	private final CorePlugin plugin;

	public void addSettingsHandler(SettingsHandler settingsHandler) {
		this.settingsHandlers.add(settingsHandler);
	}
}
