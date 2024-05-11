package club.minion.practice.listeners;

import club.minion.practice.Practice;
import club.minion.practice.match.Match;
import club.minion.practice.match.MatchState;
import club.minion.practice.player.PlayerData;
import club.minion.practice.player.PlayerState;
import com.conaxgames.util.finalutil.CC;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EntityListener implements Listener {
	private final Practice plugin = Practice.getInstance();

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

			switch (playerData.getPlayerState()) {
				case FIGHTING:
					Match match = plugin.getMatchManager().getMatch(playerData);
					if (match.getMatchState() != MatchState.FIGHTING) {
						e.setCancelled(true);
					}
					if ((match.getKit().isSumo() && e.getCause() == EntityDamageEvent.DamageCause.FALL) || (player.getFallDistance() >= 50 && e.getCause() == EntityDamageEvent.DamageCause.FALL) || match.getKit().isBoxing() && e.getCause() == EntityDamageEvent.DamageCause.FALL) {
						e.setCancelled(true);
					}
					break;
				case SPECTATING:
					if (e.getCause() == EntityDamageEvent.DamageCause.FIRE || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
						player.setFireTicks(0);
						e.setCancelled(true);
						return;
					}
				default:
					if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
						e.getEntity().teleport(this.plugin.getSpawnManager().getSpawnLocation().toBukkitLocation());
					}
					e.setCancelled(true);
					break;

			}
		}
	}

	int blockedHitsCounter =0 ;
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event  ) {
		Player entity = (Player) event.getEntity();

		Player damager;

		Player player = (Player) event.getEntity();

		if (event.getDamager() instanceof Player) {
			damager = (Player) event.getDamager();
		} else if (event.getDamager() instanceof Projectile) {
			damager = (Player) ((Projectile) event.getDamager()).getShooter();
		} else {
			return;
		}

		PlayerData entityData = this.plugin.getPlayerManager().getPlayerData(entity.getUniqueId());
		PlayerData damagerData = this.plugin.getPlayerManager().getPlayerData(damager.getUniqueId());

		if (damagerData.getPlayerState() != PlayerState.FIGHTING ||
				entityData.getPlayerState() != PlayerState.FIGHTING) {
			event.setCancelled(true);
			return;

		}

		Match match = this.plugin.getMatchManager().getMatch(entityData);

		if (damagerData.getTeamID() == entityData.getTeamID() && !match.isFFA()) {
			event.setCancelled(true);
			return;
		}


		if (event.getDamager() instanceof Player) {
			damagerData.setCombo(damagerData.getCombo() + 1);
			damagerData.setHits(damagerData.getHits() + 1);
			if (damagerData.getCombo() > damagerData.getLongestCombo()) {
				damagerData.setLongestCombo(damagerData.getCombo());
			}
			entityData.setCombo(0);
			if (match.getKit().isBuild() || match.getKit().isSumo()) {
				event.setDamage(0.0);
			}

			if (match.getMatchState() != MatchState.FIGHTING) {
				blockedHitsCounter = 0;
				return;
			}
			if (damagerData.getTeamID() == entityData.getTeamID() && !match.isFFA()) {
				event.setCancelled(true);
				return;
			}

			if (entity.isBlocking()) {
				blockedHitsCounter = 0;
				entityData.incrementBlockedHits();
			} else {
				blockedHitsCounter++;
			}


		} else if (event.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getDamager();

			if (arrow.getShooter() instanceof Player) {
				Player shooter = (Player) arrow.getShooter();

				PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

				if (!entity.getName().equals(shooter.getName())) {
					double health = Math.ceil(entity.getHealth() - event.getFinalDamage()) / 2.0D;

					if (health > 0.0D) {
						shooter.sendMessage(CC.SECONDARY + entity.getName() + CC.PRIMARY + " is now at "
								+ CC.SECONDARY + health + "❤" + CC.PRIMARY + ".");
					}
				}
			}
		}
	}

	@EventHandler
	public void onPotionSplash(PotionSplashEvent e) {
		if (!(e.getEntity().getShooter() instanceof Player)) {
			return;
		}
		for (PotionEffect effect : e.getEntity().getEffects()) {
			if (effect.getType().equals(PotionEffectType.HEAL)) {

				Player shooter = (Player) e.getEntity().getShooter();

				if (e.getIntensity(shooter) <= 0.5D) {
					PlayerData shooterData = this.plugin.getPlayerManager().getPlayerData(shooter.getUniqueId());

					if (shooterData != null) {
						shooterData.setMissedPots(shooterData.getMissedPots() + 1);
					}
				}
				break;
					}
			}
		}
	}
