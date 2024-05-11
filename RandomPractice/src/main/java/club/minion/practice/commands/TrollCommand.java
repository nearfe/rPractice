package club.minion.practice.commands;

import com.conaxgames.util.finalutil.CC;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class TrollCommand extends Command {

    public TrollCommand() {
        super("troll");
        this.setDescription("Troll a player");
        this.setUsage(CC.RED + "Usage: /troll (player)");
    }

    @Override
    public boolean execute(CommandSender player, String s, String[] args) {
        if (args.length == 0) {
            player.sendMessage(CC.RED + "Usage: /troll (player)");
            return true;
        }

        OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);

        if (p.isOnline()) {
            Player target = p.getPlayer();

            String path = Bukkit.getServer().getClass().getPackage().getName();
            String version = path.substring(path.lastIndexOf(".") + 1);

            try {
                Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
                Class<?> PacketPlayOutGameStateChange = Class.forName("net.minecraft.server." + version + ".PacketPlayOutGameStateChange");
                Class<?> Packet = Class.forName("net.minecraft.server." + version + ".Packet");

                Constructor<?> playOutConstructor = PacketPlayOutGameStateChange.getConstructor(int.class, float.class);
                Object packet = playOutConstructor.newInstance(5, 0);

                Object craftPlayerObject = craftPlayer.cast(target);
                Method getHandleMethod = craftPlayer.getMethod("getHandle");
                Object handle = getHandleMethod.invoke(craftPlayerObject);
                Object pc = handle.getClass().getField("playerConnection").get(handle);
                Method sendPacketMethod = pc.getClass().getMethod("sendPacket", Packet);
                sendPacketMethod.invoke(pc, packet);
    
                player.sendMessage(CC.PRIMARY + target.getName() + CC.SECONDARY + " got trolled!");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            player.sendMessage(CC.RED + "Player not found or offline.");
        }
        return true;
    }
}
