package com.conaxgames.command.impl;

import com.conaxgames.CorePlugin;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.CustomLocation;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.Param;
import com.conaxgames.util.cmd.annotation.commandTypes.Command;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.Color;
import com.conaxgames.util.finalutil.PlayerUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

import java.util.*;

public class FreezeCommand implements CommandHandler, Listener {

	private final Map<UUID, CustomLocation> frozenPlayers = new HashMap<>();

	public FreezeCommand() {
		CorePlugin.getInstance().getServer().getScheduler().runTaskTimer(CorePlugin.getInstance(),
				new FrozenMessageTask(this), 20L * 10, 20L);
	}

	@Command(name = {"ss", "screenshare", "freeze"}, rank = Rank.TRAINEE, description = "Freeze a player")
	public void onFreeze(Player player, @Param(name = "target") Player target) {
		if (target == null || !target.isOnline()) {
			player.sendMessage(CC.RED + "Failed to find that player.");
			return;
		}

		if (this.frozenPlayers.remove(target.getUniqueId()) != null) {
			PlayerUtil.messageRank(CC.PRIMARY + target.getName() + CC.SECONDARY + " was unfrozen by " + CC.PRIMARY + player.getName() + CC.SECONDARY + ".", Rank.TRAINEE);
			target.sendMessage(CC.GREEN + "You are no longer frozen!");

			target.setAllowFlight(false);
			target.setFlying(false);
		} else {
			CustomLocation location = CustomLocation.fromBukkitLocation(target.getLocation());
			this.frozenPlayers.put(target.getUniqueId(), location);

			PlayerUtil.messageRank(CC.PRIMARY + target.getName() + CC.SECONDARY + " was frozen by " + CC.PRIMARY + player.getName() + CC.SECONDARY + ".", Rank.TRAINEE);

			target.setAllowFlight(true);
			target.setFlying(true);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if (this.frozenPlayers.remove(event.getPlayer().getUniqueId()) != null) {
			PlayerUtil.messageRank("");
			PlayerUtil.messageRank(CC.BD_RED + event.getPlayer().getName() + " has logged out while frozen.");
			PlayerUtil.messageRank("");
		}
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		if(this.frozenPlayers.containsKey(event.getPlayer().getUniqueId())) {
			this.frozenPlayers.put(event.getPlayer().getUniqueId(), CustomLocation.fromBukkitLocation(event.getTo()));
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if(this.frozenPlayers.containsKey(event.getEntity().getUniqueId())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		String command = event.getMessage().split(" ")[0];
		if (command.equalsIgnoreCase("msg") || command.equalsIgnoreCase("r")
				|| command.equalsIgnoreCase("m") || command.equalsIgnoreCase("tell")
				|| command.equalsIgnoreCase("message")) {
			return;
		}

		if(this.frozenPlayers.containsKey(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent event) {
		Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
		Rank rank = mineman.getRank();

		if(this.frozenPlayers.containsKey(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);

			String color =
					mineman.getCustomColor() != null && !mineman.getCustomColor().isEmpty() && mineman.hasRank(Rank.BASIC) ? mineman.getCustomColor() : rank.getColor();
			String formattedMessage = String.format(rank.getPrefix() + color + "%1$s" + CC.R + ": %2$s",
					event.getPlayer().getName(), event.getMessage());

			PlayerUtil.messageRank(CC.D_RED + "[Frozen] " + formattedMessage);
			event.getPlayer().sendMessage(formattedMessage);
			return;
		}

		if(!rank.hasRank(Rank.TRAINEE)) {
			event.getRecipients().removeIf(player -> this.frozenPlayers.containsKey(player.getUniqueId()));
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if(this.frozenPlayers.containsKey(event.getDamager().getUniqueId())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onDropItem(PlayerDropItemEvent event) {
		if(this.frozenPlayers.containsKey(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
		}
	}

	@RequiredArgsConstructor
	private class FrozenMessageTask implements Runnable {

		private final FreezeCommand command;
		private int count = 0;

		@Override
		public void run() {
			Set<UUID> remove = new HashSet<>();
			this.command.frozenPlayers.forEach((uuid, location) -> {
				Player player = CorePlugin.getInstance().getServer().getPlayer(uuid);
				if (player == null) {
					remove.add(uuid);
					return;
				}

				count++;

				if(count == 3) {
					player.sendMessage("");
					player.sendMessage(Color.translate("&4&l Do not disconnect!"));
					player.sendMessage(Color.translate("&c If you disconnect, you will be banned."));
					player.sendMessage("");
					player.sendMessage(Color.translate("&7 Download &fTeamSpeak &7and connect to &cts.minion.lol&7. Do not change, edit, or delete any files on your computer."));
					player.sendMessage("");

					/*player.sendMessage(CC.BLANK_LINE);
					player.sendMessage(CC.BD_RED + StringUtil.center("Do not disconnect!"));
					player.sendMessage(CC.RED + StringUtil.center("If you disconnect, you will be banned."));
					player.sendMessage(CC.YELLOW + StringUtil.center("Download " + CC.B + "TeamSpeak" + CC.YELLOW + " and connect to:"));
					player.sendMessage(CC.YELLOW + StringUtil.center("ts.minion.lol"));
					player.sendMessage(CC.BLANK_LINE);*/
					count = 0;
				}

				Location location1 = location.toBukkitLocation();

				location1.setPitch(player.getLocation().getPitch());
				location1.setYaw(player.getLocation().getYaw());

				player.teleport(location1);
			});

			remove.forEach(command.frozenPlayers::remove);
		}

	}

}
