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

public class ReportCommand extends org.bukkit.command.Command {
	private CorePlugin plugin;

	public ReportCommand(CorePlugin plugin) {
		super("report");
		this.usageMessage = CC.RED + "Usage: /report <player> <reason>";
		this.plugin = plugin;
	}

	public boolean execute(CommandSender sender, String s, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("die");
			return true;
		}

		Player player = (Player) sender;
		if (args.length < 2) {
			player.sendMessage(usageMessage);
			return true;
		}

		Player target = Bukkit.getPlayer(args[0]);
		if (target == null) {
			player.sendMessage(CC.RED + "Failed to find that player.");
			return true;
		}

		if (target.equals(player)) {
			sender.sendMessage(CC.RED + "You cannot report yourself.");
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

		String reason = StringUtil.buildMessage(args, 1);
		player.sendMessage(CC.GREEN + "Your report has been submitted.");

		Mineman targetMineman = plugin.getPlayerManager().getPlayer(target.getUniqueId());
		String senderName = plugin.getDisguiseManager().isDisguised(player) ?
				mineman.getRank().getColor() + mineman.getName() + CC.GRAY + " (" + player.getName() + CC.GRAY + ")" :
				player.getDisplayName();
		String targetName = plugin.getDisguiseManager().isDisguised(target) ?
				targetMineman.getRank().getColor() + targetMineman.getName() + CC.GRAY + " (" + target.getName() + CC.GRAY + ")" :
				target.getDisplayName();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> CorePlugin.getInstance().getCoreRedisManager().sendMessage(
        		CoreRedisManager.getServerMessagePrefix() + CC.BLUE + "[Report] " +
						senderName + CC.AQUA + " reported " +
						targetName + CC.AQUA + " for " + CC.YELLOW + reason + CC.AQUA + '.', Rank.TRAINEE));
		mineman.setReportCooldown(System.currentTimeMillis() + (30L * 1000L));

		return true;
	}
}
