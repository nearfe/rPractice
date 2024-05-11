package com.conaxgames.command;

import com.conaxgames.rank.Rank;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.Param;
import com.conaxgames.util.cmd.annotation.commandTypes.Command;
import com.conaxgames.util.finalutil.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import com.conaxgames.CorePlugin;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.util.List;

/**
 * @since 1/1/2018
 */
@RequiredArgsConstructor
public class MaxPlayersCommand implements CommandHandler {

	private static final int defaultTo = -157345;
	private static Method getHandleMethod;
	private static Field maxPlayersField;

	private final CorePlugin plugin;

	@Command(name = {"setmaxplayers", "maxplayers", "mp"}, description = "Sets the maxplayers allowed inside the server", rank = Rank.MANAGER)
	public void setMaxPlayers(CommandSender sender, @Param(name = "max players", defaultTo = MaxPlayersCommand.defaultTo + "") int maxPlayers) {
		if (maxPlayers == MaxPlayersCommand.defaultTo) {
			sender.sendMessage(MessageFormat.format("{0}Currently allowing: {2}{3}", CC.SECONDARY, CC.PRIMARY, this.plugin.getServer().getMaxPlayers()));
			return;
		}

		if (maxPlayers <= 0) {
			sender.sendMessage(MessageFormat.format("{0}You cannot input a negative number.", CC.RED));
			return;
		}

		setSlots(maxPlayers);
		sender.sendMessage(MessageFormat.format("{0}You have set the max players to {1}{2}{0}.", CC.SECONDARY, CC.PRIMARY, maxPlayers));

		/*new BukkitRunnable() {
			public void run() {
					int oldPlayers = plugin.getServer().getMaxPlayers();
					if (getHandleMethod == null) {
						String bukkitVersion = Bukkit.getServer().getClass().getPackage()
								.getName().substring(23);
						getHandleMethod = Class.forName("org.bukkit.craftbukkit." + bukkitVersion + ".CraftServer")
								.getDeclaredMethod("getHandle", (Class<?>) null);
					}

					Object playerList = getHandleMethod.invoke(Bukkit.getServer(), (Object) null);
					if (maxPlayersField == null) {
						maxPlayersField = playerList.getClass().getSuperclass()
								.getDeclaredField("maxPlayers");
						maxPlayersField.setAccessible(true);
					}

					maxPlayersField.set(playerList, maxPlayers);

			}
		}.runTaskAsynchronously(plugin);*/
	}

	public static void setSlots(int slots) {
		slots = Math.abs(slots);

		try {
			Object invoke = Class.forName(String.valueOf(Bukkit.getServer().getClass().getPackage().getName()) + ".CraftServer").getDeclaredMethod("getHandle", (Class<?>[])new Class[0]).invoke(Bukkit.getServer());
			Field declaredField = invoke.getClass().getSuperclass().getDeclaredField("maxPlayers");

			declaredField.setAccessible(true);
			declaredField.set(invoke, slots);

			changeProperties(slots);
		} catch(ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}

	private static void changeProperties(int slots) {
		Path resolve = Paths.get(CorePlugin.getInstance().getDataFolder().getParentFile().getAbsolutePath(), new String[0]).getParent().resolve("server.properties");

		try {
			List<String> allLines = Files.readAllLines(resolve);

			for(int i = 0; i < allLines.size(); ++i) {
				if(allLines.get(i).startsWith("max-players")) {
					allLines.remove(i);
				}
			}

			allLines.add("max-players=" + slots);

			Files.write(resolve, allLines, StandardOpenOption.TRUNCATE_EXISTING);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
