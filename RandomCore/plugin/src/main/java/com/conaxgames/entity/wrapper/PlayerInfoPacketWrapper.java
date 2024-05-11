package com.conaxgames.entity.wrapper;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;

import java.lang.reflect.Field;
import java.util.List;

public class PlayerInfoPacketWrapper {

	private static Field INFO_DATA_LIST_FIELD;
	private static Field ENUM_ACTION_FIELD;

	@Getter
	private final PacketPlayOutPlayerInfo packet;

	public PlayerInfoPacketWrapper() {
		this(new PacketPlayOutPlayerInfo());
	}

	public PlayerInfoPacketWrapper(PacketPlayOutPlayerInfo packet) {
		this.packet = packet;

		if (INFO_DATA_LIST_FIELD == null) {
			try {
				INFO_DATA_LIST_FIELD = this.packet.getClass().getDeclaredField("b");
				ENUM_ACTION_FIELD = this.packet.getClass().getDeclaredField("a");
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<PacketPlayOutPlayerInfo.PlayerInfoData> getInfoData() {
		try {
			INFO_DATA_LIST_FIELD.setAccessible(true);
			return (List<PacketPlayOutPlayerInfo.PlayerInfoData>) INFO_DATA_LIST_FIELD.get(this.packet);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void setInfoData(List<PacketPlayOutPlayerInfo.PlayerInfoData> infoData) {
		try {
			INFO_DATA_LIST_FIELD.setAccessible(true);
			INFO_DATA_LIST_FIELD.set(this.packet, infoData);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public PacketPlayOutPlayerInfo.EnumPlayerInfoAction getPacketAction() {
		try {
			ENUM_ACTION_FIELD.setAccessible(true);
			return (PacketPlayOutPlayerInfo.EnumPlayerInfoAction) ENUM_ACTION_FIELD.get(this.packet);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void setPacketAction(PacketPlayOutPlayerInfo.EnumPlayerInfoAction action) {
		try {
			ENUM_ACTION_FIELD.setAccessible(true);
			ENUM_ACTION_FIELD.set(this.packet, action);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
