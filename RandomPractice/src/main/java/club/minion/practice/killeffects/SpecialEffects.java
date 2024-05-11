package club.minion.practice.killeffects;

import club.minion.practice.Practice;
import com.conaxgames.util.finalutil.CC;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public enum SpecialEffects {

  NONE(CC.PRIMARY + "None", Material.RECORD_11, "", (player, watchers) -> {}),
  BLOOD(CC.PRIMARY + "Blood", Material.REDSTONE, "minion.effects.blood", (player, watchers) -> {
    PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.BLOCK_CRACK, false, (float)player.getLocation().getX(), (float)player.getLocation().getY(), (float)player.getLocation().getZ(), 0.2f, 0.2f, 0.2f, 1.0f, 20, new int[]{Material.REDSTONE_BLOCK.getId()});
    for (Player watcher : watchers) {
      for (int i = 0; i < 5; ++i) {
        ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket((Packet)packet);
      }
      watcher.playSound(player.getLocation(), Sound.FALL_BIG, 1.0f, 0.5f);
    }
  }),
  EXPLOSION(CC.PRIMARY + "Explosion", Material.TNT, "minion.effects.explosion", (player, watchers) -> {
    PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.EXPLOSION_LARGE, false, (float)player.getLocation().getX(), (float)player.getLocation().getY(), (float)player.getLocation().getZ(), 0.2f, 0.2f, 0.2f, 1.0f, 20, new int[0]);
    for (Player watcher : watchers) {
      ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket((Packet)packet);
      watcher.playSound(player.getLocation(), Sound.EXPLODE, 1.0f, 0.7f);
    }
  }),
  LIGHTNING(CC.PRIMARY + "Lightning", Material.BEACON, "minion.effects.lightning", (player, watchers) -> {
    EntityLightning entityLightning = new EntityLightning(((CraftPlayer)player).getHandle().getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
    PacketPlayOutSpawnEntityWeather lightning = new PacketPlayOutSpawnEntityWeather((Entity)entityLightning);
    PacketPlayOutNamedSoundEffect lightningSound = new PacketPlayOutNamedSoundEffect("ambient.weather.thunder", player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), 10000.0f, 63.0f);
    PacketPlayOutWorldParticles cloud = new PacketPlayOutWorldParticles(EnumParticle.CLOUD, false, (float)player.getLocation().getX(), (float)player.getLocation().getY(), (float)player.getLocation().getZ(), 0.5f, 0.5f, 0.5f, 0.1f, 10, new int[0]);
    PacketPlayOutWorldParticles flame = new PacketPlayOutWorldParticles(EnumParticle.FLAME, false, (float)player.getLocation().getX(), (float)player.getLocation().getY(), (float)player.getLocation().getZ(), 0.3f, 0.3f, 0.3f, 0.1f, 12, new int[0]);
    for (Player watcher : watchers) {
      ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket((Packet)lightning);
      ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket((Packet)lightningSound);
      ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket((Packet)cloud);
      ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket((Packet)flame);
    }
  }),
  FLAME(CC.PRIMARY + "Flame", Material.BLAZE_POWDER, "minion.effects.flame", (player, watchers) -> {
    PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.FLAME, false, (float)player.getLocation().getX(), (float)player.getLocation().getY(), (float)player.getLocation().getZ(), 0.5f, 0.5f, 0.5f, 0.1f, 20, new int[0]);
    for (Player watcher : watchers) {
      ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket((Packet)packet);
    }
  }),
  PINATA(CC.PRIMARY + "Pinata", Material.STICK, "minion.effects.pinata", (player, watchers) -> {
    byte[] colors;
    for (int n : colors = new byte[]{1, 2, 4, 5, 6, 9, 10, 11, 12, 13, 14, 15}) {
      PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.ITEM_CRACK, false, (float)player.getLocation().getX(), (float)player.getLocation().getY(), (float)player.getLocation().getZ(), 0.0f, 0.0f, 0.0f, 0.5f, 10, new int[]{Material.INK_SACK.getId(), n});
      for (Player watcher : watchers) {
        ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket((Packet)packet);
        watcher.playSound(player.getLocation(), Sound.FIREWORK_LARGE_BLAST, 1.0f, 0.7f);
      }
    }
  }),
  SHATTERED(CC.PRIMARY + "Shattered", Material.ANVIL, "minion.effects.shattered", (player, watchers) -> {
    byte[] grayscale;
    for (int n : grayscale = new byte[]{0, 7, 8, 15}) {
      PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.ITEM_CRACK, false, (float)player.getLocation().getX(), (float)player.getLocation().add(0.0, 1.0, 0.0).getY(), (float)player.getLocation().getZ(), 0.0f, 0.0f, 0.0f, 0.5f, 20, new int[]{Material.STAINED_GLASS.getId(), n});
      for (Player watcher : watchers) {
        ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket((Packet)packet);
        watcher.playSound(player.getLocation(), Sound.GLASS, 1.0f, 0.55f);
      }
    }
  }),
  SHOCKWAVE(CC.PRIMARY + "Shockwave", Material.FIREWORK_CHARGE, "minion.effects.shockwave", (player, watchers) -> {
    final Location loc = player.getLocation().clone();
    Material block = loc.getBlock().getRelative(BlockFace.DOWN).getType();
    if (block == Material.AIR) {
      block = Material.ICE;
    }
    final Material finalBlock = block;
    new BukkitRunnable(){
      int i = 0;
      double radius = 0.5;

      public void run() {
        this.radius += 0.5;
        for (double t = 0.0; t < 50.0; t += 1.5) {
          double x = this.radius * Math.sin(t);
          double z = this.radius * Math.cos(t);
          PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.BLOCK_CRACK, false, (float)(loc.getX() + x), (float)loc.getY(), (float)(loc.getZ() + z), 0.0f, 0.0f, 0.0f, 1.0f, 6, new int[]{finalBlock.getId()});
          for (Player watcher : watchers) {
            ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket((Packet)packet);
            watcher.playSound(loc, Sound.DIG_GRAVEL, 0.3f, 0.45f);
          }
        }
        ++this.i;
        if (this.i >= 4) {
          this.cancel();
        }
      }
    }.runTaskTimerAsynchronously((Plugin) Practice.getInstance(), 0L, 5L);
  }),
  WISDOM(CC.PRIMARY + "Wisdom", Material.BOOK, "minion.effects.wisdom", (player, watchers) -> {
    Location loc = player.getLocation().clone().add(0.0, 2.8, 0.0);
    for (double d = 0.0; d < Math.PI * 2; d += 0.5235987755982988) {
      double x = Math.sin(d);
      double z = Math.cos(d);
      Vector v = new Vector(x, -0.5, z).multiply(1.5);
      PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.ENCHANTMENT_TABLE, false, (float)loc.getX(), (float)loc.getY(), (float)loc.getZ(), (float)v.getX(), (float)v.getY(), (float)v.getZ(), 0.7f, 0, new int[0]);
      PacketPlayOutWorldParticles books = new PacketPlayOutWorldParticles(EnumParticle.ITEM_CRACK, false, (float)player.getLocation().getX(), (float)player.getLocation().getY(), (float)player.getLocation().getZ(), 0.0f, 0.0f, 0.0f, 0.5f, 1, new int[]{Material.BOOK.getId(), 0});
      for (Player watcher : watchers) {
        for (int i = 0; i < 5; ++i) {
          ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket((Packet)packet);
        }
        ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket((Packet)books);
        watcher.playSound(player.getLocation(), Sound.IRONGOLEM_DEATH, 0.3f, 0.4f);
      }
    }
  }),
  SOUL(CC.PRIMARY + "Soul", Material.MAGMA_CREAM, "minion.effects.soul", (player, watchers) -> new BukkitRunnable(){
    final Location loc;
    double t;
    final double r = 0.75;
    {
      this.loc = player.getLocation();
      this.t = 0.0;
      final double r = 0.75;
    }

    public void run() {
      this.t += 0.3141592653589793;
      double x = 0.75 * Math.cos(this.t);
      double y = 0.25 * this.t;
      double z = 0.75 * Math.sin(this.t);
      this.loc.add(x, y, z);
      for (Player watcher : watchers) {
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.VILLAGER_HAPPY, false, (float)this.loc.getX(), (float)this.loc.getY(), (float)this.loc.getZ(), 0.0f, 0.0f, 0.0f, 1.0f, 0, new int[0]);
        ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket((Packet)packet);
        if (this.t > Math.PI * 3) {
          PacketPlayOutWorldParticles pop = new PacketPlayOutWorldParticles(EnumParticle.HEART, false, (float)this.loc.getX(), (float)this.loc.getY(), (float)this.loc.getZ(), 0.0f, 0.0f, 0.0f, 1.0f, 0, new int[0]);
          ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket((Packet)pop);
          Bukkit.getScheduler().runTaskLater((Plugin)Practice.getInstance(), () -> watcher.playSound(this.loc, Sound.NOTE_PLING, 1.0f, 1.6f), 1L);
          Bukkit.getScheduler().runTaskLater((Plugin)Practice.getInstance(), () -> watcher.playSound(this.loc, Sound.NOTE_PLING, 1.0f, 1.7f), 3L);
          Bukkit.getScheduler().runTaskLater((Plugin)Practice.getInstance(), () -> watcher.playSound(this.loc, Sound.NOTE_PLING, 1.0f, 1.8f), 5L);
          Bukkit.getScheduler().runTaskLater((Plugin)Practice.getInstance(), () -> watcher.playSound(this.loc, Sound.NOTE_PLING, 1.0f, 1.9f), 7L);
          Bukkit.getScheduler().runTaskLater((Plugin)Practice.getInstance(), () -> watcher.playSound(this.loc, Sound.NOTE_PLING, 1.0f, 2.0f), 9L);
          this.cancel();
          continue;
        }
        this.loc.subtract(x, y, z);
      }
    }
  }.runTaskTimerAsynchronously((Plugin)Practice.getInstance(), 0L, 1L));

  private final String name;
  private final Material icon;
  private final String permission;
  private final EffectCallable callable;

  private SpecialEffects(String name, Material icon, String permission, EffectCallable callable) {
    this.name = name;
    this.icon = icon;
    this.permission = permission;
    this.callable = callable;
  }

  public static SpecialEffects getByName(String input) {
    for (SpecialEffects type : SpecialEffects.values()) {
      if (!type.name().equalsIgnoreCase(input) && !type.getName().equalsIgnoreCase(input)) continue;
      return type;
    }
    return null;
  }

  public boolean hasPermission(Player player) {
    return player.hasPermission(this.permission) || this.permission.isEmpty();
  }

  public String getName() {
    return this.name;
  }

  public Material getIcon() {
    return this.icon;
  }

  public String getPermission() {
    return this.permission;
  }

  public EffectCallable getCallable() {
    return this.callable;
  }
}

