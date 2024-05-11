// Decompiled with: CFR 0.152
// Class Version: 8
package club.minion.practice.util;

import club.minion.practice.Practice;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class Animation {
    public static String title;

    public static void init() {
        List titles = Practice.getInstance().getScoreboardConfig().getConfig().getStringList("SCOREBOARD.TITLE.LINES");
        AtomicInteger p = new AtomicInteger();
        TaskUtil.runTimerAsync(() -> {
            if (p.get() == titles.size()) {
                p.set(0);
            }
            title = (String)titles.get(p.getAndIncrement());
        }, 0L, (long)(Practice.getInstance().getScoreboardConfig().getConfig().getDouble("SCOREBOARD.TITLE.TASK") * 20.0));
    }

    public static String getScoreboardTitle() {
        return title;
    }
}
