package com.conaxgames.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.conaxgames.CorePlugin;
import com.conaxgames.event.bungee.BungeeReceivedEvent;
import com.conaxgames.event.player.ModListRetrieveEvent;

import java.util.Map;

@RequiredArgsConstructor
public class BungeeListener implements PluginMessageListener {

	private final CorePlugin plugin;

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord")) {
			return;
		}

		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subChannel = in.readUTF();
		if (subChannel.equals("ForgeMods")) {
			try {
				Map<String, String> mods = (Map<String, String>) new JSONParser().parse(in.readUTF());
				ModListRetrieveEvent event = new ModListRetrieveEvent(player, mods);
				this.plugin.getServer().getPluginManager().callEvent(event);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return;
		}

		short len = in.readShort();
		byte[] messageBytes = new byte[len];
		in.readFully(messageBytes);

		ByteArrayDataInput dis = ByteStreams.newDataInput(messageBytes);
		String data = dis.readUTF();
		Long systemTime = Long.parseLong(data.split(":")[0]);

		BungeeReceivedEvent event = new BungeeReceivedEvent(player, subChannel, data.replace(systemTime + ":", ""), message, systemTime > System.currentTimeMillis());
		this.plugin.getServer().getPluginManager().callEvent(event);
	}

}
