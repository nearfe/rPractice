package club.minion.practice.commands;

import club.minion.practice.Practice;
import club.minion.practice.util.SymbolUtil;
import com.conaxgames.util.finalutil.Color;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.DecimalFormat;
import java.util.Arrays;

public class LagCommand extends Command {
    private final Practice plugin;

    public LagCommand() {
        super("lag");
        this.plugin = Practice.getInstance();
        this.setDescription("practice performance");
        this.setUsage(ChatColor.RED + "Usage: /lag");
        this.setAliases(Arrays.asList("performance"));
    }

    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        double tps = Bukkit.spigot().getTPS()[0];
        double lag = Math.round((1.0 - tps / 20.0) * 100.0);
        RuntimeMXBean serverStart = ManagementFactory.getRuntimeMXBean();
        String serverUptime = DurationFormatUtils.formatDurationWords(serverStart.getUptime(), true, true);
        DecimalFormat df = new DecimalFormat("#.#");
        ChatColor colour = (tps >= 18.0) ? ChatColor.GREEN : ((tps >= 15.0) ? ChatColor.YELLOW : ChatColor.RED);
        Double tpsF = Math.round(tps * 10000.0) / 10000.0;
        for (String s : Practice.getInstance().getExtra().getStringList("LAG_COMMAND")) {
            String formatted = s.replaceAll("%d_arrows%", SymbolUtil.UNICODE_ARROWS_RIGHT);

            formatted = formatted.replace("%d_arrows%", SymbolUtil.UNICODE_ARROWS_RIGHT);
            formatted = formatted.replace("%server_tps%", Color.translate(colour + df.format(tpsF)));
            formatted = formatted.replace("%players%", Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());
            formatted = formatted.replace("%server_uptime%", serverUptime);
            formatted = formatted.replace("%server_lag%", String.valueOf(Math.round(lag * 10000.0) / 10000.0 + '%'));

            sender.sendMessage(Color.translate(formatted));
        }
        return true;
    }
}
