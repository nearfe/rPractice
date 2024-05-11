package com.conaxgames.gson;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.conaxgames.CorePlugin;
import com.conaxgames.util.CustomLocation;

import java.io.IOException;

public class CustomLocationTypeAdapterFactory implements TypeAdapterFactory {

	public static LocationData serialize(CustomLocation customLocation) {
		Preconditions.checkNotNull(customLocation);

		return new LocationData(customLocation.getWorld(), customLocation.getX(), customLocation.getY(),
				customLocation.getZ(), customLocation.getYaw(), customLocation.getPitch());
	}

	public static CustomLocation deserialize(LocationData locationData) {
		Preconditions.checkNotNull(locationData);

		return new CustomLocation(locationData.getWorld(), locationData.getX(), locationData.getY(), locationData
				.getZ(), locationData.getYaw(), locationData.getPitch());
	}

	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
		if (!CustomLocation.class.isAssignableFrom(typeToken.getRawType())) {
			return null;
		}

		return new TypeAdapter<T>() {
			@Override
			public void write(JsonWriter jsonWriter, T location) throws IOException {
				if (location == null) {
					jsonWriter.nullValue();
				} else {
					CorePlugin.GSON.toJson(serialize((CustomLocation) location), LocationData.class, jsonWriter);
				}
			}

			@Override
			public T read(JsonReader jsonReader) throws IOException {
				if (jsonReader.peek() == null) {
					jsonReader.nextNull();
					return null;
				} else {
					return (T) deserialize(CorePlugin.GSON.fromJson(jsonReader, LocationData.class));
				}
			}
		};
	}

	@Getter
	@RequiredArgsConstructor
	private static class LocationData {

		private final String world;
		private final double x;
		private final double y;
		private final double z;
		private final float yaw;
		private final float pitch;
	}
}
