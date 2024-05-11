package club.minion.practice.killeffects;

import org.bukkit.entity.Player;

public interface EffectCallable {

  void call(Player player, Player... watchers);
}
