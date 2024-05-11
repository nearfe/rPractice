package club.minion.practice.killmessages;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public interface KillMessageCallable {
    public String getFormatted(String var1, String var2, boolean var3);

    public List<String> getMessages();

    public List<String> getDescription();

    default public List<String> getFormattedLore() {
        ArrayList<String> stringList = new ArrayList<String>(this.getDescription());
        stringList.add(" ");
        this.getMessages().forEach(message -> stringList.add(ChatColor.GRAY + "... was " + ChatColor.YELLOW + message + ChatColor.GRAY + " by ..."));
        stringList.add(" ");
        stringList.add(ChatColor.WHITE.toString() + ChatColor.ITALIC + "One of these messages will");
        stringList.add(ChatColor.WHITE.toString() + ChatColor.ITALIC + "appear when you kill someone.");
        return stringList;
    }
}

