package club.minion.practice.commands;

import club.minion.practice.Practice;
import club.minion.practice.killeffects.DeathEffectsMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class KillEffectCommand extends Command {
    private final Practice plugin = Practice.getInstance();

    public KillEffectCommand() {
        super("killeffect");
        this.setAliases(Arrays.asList("deatheffect"));
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        }

        Player player = (Player) commandSender;
        new DeathEffectsMenu().openMenu(player);

        return true;
    }
}
