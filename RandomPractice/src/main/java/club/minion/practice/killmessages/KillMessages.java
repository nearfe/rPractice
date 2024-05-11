package club.minion.practice.killmessages;

import club.minion.practice.util.ItemUtil;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.finalutil.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public enum KillMessages {
    NONE(CC.RED + "None", ItemUtil.createItem(Material.PAPER, "None"),  Rank.NORMAL, new KillMessageCallable(){

        @Override
        public String getFormatted(String killed, String killer, boolean otherPlayerExists) {
            String randomMessage = CC.SECONDARY + this.getMessages().get(ThreadLocalRandom.current().nextInt(this.getMessages().size())) + ChatColor.GRAY;
            if (!otherPlayerExists) {
                return CC.SECONDARY + killed + ChatColor.GRAY + " was " + randomMessage + ".";
            }
            return CC.SECONDARY + killed + ChatColor.GRAY + " was " + randomMessage + " by " + ChatColor.GREEN + killer + ChatColor.GRAY + ".";
        }

        @Override
        public List<String> getMessages() {
            return Collections.singletonList("killed");
        }

        @Override
        public List<String> getDescription() {
            return new ArrayList<String>();
        }
    }),
    NERDY(CC.YELLOW + "Nerd", ItemUtil.createItem(Material.BOOK, "Nerd"),  Rank.NORMAL, new KillMessageCallable(){

        @Override
        public String getFormatted(String killed, String killer, boolean otherPlayerExists) {
            String randomMessage = CC.SECONDARY + this.getMessages().get(ThreadLocalRandom.current().nextInt(this.getMessages().size())) + ChatColor.GRAY;
            if (!otherPlayerExists) {
                return CC.SECONDARY + killed + ChatColor.GRAY + " was " + randomMessage + ".";
            }
            return CC.SECONDARY + killed + ChatColor.GRAY + " was " + randomMessage + " by " + ChatColor.GREEN + killer + ChatColor.GRAY + ".";
        }
        @Override
        public List<String> getMessages() {
            return Arrays.asList("ALT+F4'd", "deleted", "crashed", "ratted", "hacked", "over-heated", "trolled");
        }

        @Override
        public List<String> getDescription() {
            return Arrays.asList(ChatColor.GRAY + "Nerdy computer terms", ChatColor.GRAY + "to trigger your opponents.");
        }
    }),
    Tryhard( CC.YELLOW + "Tryhard", ItemUtil.createItem(Material.DIAMOND_SWORD, "Tryhard"), Rank.NORMAL, new KillMessageCallable(){

        @Override
        public String getFormatted(String killed, String killer, boolean otherPlayerExists) {
            String randomMessage = CC.SECONDARY + this.getMessages().get(ThreadLocalRandom.current().nextInt(this.getMessages().size())) + ChatColor.GRAY;
            if (!otherPlayerExists) {
                return CC.SECONDARY + killed + ChatColor.GRAY + " was " + randomMessage + ".";
            }
            return CC.SECONDARY + killed + ChatColor.GRAY + " was " + randomMessage + " by " + ChatColor.GREEN + killer + ChatColor.GRAY + ".";
        }

        @Override
        public List<String> getMessages() {
            return Arrays.asList("destroyed", "rolled", "shoved into a locker", "clapped");
        }

        @Override
        public List<String> getDescription() {
            return Arrays.asList(ChatColor.GRAY + "Trigger your opponents", ChatColor.GRAY + "using slightly toxic phrases.");
        }
    });

    private final String name;
    private final org.bukkit.inventory.ItemStack icon;
    private final String permission;
    private final KillMessageCallable callable;

    private KillMessages(String name, org.bukkit.inventory.ItemStack icon, Rank permission, KillMessageCallable callable) {
        this.name = name;
        this.icon = icon;
        this.permission = String.valueOf(permission);
        this.callable = callable;
    }

    public static KillMessages getByName(String input) {
        for (KillMessages type : KillMessages.values()) {
            if (!type.name().equalsIgnoreCase(input) && !type.getName().equalsIgnoreCase(input)) continue;
            return type;
        }
        return null;
    }

    public boolean hasPermission(Player player) {
        return player.hasPermission(this.permission) || this.permission.isEmpty();
    }

    public String getName() {
        return this.name;
    }

    public org.bukkit.inventory.ItemStack getIcon() {
        return this.icon;
    }

    public String getPermission() {
        return this.permission;
    }

    public KillMessageCallable getCallable() {
        return this.callable;
    }
}

