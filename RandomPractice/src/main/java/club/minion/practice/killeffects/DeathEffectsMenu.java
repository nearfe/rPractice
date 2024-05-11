package club.minion.practice.killeffects;

import club.minion.practice.menu.Button;
import club.minion.practice.menu.Menu;
import club.minion.practice.player.PlayerData;
import com.conaxgames.util.finalutil.CC;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class DeathEffectsMenu extends Menu {

  public DeathEffectsMenu() {
    setUpdateAfterClick(true);
  }

  @Override
  public String getTitle(Player player) {
    return CC.PRIMARY + "Death Effects";
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = new HashMap<>();

    PlayerData practicePlayerData = plugin.getPlayerManager()
            .getPlayerData(player.getUniqueId());
    SpecialEffects specialEffects = practicePlayerData.getKillEffect();

    buttons.put(1, new SpecialEffectButton(specialEffects, practicePlayerData));

    return buttons;
  }

  @Override
  public int getSize() {
    return 9;
  }
}
