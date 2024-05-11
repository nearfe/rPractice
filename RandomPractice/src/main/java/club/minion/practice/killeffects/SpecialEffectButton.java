package club.minion.practice.killeffects;

import club.minion.practice.Practice;
import club.minion.practice.menu.Button;
import club.minion.practice.player.PlayerData;
import club.minion.practice.util.ItemBuilder;
import dev.lugami.spigot.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SpecialEffectButton extends Button {

  private final Practice plugin = Practice.getInstance();

  private final SpecialEffects specialEffect;
  private final PlayerData practicePlayerData;

  private final List<SpecialEffects> effects;

  public SpecialEffectButton(SpecialEffects specialEffect, PlayerData practicePlayerData) {
    this.specialEffect = specialEffect;
    this.practicePlayerData = practicePlayerData;

    this.effects = Arrays.stream(SpecialEffects.values()).filter(
        specialEffects -> specialEffects.hasPermission(
            Bukkit.getPlayer(practicePlayerData.getUniqueId()))).collect(Collectors.toList());
  }

  public void onPlayerDeath (Player player) {


  }

  @Override
  public ItemStack getButtonItem(Player player) {
    List<String> lore = new ArrayList<>();

    lore.add(dev.lugami.spigot.utils.CC.CHAT_BAR);
    for (SpecialEffects effect : effects) {
      lore.add(practicePlayerData.getKillEffect().getName()
          .equals(effect.getName())
          ? "&a ▶ &f" + effect.getName()
          : "&c ▷ &f" + effect.getName()
      );
    }
    lore.add(" ");
    lore.add(com.conaxgames.util.finalutil.CC.PRIMARY + "[Click to equip]");
    lore.add(dev.lugami.spigot.utils.CC.CHAT_BAR);

    return new ItemBuilder(specialEffect.getIcon())
        .name(specialEffect.getName())
        .lore(dev.lugami.spigot.utils.CC.translate(lore))
        .build();
  }

  @Override
  public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
    int currentValue = effects.indexOf(specialEffect);
    if (clickType == ClickType.LEFT) {
      if (currentValue >= effects.size() - 1) {
        currentValue = 0;
      } else {
        currentValue++;
      }
    } else if (clickType == ClickType.RIGHT) {
      if (currentValue <= 0) {
        currentValue = effects.size() - 1;
      } else {
        currentValue--;
      }
    }

    playSuccess(player);
    practicePlayerData.setKillEffect(effects.get(currentValue));
    player.sendMessage(CC.translate(
        "&aYou've selected the " + effects.get(currentValue).getName() + " death effect!"));
  }
}
