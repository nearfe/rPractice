package club.minion.practice.util;

import lombok.Getter;
import org.bukkit.ChatColor;

public class RatingUtil {

    @Getter
    public enum Rank {

        NONE("Not Placed", -1, ChatColor.DARK_GRAY, -2, 0),

        BRONZE_V("Bronze V", 0, ChatColor.YELLOW, 900, 975),
        BRONZE_IV("Bronze IV", 1, ChatColor.YELLOW, 975, 1050),
        BRONZE_III("Bronze III", 2, ChatColor.YELLOW, 1050, 1125),
        BRONZE_II("Bronze II", 3, ChatColor.YELLOW, 1125, 1200),
        BRONZE_I("Bronze I", 4, ChatColor.YELLOW, 1200, 1275),

        SILVER_V("Silver V", 5, ChatColor.GRAY, 1275, 1350),
        SILVER_IV("Silver IV", 6, ChatColor.GRAY, 1350, 1425),
        SILVER_III("Silver III", 7, ChatColor.GRAY, 1425, 1500),
        SILVER_II("Silver II", 8, ChatColor.GRAY, 1500, 1575),
        SILVER_I("Silver I", 9, ChatColor.GRAY, 1575, 1650),

        GOLD_V("Gold V", 10, ChatColor.GOLD, 1650, 1725),
        GOLD_IV("Gold IV", 11, ChatColor.GOLD, 1725, 1800),
        GOLD_III("Gold III", 12, ChatColor.GOLD, 1800, 1875),
        GOLD_II("Gold II", 13, ChatColor.GOLD, 1875, 1950),
        GOLD_I("Gold I", 14, ChatColor.GOLD, 1950, 2025),

        EMERALD_V("Emerald V", 20, ChatColor.GREEN, 2025, 2100),
        EMERALD_IV("Emerald IV", 21, ChatColor.GREEN, 2100, 2175),
        EMERALD_III("Emerald III", 22, ChatColor.GREEN, 2175, 2250),
        EMERALD_II("Emerald II", 23, ChatColor.GREEN, 2250, 2325),
        EMERALD_I("Emerald I", 24, ChatColor.GREEN, 2325, 2400),

        DIAMOND_V("Diamond V", 25, ChatColor.AQUA, 2400, 2475),
        DIAMOND_IV("Diamond IV", 26, ChatColor.AQUA, 2475, 2550),
        DIAMOND_III("Diamond III", 27, ChatColor.AQUA, 2550, 2625),
        DIAMOND_II("Diamond II", 28, ChatColor.AQUA, 2625, 2700),
        DIAMOND_I("Diamond I", 29, ChatColor.AQUA, 2700, 2775),

        MASTERS("Masters", 30, ChatColor.RED, 2775, 20000);

        private final String name;
        private final int rank;
        private final ChatColor chatColor;
        private final int minElo;
        private final int maxElo;

        Rank(String name, int rank, ChatColor chatColor, int minElo, int maxElo) {
            this.name = name;
            this.rank = rank;
            this.chatColor = chatColor;
            this.minElo = minElo;
            this.maxElo = maxElo;
        }

        public static Rank getRankByElo(double elo) {
            for (Rank rank : Rank.values()) {
                if (elo >= rank.minElo && elo < rank.maxElo) {
                    return rank;
                }
            }
            return Rank.NONE;
        }

        public static boolean isPromotionGame(int oldElo, int newElo) {
            return getRankByElo(newElo) != getRankByElo(oldElo);
        }

        public String getName() {
            return name;
        }

        public int getRank() {
            return rank;
        }

        public ChatColor getChatColor() {
            return this.chatColor;
        }
    }


}
