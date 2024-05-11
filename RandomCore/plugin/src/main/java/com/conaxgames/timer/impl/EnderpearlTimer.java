package com.conaxgames.timer.impl;

import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.StringUtil;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import com.conaxgames.timer.PlayerTimer;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class EnderpearlTimer extends PlayerTimer implements Listener {

	public EnderpearlTimer() {
		super("Enderpearl", TimeUnit.SECONDS.toMillis(15));
	}

	@Override
	protected void handleExpiry(Player player, UUID playerUUID) {
		super.handleExpiry(player, playerUUID);

		if (player == null) {
			return;
		}

		player.sendMessage(CC.SECONDARY + "Your pearl cooldown has expired!");
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if ((event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR)
				|| !event.hasItem()) {
			return;
		}
		Player player = event.getPlayer();

		if (event.getItem().getType() == Material.ENDER_PEARL) {
			long cooldown = this.getRemaining(player);
			if (cooldown > 0) {
				event.setCancelled(true);
				player.sendMessage(CC.RED + "You cannot do this for another " + StringUtil.niceTime(cooldown / 1000L, true) + " seconds.");
				/*player.sendMessage(MessageFormat
						.format("{0}You''re on pearl cooldown for {1}{2}{0}.", CC.PRIMARY, CC.SECONDARY,
								DurationFormatUtils.formatDurationWords(cooldown, true, true)));*/
				player.updateInventory();
			}
		}
	}

	@EventHandler
	public void onPearlLaunch(ProjectileLaunchEvent event) {
		if (event.getEntity().getShooter() instanceof Player) {
			if (event.getEntity() instanceof EnderPearl) {
				Player player = (Player) event.getEntity().getShooter();
				this.setCooldown(player, player.getUniqueId());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
		if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
			return;
		}
		Player player = event.getPlayer();

		if (this.getRemaining(player) != 0) {
			// Was the event cancelled?
			if (event.isCancelled()) {
				this.clearCooldown(player);
			}
		}
		event.getTo().setX((double) event.getTo().getBlockX() + 0.5D);
		event.getTo().setZ((double) event.getTo().getBlockZ() + 0.5D);
		if (event.getTo().getBlock().getType() != Material.AIR) {
			event.getTo().setY(event.getTo().getY() - (event.getTo().getY() - event.getTo().getBlockY()));
		}
	}
}
