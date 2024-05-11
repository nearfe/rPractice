package club.minion.practice.leaderboard;

import club.minion.practice.Practice;
import club.minion.practice.kit.Kit;
import club.minion.practice.menu.Button;
import club.minion.practice.menu.Menu;
import club.minion.practice.player.PracticePlayerData;
import club.minion.practice.util.ItemBuilder;
import club.minion.practice.util.RatingUtil;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoCursor;
import dev.lugami.spigot.utils.CC;
import lombok.AllArgsConstructor;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LeaderboardsMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "Statistics";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(13, new PlayerStatsButton());

        AtomicInteger value = new AtomicInteger(28);
        for (Kit kit : Practice.getInstance().getKitManager().getKits()) {
            if (kit.isRanked()) {
                buttons.put(value.getAndIncrement(), new LadderButton(kit));
                if (value.get() == 35) {
                    value.set(38);
                }
                if (value.get() == 39) {
                    value.set(42);
                }
            }
        }
        return buttons;
    }

    @AllArgsConstructor
    public static class PlayerStatsButton extends Button {

        private final Practice plugin = Practice.getInstance();

        public ItemStack getButtonItem(Player player) {
            List<String> description = Lists.newArrayList();
            PracticePlayerData practicePlayerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

            description.add(CC.MENU_BAR);
            for (Kit kit : Practice.getInstance().getKitManager().getKits()) {
                if (kit.isRanked()) {
                    description.add(ChatColor.RED + kit.getName() + ChatColor.GRAY + ": " + ChatColor.WHITE + practicePlayerData.getElo(kit.getName()));
                }
            }
            description.add(CC.MENU_BAR);
            description.add(ChatColor.RED + "Global ELO" + ChatColor.GRAY + ": " + ChatColor.WHITE + practicePlayerData.getGlobalStats("ELO"));
            description.add(ChatColor.RED + "Global Rating" + ChatColor.GRAY + ": " + ChatColor.WHITE + RatingUtil.Rank.getRankByElo(practicePlayerData.getGlobalStats("ELO")).getName());
            description.add(ChatColor.RED + "Matches Played" + ChatColor.GRAY + ": " + ChatColor.WHITE + practicePlayerData.getMatchesPlayed());
            description.add(CC.MENU_BAR);

            return new ItemBuilder(Material.SKULL_ITEM).name(ChatColor.RED + player.getName() + " | Statistics").lore(description).durability(3).build();
        }
    }

    @AllArgsConstructor
    public static class LadderButton extends Button {

        Kit kit;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            int lineNum = 0;

            lore.add(CC.MENU_BAR);

            try (MongoCursor<Document> iterator = Practice.getInstance().getPlayerManager().getPlayersSortByLadderElo(kit)) {
                while (iterator.hasNext()) {
                    lineNum++;
                    try {
                        Document document = iterator.next();
                        UUID uuid = UUID.fromString(document.getString("uuid"));

                        if (!document.containsKey("statistics")) {
                            continue;
                        }

                        Document statistics = (Document) document.get("statistics");
                        Document ladder = (Document) statistics.get(this.kit.getName());
                        int amount = ladder.getInteger("ranked-elo");

                        OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);

                        String text = (lineNum <= 3 ? ChatColor.DARK_RED.toString() + lineNum : ChatColor.GRAY.toString() + lineNum) + ChatColor.GRAY + ChatColor.BOLD + " | "
                                + ChatColor.RED + target.getName() + ChatColor.GRAY + ": " + ChatColor.WHITE + amount
                                + ChatColor.GRAY + " (" + RatingUtil.Rank.getRankByElo(amount).getName() + ChatColor.GRAY + ")";

                        lore.add(text);
                    } catch (Exception ignored) {
                    }
                }
            }

            lore.add(CC.MENU_BAR);

            return new ItemBuilder(kit.getIcon().getType()).name(ChatColor.RED + kit.getName() + ChatColor.GRAY + ChatColor.BOLD + " | " + ChatColor.WHITE + "Top 10").lore(lore).durability(kit.getIcon().getDurability()).build();
        }
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 9 * 6;
    }

}
