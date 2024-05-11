package club.minion.practice.runnable;

import club.minion.practice.Practice;
import org.bukkit.entity.Item;

import java.util.Iterator;

public class ItemDespawnRunnable implements Runnable {

	private final Practice plugin = Practice.getInstance();

    public ItemDespawnRunnable(Practice practice) {
    }

    @Override
	public void run() {
		Iterator<Item> it = this.plugin.getFfaManager().getItemTracker().keySet().iterator();
		while (it.hasNext()) {
			Item item = it.next();
			long l = this.plugin.getFfaManager().getItemTracker().get(item);
			if (l + 15000 < System.currentTimeMillis()) {
				item.remove();
				it.remove();
			}
		}
	}
}
