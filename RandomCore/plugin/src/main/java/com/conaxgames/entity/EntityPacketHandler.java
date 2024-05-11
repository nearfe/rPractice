package com.conaxgames.entity;

public class EntityPacketHandler {
}

  /*  private static Field ENTITY_ID_FIELD;

    public EntityPacketHandler() {
        if (ENTITY_ID_FIELD == null) {
            try {
                ENTITY_ID_FIELD = PacketPlayInUseEntity.class.getDeclaredField("a");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleReceivedPacket(PlayerConnection playerConnection, Packet packet) {
        if (packet instanceof PacketPlayInUseEntity) {
            PacketPlayInUseEntity useEntity = (PacketPlayInUseEntity) packet;
            Entity entity = ((PacketPlayInUseEntity) packet).a(playerConnection.player.world);

            // NPC's will always return null, as we track them ourselves.
            if (entity != null) {
                return;
            }

            ENTITY_ID_FIELD.setAccessible(true);
            try {
                int id = (int) ENTITY_ID_FIELD.get(useEntity);
                PlayerWrapper player = CorePlugin.getInstance().getEntityManager().getFakePlayers().get(id);
                if (player == null) {
                    return;
                }

                if (player.getEntityInteraction() != null) {
                    PlayerInteractFakeEntityEvent event =
                            new PlayerInteractFakeEntityEvent(playerConnection.player.getBukkitEntity(), player);
                    player.getEntityInteraction().interact(playerConnection.player.getBukkitEntity(), event);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (Exception e) {
                //
            }
        }
    }

    @Override
    public void handleSentPacket(PlayerConnection playerConnection, Packet packet) {

    }

}
 */