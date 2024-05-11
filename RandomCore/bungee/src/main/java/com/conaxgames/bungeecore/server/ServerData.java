package com.conaxgames.bungeecore.server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.config.ServerInfo;

/**
 * @since 10/10/2017
 */
@RequiredArgsConstructor
@Getter
public class ServerData {

	private final ServerInfo serverInfo;
	private final String proxy;
}
