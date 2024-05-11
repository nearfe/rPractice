package club.minion.practice.handler;

import club.minion.practice.Practice;
import club.minion.practice.match.Match;
import club.minion.practice.match.MatchState;
import club.minion.practice.player.PlayerData;
import club.minion.practice.player.PlayerState;
import dev.lugami.spigot.handler.MovementHandler;
import club.minion.practice.util.BlockUtil;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class CustomMovementHandler implements MovementHandler {

	private final Practice plugin = Practice.getInstance();

	@Override
	public void handleUpdateLocation(Player player, Location to, Location from, PacketPlayInFlying packetPlayInFlying) {
		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

		if (playerData == null) {
			this.plugin.getLogger().warning(player.getName() + "'s player data is null");
			return;
		}
		if (playerData.getPlayerState() == PlayerState.FIGHTING) {
			Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());

			if (match.getKit().isSpleef() || match.getKit().isSumo()) {
				if (BlockUtil.isOnLiquid(to, 0) || BlockUtil.isOnLiquid(to, 1)) {
				this.plugin.getMatchManager().removeFighter(player, playerData, true);
			}
				if (to.getX() != from.getX() || to.getZ() != from.getZ()) {
					if (match.getMatchState() == MatchState.STARTING) {
						player.teleport(from);
						((CraftPlayer) player).getHandle().playerConnection.checkMovement = false;
					}
				}
			}
		}
	}

	@Override
	public void handleUpdateRotation(Player player, Location location, Location location1, PacketPlayInFlying packetPlayInFlying) {

	}
}
