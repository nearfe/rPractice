// =============================================== //
// Recompile disabled. Please run Recaf with a JDK //
// =============================================== //

// Decompiled with: CFR 0.152
// Class Version: 8
package club.minion.practice.listeners;

import club.minion.practice.Practice;
import club.minion.practice.events.PracticeEvent;
import club.minion.practice.events.oitc.OITCEvent;
import club.minion.practice.events.oitc.OITCPlayer;
import club.minion.practice.ffa.killstreak.KillStreak;
import club.minion.practice.kit.Kit;
import club.minion.practice.kit.PlayerKit;
import club.minion.practice.match.Match;
import club.minion.practice.match.MatchState;
import club.minion.practice.party.Party;
import club.minion.practice.player.PlayerData;
import club.minion.practice.player.PlayerState;
import club.minion.practice.player.PracticePlayerData;
import club.minion.practice.task.RespawnTask;
import club.minion.practice.util.ItemUtil;
import club.minion.practice.util.PlayerUtil;
import com.conaxgames.event.player.MinemanRetrieveEvent;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.StringUtil;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityLightning;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityWeather;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerListener
		implements Listener {
	private final Practice plugin = Practice.getInstance();

	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		if (event.getItem().getType() == Material.GOLDEN_APPLE) {
			if (!event.getItem().hasItemMeta() || !event.getItem().getItemMeta().getDisplayName().contains("Golden Head")) {
				return;
			}
			PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(event.getPlayer().getUniqueId());
			if (playerData.getPlayerState() == PlayerState.FIGHTING) {
				Player player = event.getPlayer();
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
				player.setFoodLevel(Math.min(player.getFoodLevel() + 6, 20));
			}
		}
	}

	@EventHandler
	public void onRegenerate(EntityRegainHealthEvent event) {
		Match match;
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		if (event.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED) {
			return;
		}
		Player player = (Player)event.getEntity();
		PracticePlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
		if (playerData.getPlayerState() == PlayerState.FIGHTING && (match = this.plugin.getMatchManager().getMatch(player.getUniqueId())) != null && match.getKit() != null && match.getKit().isBoxing()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		this.plugin.getPlayerManager().createPlayerData(player);
		this.plugin.getPlayerManager().sendToSpawnAndReset(player);
		player.sendMessage("");
		player.sendMessage(StringUtil.getBorderLine());
		player.sendMessage("");
		player.sendMessage(StringUtil.center((String)(CC.PRIMARY + "Welcome to Practice.")));
		player.sendMessage("");
		player.sendMessage(StringUtil.getBorderLine());
		player.sendMessage("");
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
		if (playerData == null) {
			return;
		}
		switch (playerData.getPlayerState()) {
			case FIGHTING: {
				this.plugin.getMatchManager().removeFighter(player, playerData, false);
				break;
			}
			case SPECTATING: {
				this.plugin.getMatchManager().removeSpectator(player);
				break;
			}
			case EDITING: {
				this.plugin.getEditorManager().removeEditor(player.getUniqueId());
				break;
			}
			case QUEUE: {
				if (party == null) {
					this.plugin.getQueueManager().removePlayerFromQueue(player);
					break;
				}
				if (!this.plugin.getPartyManager().isLeader(player.getUniqueId())) break;
				this.plugin.getQueueManager().removePartyFromQueue(party);
				break;
			}
			case FFA: {
				this.plugin.getFfaManager().removePlayer(player);
				break;
			}
			case EVENT: {
				PracticeEvent practiceEvent = this.plugin.getEventManager().getEventPlaying(player);
				if (practiceEvent == null) break;
				practiceEvent.leave(player, true);
			}
		}
		this.plugin.getTournamentManager().leaveTournament(player);
		this.plugin.getPartyManager().leaveParty(player);
		this.plugin.getMatchManager().removeMatchRequests(player.getUniqueId());
		this.plugin.getPartyManager().removePartyInvites(player.getUniqueId());
		this.plugin.getPlayerManager().removePlayerData(player.getUniqueId());
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		block61: {
			Player player = event.getPlayer();
			if (player.getGameMode() == GameMode.CREATIVE) {
				return;
			}
			PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
			if (playerData.getPlayerState() == PlayerState.SPECTATING) {
				event.setCancelled(true);
			}
			if (event.getAction().name().endsWith("_BLOCK")) {
				Sign sign;
				if (event.getClickedBlock().getType().name().contains("SIGN") && event.getClickedBlock().getState() instanceof Sign && ChatColor.stripColor((String)(sign = (Sign)event.getClickedBlock().getState()).getLine(1)).equals("[Soup]")) {
					event.setCancelled(true);
					Inventory inventory = this.plugin.getServer().createInventory(null, 54, CC.DARK_GRAY + "Soup Refill");
					for (int i = 0; i < 54; ++i) {
						inventory.setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
					}
					event.getPlayer().openInventory(inventory);
				}
				if (event.getClickedBlock().getType() == Material.CHEST || event.getClickedBlock().getType() == Material.ENDER_CHEST) {
					event.setCancelled(true);
				}
			}
			if (!event.getAction().name().startsWith("RIGHT_")) break block61;
			ItemStack item = event.getItem();
			Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
			block0 : switch (playerData.getPlayerState()) {
				case LOADING: {
					player.sendMessage(CC.RED + "You must wait until your player data has loaded before you can use items.");
					break;
				}
				case FFA: {
					if (item == null) {
						return;
					}
					switch (item.getType()) {
						case MUSHROOM_SOUP: {
							if (!(player.getHealth() <= 19.0) || player.isDead()) break;
							if (player.getHealth() < 20.0 || player.getFoodLevel() < 20) {
								player.getItemInHand().setType(Material.BOWL);
							}
							player.setHealth(player.getHealth() + 7.0 > 20.0 ? 20.0 : player.getHealth() + 7.0);
							player.setFoodLevel(player.getFoodLevel() + 2 > 20 ? 20 : player.getFoodLevel() + 2);
							player.setSaturation(12.8f);
							player.updateInventory();
						}
					}
					break;
				}
				case FIGHTING: {
					if (item == null) {
						return;
					}
					Match match = this.plugin.getMatchManager().getMatch(playerData);
					switch (item.getType()) {
						case ENDER_PEARL: {
							if (match.getMatchState() != MatchState.STARTING) break;
							event.setCancelled(true);
							player.sendMessage(CC.RED + "You can't throw pearls right now!");
							player.updateInventory();
							break;
						}
						case MUSHROOM_SOUP: {
							if (!(player.getHealth() <= 19.0) || player.isDead()) break;
							if (player.getHealth() < 20.0 || player.getFoodLevel() < 20) {
								player.getItemInHand().setType(Material.BOWL);
							}
							player.setHealth(Math.min(player.getHealth() + 7.0, 20.0));
							player.setFoodLevel(Math.min(player.getFoodLevel() + 2, 20));
							player.setSaturation(12.8f);
							player.updateInventory();
							break;
						}
						case ENCHANTED_BOOK: {
							Kit kit = match.getKit();
							PlayerInventory inventory = player.getInventory();
							int kitIndex = inventory.getHeldItemSlot();
							if (kitIndex == 8) {
								kit.applyToPlayer(player);
								break;
							}
							Map<Integer, PlayerKit> kits = playerData.getPlayerKits(kit.getName());
							PlayerKit playerKit = kits.get(kitIndex + 1);
							if (playerKit == null) break;
							playerKit.applyToPlayer(player);
						}
					}
					break;
				}
				case SPAWN: {
					if (item == null) {
						return;
					}
					switch (item.getType()) {
						case DIAMOND_SWORD: {
							if (party != null) {
								player.sendMessage(CC.RED + "You can't join the Premium Queue while in a party.");
								return;
							}
							if (playerData.getPremiumMatches() <= 0) {
								player.sendMessage(CC.SECONDARY + "You don't have any " + CC.PRIMARY + "Premium Matches " + CC.SECONDARY + "remaining! Purchase more here: " + CC.PRIMARY + "https://store.minion.club");
								return;
							}
							player.openInventory(this.plugin.getInventoryManager().getJoinPremiumInventory().getCurrentPage());
							break;
						}
						case IRON_SWORD: {
							if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
								player.sendMessage(CC.RED + "Only the party leader can join the 2v2 queue.");
								return;
							}
							player.openInventory(this.plugin.getInventoryManager().getRankedInventory().getCurrentPage());
							break;
						}
						case STONE_SWORD: {
							if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
								player.sendMessage(CC.RED + "Only the party leader can join the 2v2 queue.");
								return;
							}
							player.openInventory(this.plugin.getInventoryManager().getUnrankedInventory().getCurrentPage());
							break;
						}
						case BLAZE_POWDER: {
							UUID rematching = this.plugin.getMatchManager().getRematcher(player.getUniqueId());
							Player rematcher = this.plugin.getServer().getPlayer(rematching);
							if (rematcher == null) {
								player.sendMessage(CC.RED + "Player is no longer online.");
								return;
							}
							if (this.plugin.getMatchManager().getMatchRequest(rematcher.getUniqueId(), player.getUniqueId()) != null) {
								this.plugin.getServer().dispatchCommand((CommandSender)player, "accept " + rematcher.getName());
								break;
							}
							this.plugin.getServer().dispatchCommand((CommandSender)player, "duel " + rematcher.getName());
							break;
						}
						case PAPER: {
							if (!this.plugin.getMatchManager().isRematching(player.getUniqueId())) break;
							this.plugin.getServer().dispatchCommand((CommandSender)player, "inv " + this.plugin.getMatchManager().getRematcherInventory(player.getUniqueId()));
							break;
						}
						case NAME_TAG: {
							this.plugin.getPartyManager().createParty(player);
							break;
						}
						case BOOK: {
							player.openInventory(this.plugin.getInventoryManager().getEditorInventory().getCurrentPage());
							break;
						}
						case WATCH: {
							player.performCommand("settings");
							break;
						}
						case DIAMOND_AXE: {
							if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
								player.sendMessage(CC.RED + "Only the party leader can start events.");
								return;
							}
							player.openInventory(this.plugin.getInventoryManager().getPartyEventInventory().getCurrentPage());
							break;
						}
						case IRON_AXE: {
							if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
								player.sendMessage(CC.RED + "Only the party leader can start events.");
								return;
							}
							player.openInventory(this.plugin.getInventoryManager().getPartyInventory().getCurrentPage());
							break;
						}
						case NETHER_STAR: {
							this.plugin.getPartyManager().leaveParty(player);
							this.plugin.getTournamentManager().leaveTournament(player);
						}
					}
					break;
				}
				case QUEUE: {
					if (item == null) {
						return;
					}
					if (item.getType() != Material.REDSTONE) break;
					if (party == null) {
						this.plugin.getQueueManager().removePlayerFromQueue(player);
						break;
					}
					this.plugin.getQueueManager().removePartyFromQueue(party);
					break;
				}
				case SPECTATING: {
					if (item == null) {
						return;
					}
					if (item.getType() == Material.REDSTONE) {
						if (party != null) break;
						this.plugin.getMatchManager().removeSpectator(player);
						break;
					}
					if (item.getType() != Material.NETHER_STAR) break;
					this.plugin.getPartyManager().leaveParty(player);
					break;
				}
				case EDITING: {
					if (event.getClickedBlock() == null) {
						return;
					}
					switch (event.getClickedBlock().getType()) {
						case WALL_SIGN:
						case SIGN:
						case SIGN_POST: {
							this.plugin.getEditorManager().removeEditor(player.getUniqueId());
							this.plugin.getPlayerManager().sendToSpawnAndReset(player);
							break block0;
						}
						case CHEST: {
							Kit kit = this.plugin.getKitManager().getKit(this.plugin.getEditorManager().getEditingKit(player.getUniqueId()));
							if (kit.getKitEditContents()[0] != null) {
								Inventory editorInventory = this.plugin.getServer().createInventory(null, 36);
								editorInventory.setContents(kit.getKitEditContents());
								player.openInventory(editorInventory);
								event.setCancelled(true);
								break block0;
							}
							break block61;
						}
						case ANVIL: {
							player.openInventory(this.plugin.getInventoryManager().getEditingKitInventory(player.getUniqueId()).getCurrentPage());
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
		Material drop = event.getItemDrop().getItemStack().getType();
		switch (playerData.getPlayerState()) {
			case FFA: {
				if (drop != Material.BOWL) {
					event.setCancelled(true);
					break;
				}
				event.getItemDrop().remove();
				break;
			}
			case FIGHTING: {
				if (drop == Material.ENCHANTED_BOOK) {
					event.setCancelled(true);
					break;
				}
				if (drop == Material.GLASS_BOTTLE) {
					event.getItemDrop().remove();
					break;
				}
				Match match = this.plugin.getMatchManager().getMatch(event.getPlayer().getUniqueId());
				this.plugin.getMatchManager().addDroppedItem(match, event.getItemDrop());
				break;
			}
			default: {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		Material drop = event.getItemDrop().getItemStack().getType();
		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
		switch (playerData.getPlayerState()) {
			case FIGHTING: {
				if (drop != Material.DIAMOND_SWORD) break;
				event.setCancelled(true);
				player.sendMessage(CC.RED + "You can't drop your weapon in 1v1s.");
				break;
			}
			default: {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
		if (playerData.getPlayerState() == PlayerState.FIGHTING) {
			Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
			if (match.getEntitiesToRemove().contains(event.getItem())) {
				match.removeEntityToRemove((org.bukkit.entity.Entity)event.getItem());
			} else {
				event.setCancelled(true);
			}
		} else if (playerData.getPlayerState() != PlayerState.FFA) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
		String chatMessage = event.getMessage();
		if (party != null) {
			if (chatMessage.startsWith("!") || chatMessage.startsWith("@")) {
				event.setCancelled(true);
				String message = CC.PRIMARY + "[Party] " + CC.PRIMARY + player.getName() + CC.R + ": " + chatMessage.replaceFirst("!", "").replaceFirst("@", "");
				party.broadcast(message);
			}
		} else {
			PlayerKit kitRenaming = this.plugin.getEditorManager().getRenamingKit(player.getUniqueId());
			if (kitRenaming != null) {
				kitRenaming.setDisplayName(ChatColor.translateAlternateColorCodes((char)'&', (String)chatMessage));
				event.setCancelled(true);
				event.getPlayer().sendMessage(CC.PRIMARY + "Set kit " + CC.SECONDARY + kitRenaming.getIndex() + CC.PRIMARY + "'s name to " + CC.SECONDARY + kitRenaming.getDisplayName());
				this.plugin.getEditorManager().removeRenamingKit(event.getPlayer().getUniqueId());
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		Player killer = event.getEntity().getKiller();
		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
		Location deathLocation = player.getLocation();
		switch (playerData.getPlayerState()) {
			case FIGHTING: {
				this.plugin.getMatchManager().removeFighter(player, playerData, true);
				event.getDrops().clear();
				RespawnTask.scheduleRespawn(player);
				break;
			}
			case EVENT: {
				PracticeEvent currentEvent = this.plugin.getEventManager().getEventPlaying(player);
				if (currentEvent == null) break;
				if (currentEvent instanceof OITCEvent) {
					OITCEvent oitcEvent = (OITCEvent)currentEvent;
					OITCPlayer oitcKiller = (OITCPlayer)oitcEvent.getPlayer(player.getKiller());
					OITCPlayer oitcPlayer = (OITCPlayer)oitcEvent.getPlayer(player);
					PlayerUtil.respawnPlayer(event);
					break;
				}
				currentEvent.onDeath().accept(player);
				break;
			}
			case FFA: {
				String deathMessage;
				for (ItemStack item : player.getInventory().getContents()) {
					if (item == null || item.getType() != Material.MUSHROOM_SOUP) continue;
					this.plugin.getFfaManager().getItemTracker().put(player.getWorld().dropItemNaturally(player.getLocation(), item), System.currentTimeMillis());
				}
				this.plugin.getFfaManager().getKillStreakTracker().put(player.getUniqueId(), 0);
				if (player.getKiller() == null) {
					deathMessage = ChatColor.RED + player.getName() + ChatColor.RED + " has been eliminated.";
				} else {
					deathMessage = ChatColor.RED + player.getName() + ChatColor.RED + " has been eliminated by " + ChatColor.GREEN + player.getKiller().getName();
					int ks = this.plugin.getFfaManager().getKillStreakTracker().compute(player.getKiller().getUniqueId(), (k, v) -> (v == null ? 0 : v) + 1);
					for (KillStreak killStreak : this.plugin.getFfaManager().getKillStreaks()) {
						if (!killStreak.getStreaks().contains(ks)) continue;
						killStreak.giveKillStreak(player.getKiller());
						for (PlayerData data : this.plugin.getPlayerManager().getAllData()) {
							if (data.getPlayerState() != PlayerState.FFA) continue;
							deathMessage = deathMessage + "\n" + ChatColor.WHITE.toString() + ChatColor.BOLD + "* " + ChatColor.GREEN + player.getKiller().getName() + ChatColor.GRAY + " is on a " + ChatColor.RED.toString() + ChatColor.BOLD + ks + ChatColor.GRAY + " kill streak.";
						}
					}
				}
				for (PlayerData data2 : this.plugin.getPlayerManager().getAllData()) {
					if (data2.getPlayerState() != PlayerState.FFA) continue;
					Player ffaPlayer = this.plugin.getServer().getPlayer(data2.getUniqueId());
					ffaPlayer.sendMessage(deathMessage);
				}
				this.plugin.getServer().getScheduler().runTask((Plugin)this.plugin, () -> this.plugin.getFfaManager().removePlayer(event.getEntity()));
				break;
			}
		}
		event.setDroppedExp(0);
		event.setDeathMessage(null);
		RespawnTask.scheduleRespawn(player);
	}

	private void spawnLightning(Player killer, Location location) {
		EntityLightning lightning = new EntityLightning(((CraftPlayer)killer).getHandle().getWorld(), location.getX(), location.getY(), location.getZ(), false);
		PacketPlayOutSpawnEntityWeather packet = new PacketPlayOutSpawnEntityWeather((Entity)lightning);
		((CraftPlayer)killer).getHandle().playerConnection.sendPacket((Packet)packet);
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		Player player = (Player)event.getEntity();
		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
		if (playerData.getPlayerState() == PlayerState.FIGHTING) {
			Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
			if (match.getKit().isBoxing() || match.getKit().isSumo() || this.plugin.getEventManager().getEventPlaying(player) != null) {
				event.setCancelled(true);
			}
		} else {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		if (event.getEntity().getShooter() instanceof Player) {
			Player shooter = (Player)event.getEntity().getShooter();
			PlayerData shooterData = this.plugin.getPlayerManager().getPlayerData(shooter.getUniqueId());
			if (shooterData.getPlayerState() == PlayerState.FIGHTING) {
				Match match = this.plugin.getMatchManager().getMatch(shooter.getUniqueId());
				match.addEntityToRemove((org.bukkit.entity.Entity)event.getEntity());
			}
		}
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		if (event.getEntity().getShooter() instanceof Player) {
			Player shooter = (Player)event.getEntity().getShooter();
			PlayerData shooterData = this.plugin.getPlayerManager().getPlayerData(shooter.getUniqueId());
			if (shooterData != null && shooterData.getPlayerState() == PlayerState.FIGHTING) {
				Match match = this.plugin.getMatchManager().getMatch(shooter.getUniqueId());
				match.removeEntityToRemove((org.bukkit.entity.Entity)event.getEntity());
				if (event.getEntityType() == EntityType.ARROW) {
					event.getEntity().remove();
				}
			}
		}
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
		switch (playerData.getPlayerState()) {
			case FIGHTING: {
				RespawnTask.scheduleRespawn(player);
				break;
			}
			case EVENT: {
				PracticeEvent currentEvent = this.plugin.getEventManager().getEventPlaying(player);
				if (currentEvent == null || !(currentEvent instanceof OITCEvent)) break;
				event.setRespawnLocation(player.getLocation());
				currentEvent.onDeath().accept(player);
			}
		}
	}

	private void respawnPlayer(Player player, Location location) {
		Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, () -> {
			player.teleport(location);
			PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
			Match match = this.plugin.getMatchManager().getMatch(playerData);
			Collection<PlayerKit> playerKits = playerData.getPlayerKits(match.getKit().getName()).values();
			if (playerKits.size() == 0) {
				match.getKit().applyToPlayer(player);
			} else {
				player.getInventory().setItem(8, this.plugin.getItemManager().getDefaultBook());
				int slot = -1;
				for (PlayerKit playerKit : playerKits) {
					player.getInventory().setItem(++slot, ItemUtil.createItem(Material.ENCHANTED_BOOK, CC.PRIMARY + playerKit.getDisplayName()));
				}
			}
			player.updateInventory();
		}, 60L);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.LIGHTNING) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onMinemanRetrieve(MinemanRetrieveEvent event) {
		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(event.getUniqueId());
		if (playerData != null) {
			playerData.setMinemanID(event.getMineman().getId());
		}
	}
}
 