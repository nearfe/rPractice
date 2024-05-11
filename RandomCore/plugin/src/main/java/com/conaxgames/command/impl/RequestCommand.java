package com.conaxgames.command.impl;

import com.conaxgames.CorePlugin;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import com.conaxgames.redis.CoreRedisManager;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.Color;
import com.conaxgames.util.finalutil.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class RequestCommand extends org.bukkit.command.Command {
	private CorePlugin plugin;

	public RequestCommand(CorePlugin plugin) {
		super("helpop");
		this.usageMessage = CC.RED + "Usage: /helpop <reason>";
		this.plugin = plugin;
		this.setAliases(Arrays.asList("request"));
	}

	public boolean execute(CommandSender sender, String s, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("die");
			return true;
		}

		Player player = (Player) sender;
		if (args.length == 0) {
			player.sendMessage(usageMessage);
			return true;
		}

		Mineman mineman = plugin.getPlayerManager().getPlayer(player.getUniqueId());

		if(mineman.isBanned() || mineman.isIpBanned()) {
			player.sendMessage(Color.translate("&cYou cannot use this command."));
			return true;
		}

		if (mineman.getReportCooldown() > System.currentTimeMillis()) {
			sender.sendMessage(CC.RED + "Please wait before doing this again.");
			return true;
		}

		String reason = StringUtil.buildMessage(args, 0);
		player.sendMessage(CC.GREEN + "Your request has been submitted.");

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> CorePlugin.getInstance().getCoreRedisManager().sendMessage(
        		CoreRedisManager.getServerMessagePrefix() + CC.BLUE + "[Request] " + player.getDisplayName() + CC.AQUA + " requested: " + CC.YELLOW + reason, Rank.HOST));
		mineman.setReportCooldown(System.currentTimeMillis() + (30L * 1000L));

		return true;
	}
}
