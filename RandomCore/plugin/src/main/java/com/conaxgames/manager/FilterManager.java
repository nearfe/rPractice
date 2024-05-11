package com.conaxgames.manager;

import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.finalutil.Color;
import com.conaxgames.util.finalutil.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.conaxgames.CorePlugin;

/**
 * Created by Marko on 09.11.2018.
 */
public class FilterManager {

    private final CorePlugin plugin = CorePlugin.getInstance();

    private String[] blockedNormal = {
            " faggot ", " fag ", " f4g ", " f4gg0t ", " f4ggot ", " faggots ", " fags ", " f4gs ", " f4gg0ts ", " f4ggots ",
            " n1gg3r ", " nigger ", " nigg3r ", " nig ", " n1g ", " n1gg3rs ", " niggers ", " nigg3rs ", " nigs ", " n1gs ",
            " kike ", " k1ke ", " kik3 ", " k1k3 ",
            " beaner ", " b34n3r ", " b3aner ", " b34ner ", " b3an3r ",
            " kikes ", " k1kes ", " kik3s ", " k1k3s ",
            " beaners ", " b34n3rs ", " b3aners ", " b34ners ", " b3an3rs ",
            " chink ", " ch1nk ",
            " j4p ", " jap ",
            " nignog ", " niglet  ",
            " hick ", " h1ck ",
            " sp1ck ", " spick ", " spic ", " sp1c ",
            " p4ki ", " paki ", " p4k1 ", " pak1 ",
            " chinks ", " ch1nks ",
            " j4ps ", " japs ",
            " nignogs ", " niglets ",
            " hicks ", " h1cks ",
            " sp1cks ", " spicks ", " spics ", " sp1cs ",
            " p4kis ", " pakis ", " p4k1s ", " pak1s ",
    };

    public boolean isFiltered(Player player, Mineman data, String message) {
        boolean cancel = false;
        String fixedMessage = message.toLowerCase().replaceAll("[^a-z0-9 ] ", " ").replace("@ ", " a").replace("3 ", " e").replace("0 ", " o").replace("4 ", " a").replace("1 ", " i").replace("5 ", " s");

        for(String normal : blockedNormal) {
            if(!cancel && fixedMessage.contains(normal.toLowerCase()) && !fixedMessage.contains("pickaxe")) {
                handleAlert(player, fixedMessage);
                data.setFilter(data.getFilter() + 1);
                check(player, data);
                cancel = true;
            }
        }

        if(!cancel && (fixedMessage.equals("L") || fixedMessage.equals("LL") || fixedMessage.equals("LLL"))) {
            handleAlert(player, fixedMessage);
            data.setFilter(data.getFilter() + 1);
            check(player, data);
            cancel = true;
        }

        if(isFilterActive(player)) {
            applyFilterCooldown(player);
            data.setSpam(data.getSpam() + 1);
            check(player, data);
        } else {
            data.setSpam(0);
            applyFilterCooldown(player);
        }

        return cancel;
    }

    private void check(Player player, Mineman data) {
        // There is no way to spam in chat because there is cooldown that must expire first but yea i made a check!

        if(data.getSpam() == 2 || data.getSpam() == 3) {
            player.sendMessage(Color.translate("&cConsider sending messages slower. You might get muted for spamming."));
        }

        if(data.getSpam() == 4) {
            data.setSpam(0);
            handleCommand("mute " + player.getName() + " 30m Spamming -s");
            player.sendMessage(Color.translate("&cYou have been temporarily muted for &eSpamming&c."));
            player.sendMessage(Color.translate("&cIf you believe this is false, join our TeamSpeak (ts.minion.lol)"));
        }

        if(data.getFilter() == 1) {
            data.setFilter(0);
            handleCommand("mute " + player.getName() + " 3h Toxicity -s");
            player.sendMessage(Color.translate("&cYou have been temporarily muted for &eToxicity&c."));
            player.sendMessage(Color.translate("&cIf you believe this is false, join our TeamSpeak (ts.minion.lol)"));
        }
    }

    private void applyFilterCooldown(Player player) {
        plugin.getPlayerManager().getPlayer(player.getUniqueId()).setSilentSpam(System.currentTimeMillis() + 1750L);
    }

    private boolean isFilterActive(Player player) {
        return System.currentTimeMillis() < plugin.getPlayerManager().getPlayer(player.getUniqueId()).getSilentSpam();
    }

    public void handleCommand(String input) {
        Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), input));
    }

    private void handleAlert(Player player, String input) {
        if (PlayerUtil.testPermission(player, Rank.HOST)) {
            player.sendMessage(Color.translate("&cThat would have been filtered."));
        }
        PlayerUtil.messageRank("&c[Filtered] " + player.getDisplayName() + "&7: &f" + input);
    }
}
