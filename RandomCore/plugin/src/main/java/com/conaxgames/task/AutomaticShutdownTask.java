package com.conaxgames.task;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import com.conaxgames.CorePlugin;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RequiredArgsConstructor
public class AutomaticShutdownTask extends BukkitRunnable {

    private DateFormat format = new SimpleDateFormat("HH:mm:ss");

    public void run() {
        Date now = new Date();

        try {
            if(format.format(now).equals(format.format(format.parse("04:00:00").getTime() - 600000L))) {
                Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "shutdown time 600"));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /*Date date = new Date();
        String time = format.format(date);
        if(plugin.getConfig().getStringList("reboot-times").contains(time)) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "shutdown time 300");
        }*/
    }
}
