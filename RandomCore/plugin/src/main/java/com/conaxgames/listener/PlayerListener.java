package com.conaxgames.listener;

import com.conaxgames.CorePlugin;
import com.conaxgames.api.impl.LogRequest;
import com.conaxgames.board.Board;
import com.conaxgames.disguise.DisguiseManager;
import com.conaxgames.event.player.MinemanRetrieveEvent;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import com.conaxgames.redis.CoreRedisManager;
import com.conaxgames.redis.MessageType;
import com.conaxgames.util.BanWrapper;
import com.conaxgames.util.MessageFilter;
import com.conaxgames.util.finalutil.*;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.PlayerList;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import redis.clients.jedis.Jedis;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

	private final CorePlugin plugin;

	@EventHandler(priority = EventPriority.LOWEST)
	public void onAsyncPlayerPreLoginLow(AsyncPlayerPreLoginEvent event) {
		if (!CorePlugin.SETUP) {
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, CC.RED + "Server is setting up...");
			return;
		}

		if (CorePlugin.getInstance().getShutdownTask() != null) {
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, CC.RED + "You cannot join while the server is shutting down.");
			return;
		}

		/*if(event.getAddress().getHostAddress().equals("127.0.0.1")) {
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, CC.RED + "That ip is blocked.");
			return;
		}*/

		this.plugin.getPlayerManager().addPlayer(event.getUniqueId(), event.getName(), event.getAddress());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onAsyncPlayerPreLoginHigh(AsyncPlayerPreLoginEvent event) {
		if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
			return;
		}

		Mineman mineman = this.plugin.getPlayerManager().getPlayer(event.getUniqueId());

		BanWrapper wrapper = mineman.fetchData();
		if(mineman.isBlacklisted() || (wrapper.isBanned() && !CorePlugin.getInstance().getServerManager().getServerName().toLowerCase().contains("hub"))) {
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, wrapper.getMessage());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();

		boolean allowed = true;
		if(plugin.getPlayerManager().isDevOwnerOnly() && !PlayerUtil.testPermission(player, Rank.DEVELOPER)) {
			event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, CC.RED + "The server is currently whitelisted...");
			allowed	= false;
		} else if(plugin.getPlayerManager().isDonorOnly() && !PlayerUtil.testPermission(player, Rank.BASIC)) {
			event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, CC.RED + "The server is currently in donor mode...");
			allowed	= false;
		} else if(plugin.getPlayerManager().isStaffOnly() && !PlayerUtil.testPermission(player, Rank.TRAINEE)) {
			event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, CC.RED + "The server is currently in staff mode...");
			allowed	= false;
		} else if(Bukkit.getMaxPlayers() <= Bukkit.getOnlinePlayers().size() && !PlayerUtil.testPermission(player, Rank.BASIC)) {
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, CC.RED + "The server is currently full...");
			allowed	= false;
		} else {
			event.allow();
		}

		if (!allowed) {
			plugin.getPlayerManager().removePlayer(player.getUniqueId());
		}
	}

	@EventHandler
	public void onMinemanRetrieve(MinemanRetrieveEvent event) {
		if (event.getBanWrapper().isBanned() &&
				!CorePlugin.getInstance().getServerManager().getServerName().toLowerCase().contains("hub")) {
			Player player = event.getMineman().getPlayer();
			if (player != null) {
				player.kickPlayer(event.getBanWrapper().getMessage());
			}
		} else {
			Mineman mineman = event.getMineman();
			Player player = this.plugin.getServer().getPlayer(mineman.getUuid());
			if (player != null) {
				mineman.onJoin();
				player.setPlayerListName(mineman.getRank().getColor() + player.getName() + CC.R);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);

		Player player = event.getPlayer();
		Mineman mineman = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());

		if (mineman != null && mineman.isErrorLoadingData()) {
			player.kickPlayer(StringUtil.LOAD_ERROR);
			return;
		}

		// API might be processing them slowly, so wait for the retrieve event
		if (mineman == null || !mineman.isDataLoaded()) {
			player.kickPlayer(StringUtil.LOAD_ERROR);
			return;
		}

		if (mineman.getBanData().isBanned() &&
		    !CorePlugin.getInstance().getServerManager().getServerName().toLowerCase().contains("hub")) {
			if (plugin.getPlayerManager().getDummyPlayers().contains(player.getUniqueId())) {
				Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
					JsonObject object = new JsonObject();
					object.addProperty("type", MessageType.FORCE_REMOVE.toString());
					object.addProperty("uuid", player.getUniqueId().toString());
					plugin.getServerManager().getProxyPublisher().write(object);
				});

				Bukkit.getScheduler().runTaskLater(plugin, () -> {
					PlayerList playerList = ((CraftServer) Bukkit.getServer()).getHandle();
					playerList.disconnect(((CraftPlayer) player).getHandle());
				}, 1L);
				return;
			}

			player.kickPlayer(mineman.getBanData().getMessage());
			return;
		}

		mineman.onJoin();

		if (this.plugin.getBoardManager() != null) {
			this.plugin.getBoardManager().getPlayerBoards()
			           .put(player.getUniqueId(), new Board(player, this.plugin.getBoardManager().getAdapter()));
		}

		this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
			Mineman mineman1 = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());

			if (mineman1 != null) {
				mineman1.updateTabList(mineman.getDisplayRank());
			}
		}, 5L);

        for (Mineman mineman2 : plugin.getPlayerManager().getPlayers().values()) {
            if (mineman2.isVanishMode()) {
                if (mineman.getRank().getPriority() > mineman2.getRank().getPriority()) {
                    continue;
                }
                player.hidePlayer(mineman2.getPlayer());
            }
        }

        /*
		if(mineman.hasRank(Rank.TRAINEE)) {
			Bukkit.getScheduler().runTaskLater(CorePlugin.getInstance(), () ->
					BungeeUtil.sendPermissionToBungee(player,
							mineman.getRank().getColor() + player.getName(),
							"Bloom.staff"), 1L);
		}
		*/

		switch(mineman.getWorldTime()) {
			case "DAY": player.setPlayerTime(6000L, false);
			case "NIGHT": player.setPlayerTime(18000L, false);
			case "SUNSET": player.setPlayerTime(12000L, false);
			default: player.resetPlayerTime();
		}

		/*
		this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
			CorePlugin.getInstance().getCoreRedisManager().getStorage().set("player-servers",
					event.getPlayer().getUniqueId().toString(),
					CorePlugin.getInstance().getServerManager().getServerName());

			CorePlugin.getInstance().getCoreRedisManager().getStorage().set("player-names",
					event.getPlayer().getName(),
					event.getPlayer().getUniqueId().toString());

			CorePlugin.getInstance().getCoreRedisManager().getStorage().set("player-ranks",
					event.getPlayer().getName(),
					mineman.getRank().getColor());
		});
		*/
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		Player player = event.getPlayer();
		this.plugin.getPlayerManager().removePlayer(player.getUniqueId());

		if (this.plugin.getBoardManager() != null) {
			this.plugin.getBoardManager().getPlayerBoards().remove(player.getUniqueId());
		}
	}

	@EventHandler
	public void onPlayerCommandPreProcess(PlayerCommandPreprocessEvent event) {
		// TODO: Fix this
		//this.plugin.getRequestManager().sendRequestNow(new CommandExecuteRequest(event.getMessage()));

		Player player = event.getPlayer();
		Mineman mineman = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());
		String message = event.getMessage();

		this.plugin.getRequestProcessor().sendRequestAsync(new LogRequest.CommandLogRequest(message, mineman.getId()));

		if (mineman.hasRank(Rank.TRAINEE)) {
			return;
		}

		if (player.isOp()) {
			return;
		}

		if (System.currentTimeMillis() < mineman.getCommandCooldown()) {
			event.setCancelled(true);
			player.sendMessage(StringUtil.COMMAND_COOLDOWN);
		} else {
			mineman.setCommandCooldown(System.currentTimeMillis() + 1000L);
		}
	}

	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent event) {
		if (event.getItem().getItemStack().getType() == Material.BED) {
			event.setCancelled(true);
		}

		Player player = event.getPlayer();
		Mineman mineman = plugin.getPlayerManager().getPlayer(player.getUniqueId());

		if (mineman.isVanishMode()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Mineman mineman = plugin.getPlayerManager().getPlayer(player.getUniqueId());

		if (mineman.isVanishMode()) {
			event.setCancelled(true);
		}
    }

    @EventHandler
    public void onBlockBreak(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Mineman mineman = plugin.getPlayerManager().getPlayer(player.getUniqueId());

		if (!mineman.isVanishMode()) {
			event.setCancelled(true);
		}
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		Mineman mineman = plugin.getPlayerManager().getPlayer(player.getUniqueId());

		if (mineman.isVanishMode()) {
			event.setCancelled(true);
		}
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		Mineman mineman = plugin.getPlayerManager().getPlayer(player.getUniqueId());

        if (mineman.isVanishMode()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
	    if (event.getReason() == EntityTargetEvent.TargetReason.CUSTOM) {
	        return;
        }

        Entity entity = event.getEntity();
	    Entity target = event.getTarget();
        if ((entity instanceof ExperienceOrb || entity instanceof LivingEntity) && target instanceof Player && this.plugin.getPlayerManager().getPlayer(target.getUniqueId()).isVanishMode()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        EntityDamageEvent.DamageCause cause = event.getCause();
        if (cause == EntityDamageEvent.DamageCause.VOID || cause == EntityDamageEvent.DamageCause.SUICIDE) {
            return;
        }

        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            Mineman mineman = plugin.getPlayerManager().getPlayer(player.getUniqueId());

            if (mineman.isVanishMode()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        EntityDamageEvent.DamageCause cause = event.getCause();
        if (cause == EntityDamageEvent.DamageCause.VOID || cause == EntityDamageEvent.DamageCause.SUICIDE) {
            return;
        }

        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (plugin.getPlayerManager().getPlayer(player.getUniqueId()).isVanishMode()) {
                event.setCancelled(true);
            }
        }
        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            if (plugin.getPlayerManager().getPlayer(damager.getUniqueId()).isVanishMode()) {
                event.setCancelled(true);
            }
        }
    }

	//	@EventHandler
	//	public void onPlayerChatTabComplete(PlayerChatTabCompleteEvent event) {
	//		event.getTabCompletions().removeIf(str -> this.plugin.getServer().getPlayer(str) != null && !event
	// .getPlayer()
	// .canSee(this.plugin.getServer().getPlayer(str)));
	//	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);

		Player player = event.getPlayer();

		this.plugin.getPlayerManager().removePlayer(player.getUniqueId());

		if (this.plugin.getBoardManager() != null) {
			this.plugin.getBoardManager().getPlayerBoards().remove(player.getUniqueId());
		}

		this.plugin.getDisguiseManager().getDisguiseData().remove(player.getUniqueId());
		this.plugin.getDisguiseManager().getOriginalCache().remove(player.getUniqueId());

		player.getInventory().remove(Material.MAP);

		/*
		this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
			CorePlugin.getInstance().getCoreRedisManager().getStorage()
					.remove("player-servers", event.getPlayer().getUniqueId().toString());

			CorePlugin.getInstance().getCoreRedisManager().getStorage()
					.remove("player-names", event.getPlayer().getName());

			CorePlugin.getInstance().getCoreRedisManager().getStorage()
					.remove("player-ranks", event.getPlayer().getName());
		});
		*/
	}

	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		Mineman mineman = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());


		if (mineman.isMuted()) {
			if (mineman.getMuteTime() != null && System.currentTimeMillis() - mineman.getMuteTime().getTime() > 0L) {
				mineman.setMuted(false);
				mineman.setMuteTime(new Timestamp(0L));
			} else {
				if (mineman.getMuteTime() == null) {
					player.sendMessage(StringUtil.PERMANENT_MUTE);
				} else {
					player.sendMessage(String.format(StringUtil.TEMPORARY_MUTE,
							TimeUtil.millisToRoundedTime(
									Math.abs(System.currentTimeMillis() - mineman.getMuteTime().getTime()))));
				}

				event.setCancelled(true);
				return;
			}
		}

		if (mineman.isBanned()) {
			player.sendMessage(CC.RED + "You cannot send chat messages while banned.");
			event.setCancelled(true);
			return;
		}

		Rank rank = mineman.getRank();

		if (CoreRedisManager.handleMessage(mineman, event.getMessage())) {
			event.setCancelled(true);
			return;
		}

		if (this.plugin.getPlayerManager().isChatSilenced() && !rank.hasRank(Rank.HOST)) {
			player.sendMessage(CC.RED + "You cannot speak while chat is silenced.");
			event.setCancelled(true);
			return;
		}

		if (!mineman.hasRank(Rank.BASIC)) {
			long slowChat = this.plugin.getPlayerManager().getChatSlowDownTime();
			if (System.currentTimeMillis() < mineman.getChatCooldown()) {
				player.sendMessage(slowChat > 0L ? StringUtil.SLOW_CHAT.replace("%time%", DurationFormatUtils.formatDurationWords(slowChat, true, true)) : StringUtil.CHAT_COOLDOWN);
				event.setCancelled(true);
				return;
			} else {
				mineman.setChatCooldown(System.currentTimeMillis() + (slowChat > 0L ? slowChat : 3000L));
			}
		}

		rank = mineman.getDisplayRank();

		if (MessageFilter.shouldFilter(event.getMessage())) {
			if (mineman.hasRank(Rank.HOST)) {
				player.sendMessage(CC.RED + "That would have been filtered.");
			} else {
				String color = mineman.getCustomColor() != null && !mineman.getCustomColor().isEmpty() && mineman.hasRank(Rank.BASIC) ? mineman
						.getCustomColor() : rank.getColor();
				String formattedMessage = String
						.format(rank.getPrefix() + color + "%1$s" + CC.R + ": %2$s", player.getName(),
								event.getMessage());

				PlayerUtil.messageRank(CC.RED + "[Filtered] " + formattedMessage);
				player.sendMessage(formattedMessage);
				event.setCancelled(true);
				return;
			}
		}

		if(event.getMessage().contains("IÌ‡") || event.getMessage().contains("İ")) {
			event.setCancelled(true);
			plugin.getFilterManager().handleCommand("mute " + player.getName() + " Sending crash codes -s");
			player.sendMessage(Color.translate("&cYou have been muted for &eCrash Codes&c."));
			player.sendMessage(Color.translate("&cIf you beleive this is false, join our TeamSpeak (ts.minion.lol)"));
			return;
		}

		if(!PlayerUtil.testPermission(player, Rank.HOST) && plugin.getFilterManager().isFiltered(player, mineman, event.getMessage())) {
			event.setCancelled(true);
			return;
		}

		String color = mineman.getCustomColor() != null && !mineman.getCustomColor().isEmpty() && mineman.hasRank(Rank.BASIC) ? mineman.getCustomColor() : rank.getColor();
		String prefix = mineman.getCustomPrefix().equals("") ? "" : mineman.getCustomPrefix() + " ";
		event.setFormat(prefix + rank.getPrefix() + color +  player.getName() + CC.R + ": %2$s");

		List<Player> recipientList = new ArrayList<>(event.getRecipients());
		for (Player recipient : recipientList) {
			Mineman recipientMineman = this.plugin.getPlayerManager().getPlayer(recipient.getUniqueId());
			if (!mineman.hasRank(Rank.TRAINEE) && recipientMineman != null && recipientMineman.isDataLoaded()) {
				if (recipientMineman.isIgnoring(mineman.getId()) || !recipientMineman.isChatEnabled()) {
					event.getRecipients().remove(recipient);
				}
			}
		}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Mineman mineman = plugin.getPlayerManager().getPlayer(player.getUniqueId());

			}
		}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Mineman mineman = plugin.getPlayerManager().getPlayer(player.getUniqueId());


		if (event.getClickedInventory() != null && event.getClickedInventory().getTitle().contains("Inventory: ")) {
			ItemStack stack = event.getCurrentItem();
			if (stack == null || stack.getType() == Material.AIR || !stack.hasItemMeta()) {
				return;
			}

			if (!mineman.hasRank(Rank.ADMIN)) {
				event.setCancelled(true);
			}
		}
	}
}
