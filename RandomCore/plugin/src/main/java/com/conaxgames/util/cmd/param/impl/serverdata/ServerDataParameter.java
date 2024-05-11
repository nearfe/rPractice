package com.conaxgames.util.cmd.param.impl.serverdata;

import org.bukkit.command.CommandSender;
import com.conaxgames.CorePlugin;
import com.conaxgames.server.ServerData;
import com.conaxgames.util.cmd.param.Parameter;
import com.conaxgames.util.finalutil.CC;

public class ServerDataParameter extends Parameter<WrappedServerData> {
	@Override
	public WrappedServerData transfer(CommandSender sender, String argument) {
		if (argument.equalsIgnoreCase("self") || argument.equalsIgnoreCase("this") || argument.equalsIgnoreCase("")) {
			return new WrappedServerData(CorePlugin.getInstance().getServerManager().getServerName(), CorePlugin.getInstance().getServerManager().getServers().get(CorePlugin.getInstance().getServerManager().getServerName()));
		}
		ServerData serverData = CorePlugin.getInstance().getServerManager().getServerDataByName(argument);
		if (serverData == null) {
			sender.sendMessage(CC.RED + "Server \'" + argument + "\' not found!");
			return null;
		}

		return new WrappedServerData(argument, serverData);
	}
}
