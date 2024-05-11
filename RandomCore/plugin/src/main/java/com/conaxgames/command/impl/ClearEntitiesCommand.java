package com.conaxgames.command.impl;

import com.conaxgames.rank.Rank;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.commandTypes.Command;
import com.conaxgames.util.finalutil.CC;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ClearEntitiesCommand implements CommandHandler {

	@Command(name = { "clearentities", "clearent", "cent" }, rank = Rank.MANAGER,
	         description = "Clear all entities in the world")
	public void clearEntitiesCommand(Player commandSender) {
		int entitiesCleared = 0;
		int entitiesIgnored = 0;
		int entitiesTotal = commandSender.getWorld().getEntities().size();

		for(Entity entity : commandSender.getWorld().getEntities()) {
			if(entity instanceof Player || entity.getPassenger() instanceof Player) {
				entitiesIgnored++;
				continue;
			}

			entity.remove();
			entitiesCleared++;
		}

		commandSender.sendMessage(CC.SECONDARY + "Cleared " + CC.PRIMARY + entitiesCleared + CC.SECONDARY + " of " + CC.PRIMARY + entitiesTotal + CC.SECONDARY + " in the world. There was " + CC.PRIMARY + entitiesIgnored + CC.SECONDARY + " ignored entit" + (entitiesIgnored == 1 ? "y" : "ies") + ".");
	}
}
