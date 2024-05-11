package com.conaxgames.manager;

import com.conaxgames.api.abstr.AbstractBukkitCallback;
import com.conaxgames.api.impl.PunishmentRequest;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.BanWrapper;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.PlayerUtil;
import com.conaxgames.util.finalutil.StringUtil;
import com.conaxgames.util.finalutil.TimeUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.conaxgames.CorePlugin;
import com.conaxgames.clickable.Clickable;
import com.conaxgames.command.impl.punish.PunishCommand;

import java.sql.Timestamp;
import java.text.MessageFormat;

@RequiredArgsConstructor
public class PunishmentManager {

    private final CorePlugin plugin;

    private void broadcastPunishment(String type, String reason, String punished, String punisher, boolean global, String duration) {
        Player player = this.plugin.getServer().getPlayer(punished);
        if (player != null) {
            punished = player.getName();
        }

        if (global) {
            for (Player o : Bukkit.getOnlinePlayers()) {
                if (PlayerUtil.testPermission(o, Rank.TRAINEE)) {
                    new Clickable(CC.RED + punished + " was " + type + " by " + punisher + ".", CC.SECONDARY + "Added by: " + CC.PRIMARY + punisher + "\n" + CC.SECONDARY + "Reason: " + CC.PRIMARY + reason + (duration != null ? "\n" + CC.SECONDARY + "Duration: " + CC.PRIMARY + duration : ""), "").sendToPlayer(o);
                } else {
                    o.sendMessage(CC.RED + punished + " was " + type + " by " + punisher + ".");
                }
            }

            //this.plugin.getServer().broadcastMessage(CC.RED + punished + " was " + type + " by " + punisher + ".");
            //this.plugin.getServer().broadcastMessage(punished + CC.GREEN + " was " + type + " by " + punisher + CC.GREEN + ".");
        } else {
            for (Player o : Bukkit.getOnlinePlayers()) {
                if (PlayerUtil.testPermission(o, Rank.TRAINEE)) {
                    new Clickable(CC.GRAY + "[S] " + CC.RED + punished + " was " + type + " by " + punisher + ".", CC.SECONDARY + "Added by: " + CC.PRIMARY + punisher + "\n" + CC.SECONDARY + "Reason: " + CC.PRIMARY + reason + (duration != null ? "\n" + CC.SECONDARY + "Duration: " + CC.PRIMARY + duration : ""), "").sendToPlayer(o);
                }
            }

            //PlayerUtil.messageRank(CC.GRAY + "[Silent] " + CC.RED + punished + " was " + type + " by " + punisher + " for " + reason + ".");
            //PlayerUtil.messageRank(CC.GRAY + "[Silent] " + punished + CC.GREEN + " was " + type + " by " + punisher + CC.GREEN + " for " + CC.DARK_GREEN + reason + CC.GREEN + ".");
        }
    }

    public void punish(CommandSender punisher,
                       PunishCommand.PunishType type,
                       String target, String reason, String ip,
                       Timestamp expiry,
                       boolean silent, boolean temporary) {

        int id = punisher instanceof Player ?
                this.plugin.getPlayerManager().getPlayer(((Player) punisher).getUniqueId()).getId() : -1;
        String name = punisher instanceof Player ?
                plugin.getPlayerManager().getPlayer(((Player) punisher).getUniqueId()).getName() : punisher.getName();

        String finalType;
        if (type.getName().toLowerCase().startsWith("un")) {
            finalType = type.getName();
        } else if (temporary) {
            finalType = "temp-" + type.getName();
        } else {
            finalType = "perm-" + type.getName();
        }

        Player targetPlayer = this.plugin.getServer().getPlayerExact(target);

        if (type == PunishCommand.PunishType.KICK && targetPlayer == null) {
            punisher.sendMessage(CC.RED + "Player not online.");
            return;
        }

        final Timestamp finalExpiry = expiry;
        final String finalExpiryTime = finalExpiry == null ? "" :
                TimeUtil.millisToRoundedTime(
                        Math.abs(System.currentTimeMillis() - finalExpiry.getTime()));

        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                    String playerName;
                    String disguisedName;
                    if (targetPlayer != null) {
                        playerName = CorePlugin.getInstance().getPlayerManager().getPlayer(targetPlayer.getUniqueId()).getName();
                        disguisedName = targetPlayer.getName();
                    } else {
                        playerName = CorePlugin.getRequestNameOrUUID(target);
                        disguisedName = target;
                    }

                    String server = CorePlugin.getInstance().getServerManager().getServerName();
                    this.plugin.getRequestProcessor().sendRequest(
                            new PunishmentRequest(finalExpiry, ip, reason, playerName, finalType, -1, id), new AbstractBukkitCallback() {
                                @Override
                                public void callback(JsonElement element) {
                                    JsonObject data = element.getAsJsonObject();

                                    String response = data.get("response").getAsString();

                                    switch (response) {
                                        case "player-never-joined":
                                            punisher.sendMessage(CC.RED + "Player has never joined.");
                                            break;
                                        case "player-not-found":
                                        case "invalid-player":
                                            punisher.sendMessage(CC.RED + "Failed to find that player.");
                                            break;
                                        case "not-muted":
                                            punisher.sendMessage(CC.RED + "Player not muted.");
                                            break;
                                        case "not-banned":
                                            punisher.sendMessage(CC.RED + "Player not banned.");
                                            break;
                                        case "success":
                                            String broadcast = null;

                                            Player player = Bukkit.getPlayer(disguisedName);
                                            Mineman mineman = player != null ?
                                                    plugin.getPlayerManager().getPlayer(player.getUniqueId())
                                                    : null;

                                            switch (type) {
                                                case BLACKLIST:
                                                    broadcast = "blacklisted";
                                                    if (player != null) {
                                                        player.kickPlayer(StringUtil.BLACKLIST);

                                                        if (mineman.getIpAddress() != null) {
                                                            Bukkit.getOnlinePlayers().stream()
                                                                    .filter(o -> o.getAddress().getAddress()
                                                                            .equals(mineman.getIpAddress()))
                                                                    .forEach(o -> o.kickPlayer(StringUtil.BLACKLIST));
                                                        }
                                                    } else if (server != null) {
                                                        plugin.getCoreRedisManager()
                                                                .kickPlayer(target, server, StringUtil.BLACKLIST);
                                                    }
                                                    break;
                                                case UNBLACKLIST:
                                                    broadcast = "unblacklisted";
                                                    if (mineman != null) {
                                                        mineman.setBlacklisted(false);
                                                        mineman.setBanTime(new Timestamp(0L));
                                                        mineman.setBanData(new BanWrapper("", false));
                                                    }
                                                    break;
                                                case IPBAN:
                                                    broadcast = "banned";
                                                    if (player != null) {
                                                        player.kickPlayer(StringUtil.IP_BAN);

                                                        if (mineman.getIpAddress() != null) {
                                                            Bukkit.getOnlinePlayers().stream()
                                                                    .filter(o -> o.getAddress().getAddress()
                                                                            .equals(mineman.getIpAddress()))
                                                                    .forEach(o -> o.kickPlayer(StringUtil.IP_BAN_OTHER.replace("%s", player.getName())));
                                                        }
                                                    } else if (server != null) {
                                                        plugin.getCoreRedisManager()
                                                                .kickPlayer(target, server, StringUtil.IP_BAN);
                                                    }
                                                    break;
                                                case BAN:
                                                    //broadcast = (temporary ? "temporarily banned for " + finalExpiryTime :
                                                    //		"permanently banned");

                                                    broadcast = (temporary ? "temporarily banned" :
                                                            "banned");
                                                    if (player != null) {
                                                        player.kickPlayer(temporary ? String.format(StringUtil.TEMPORARY_BAN,
                                                                finalExpiryTime) : StringUtil.PERMANENT_BAN);

                                                        if (mineman.getIpAddress() != null) {
                                                            Bukkit.getOnlinePlayers().stream()
                                                                    .filter(o -> o.getAddress().getAddress()
                                                                            .equals(mineman.getIpAddress()))
                                                                    .forEach(o -> o.kickPlayer(temporary ? String.format(StringUtil.TEMPORARY_BAN,
                                                                            finalExpiryTime) : StringUtil.PERMANENT_BAN));
                                                        }
                                                    } else if (server != null) {
                                                        plugin.getCoreRedisManager()
                                                                .kickPlayer(target, server,
                                                                        temporary ? String.format(StringUtil.TEMPORARY_BAN,
                                                                                finalExpiryTime) : StringUtil.PERMANENT_BAN);
                                                    }
                                                    break;
                                                case KICK:
                                                    broadcast = "kicked";
                                                    if (player != null) {
                                                        player.kickPlayer(CC.RED + "You were kicked: " + reason);
                                                    } else if (server != null) {
                                                        plugin.getCoreRedisManager()
                                                                .kickPlayer(target, server,
                                                                        CC.RED + "You were kicked " + reason);
                                                    }
                                                    break;
                                                case UNBAN:
                                                    broadcast = "unbanned";
                                                    if (mineman != null) {
                                                        mineman.setBanned(false);
                                                        mineman.setBanTime(new Timestamp(0L));
                                                        mineman.setBanData(new BanWrapper("", false));
                                                    }
                                                    break;
                                                case UNMUTE:
                                                    broadcast = "unmuted";
                                                    if (mineman != null) {
                                                        mineman.setMuted(false);
                                                        mineman.setMuteTime(new Timestamp(0L));

                                                        if (player != null) {
                                                            player.sendMessage(CC.RED + "You have been unmuted.");
                                                        }
                                                    }
                                                    break;
                                                case MUTE:
                                                    broadcast = (temporary ? "temporarily muted" :
                                                            "muted");

                                                    if (mineman != null) {
                                                        mineman.setMuted(true);
                                                        mineman.setMuteTime(finalExpiry);

                                                        if (player != null) {
                                                            player.sendMessage(CC.RED + "You have been muted for " + reason);
                                                        }
                                                    } else if (server != null) {
                                                        plugin.getCoreRedisManager()
                                                                .mutePlayer(target, server, broadcast, finalExpiry);
                                                    }
                                                    break;
                                            }

                                            if (!type.getName().startsWith("un")) {
                                                broadcastPunishment(broadcast, reason, disguisedName, name, silent, finalType.startsWith("temp-") ? finalExpiryTime : "Permanent");
                                            } else {
                                                broadcastPunishment(broadcast, reason, disguisedName, name, false, null);
                                            }
                                            break;
                                        default:
                                            punisher.sendMessage(CC.RED + "An error has occurred. Please notify an administrator.");
                                            plugin.getLogger().warning("Punishment returned: " + element);
                                            break;
                                    }
                                }

                                @Override
                                public void onError(String message) {
                                    super.onError(message);
                                    punisher.sendMessage(MessageFormat.format("{0}Something went wrong while punishing ''{1}''.", CC.RED, target));
                                }
                            });
                }
        );
    }

}
