package com.conaxgames.util.cmd.param.impl.serverdata;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.conaxgames.server.ServerData;

@Data
@AllArgsConstructor
public class WrappedServerData {

	private String argument;
	private ServerData serverData;

}
