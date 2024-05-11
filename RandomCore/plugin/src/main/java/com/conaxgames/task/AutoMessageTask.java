package com.conaxgames.task;

import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.Color;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import com.conaxgames.CorePlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Marko on 17.11.2018.
 */
public class AutoMessageTask extends BukkitRunnable {

    private int lastCount;
    private List<String> defaultMessages = new ArrayList<>();
    private List<String> practiceMessages = new ArrayList<>();
    private List<String> uhcMessages = new ArrayList<>();
    private List<String> hcfMessages = new ArrayList<>();

    public AutoMessageTask() {
        setupMessages();

        runTaskTimerAsynchronously(CorePlugin.getInstance(), 20L, 120 * 20L);
    }

    @Override
    public void run() {
        String server = CorePlugin.getInstance().getServerManager().getServerName();

        if(server.contains("hcf") || server.contains("powers")) {
            sendMessage(hcfMessages);
        } else if(server.contains("practice-")) {
            sendMessage(practiceMessages);
        } else if(server.contains("uhc-") || server.contains("uhcm-") || server.equalsIgnoreCase("uhcgames")) {
            sendMessage(uhcMessages);
        } else {
            sendMessage(defaultMessages);
        }
    }

    private void setupMessages() {
        String prefix = CC.DARK_GRAY + "[" + CC.DARK_RED + "Alert" + CC.DARK_GRAY + "] " + CC.YELLOW;

        defaultMessages.add(prefix + "Join our discord for server announcements and giveaways: discord.minion.lol");
        defaultMessages.add(prefix + "Purchase ranks, perks, and more on our shop - shop.minion.lol");
        defaultMessages.add(prefix + "Configure our systems to your liking by using /settings.");

        practiceMessages.addAll(defaultMessages);
        practiceMessages.add(prefix + "Vips can host events using /event.");
        practiceMessages.add(prefix + "Butterfly clicking may result in a punishment! Use at your own risk.");
        practiceMessages.add(prefix + "View the leaderboards on our website - www.minion.lol/leaderboards");

        uhcMessages.addAll(defaultMessages);
        uhcMessages.add(prefix + "Follow our UHC Twitter to be notified when there is a UHC game - www.twitter.com/BloomNetwork");

        hcfMessages.addAll(defaultMessages);
        hcfMessages.add(prefix + "Use /coords to list all event locations.");
        hcfMessages.add(prefix + "Remember that Enderpearls can pass through fence gates!");
        hcfMessages.add(prefix + "Kicking and killing members is not allowed.");
        hcfMessages.add(prefix + "Truce with other factions is not allowed. Only two solo factions can truce.");
        hcfMessages.add(prefix + "DTR evading is not allowed.");
        hcfMessages.add(prefix + "Purchase lives and crates on our shop - shop.minion.lol");
        hcfMessages.add(prefix + "For all faction related commands, use /faction.");
        hcfMessages.add(prefix + "For all lives related commands, use /lives.");
        hcfMessages.add(prefix + "Suffocation traps are not allowed.");
    }

    private void sendMessage(List<String> input) {
        Bukkit.broadcastMessage("");

        int count = ThreadLocalRandom.current().nextInt(defaultMessages.size());
        Bukkit.broadcastMessage(Color.translate(input.get(lastCount == count ? ThreadLocalRandom.current().nextInt(input.size()) : count)));
        lastCount = count;

        Bukkit.broadcastMessage("");
    }
}