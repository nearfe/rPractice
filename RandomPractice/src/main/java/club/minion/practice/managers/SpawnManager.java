package club.minion.practice.managers;

import com.conaxgames.util.CustomLocation;
import club.minion.practice.Practice;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SpawnManager {
	private final Practice plugin = Practice.getInstance();

	private CustomLocation spawnLocation;
	private CustomLocation spawnMin, cornersMin;
	private CustomLocation spawnMax, cornersMax;

	private CustomLocation editorLocation;
	private CustomLocation editorMin;
	private CustomLocation editorMax;

	private CustomLocation sumoLocation, lmsLocation, parkourLocation, parkourGameLocation, oitcLocation, cornersLocation;
	private CustomLocation sumoFirst;
	private CustomLocation sumoSecond;

	private List<CustomLocation> runnerLocations = new ArrayList<>();
	private List<CustomLocation> lmsLocations = new ArrayList<>();
	private List<CustomLocation> oitcLocations = new ArrayList<>();

	public SpawnManager() {
		this.loadConfig();
	}

	private void loadConfig() {
		FileConfiguration config = this.plugin.getMainConfig().getConfig();
		if(config.contains("spawnLocation")) {
			this.spawnLocation = CustomLocation.stringToLocation(config.getString("spawnLocation"));
			this.spawnMin = CustomLocation.stringToLocation(config.getString("spawnMin"));
			this.spawnMax = CustomLocation.stringToLocation(config.getString("spawnMax"));
			this.editorLocation = CustomLocation.stringToLocation(config.getString("editorLocation"));
			this.editorMin = CustomLocation.stringToLocation(config.getString("editorMin"));
			this.editorMax = CustomLocation.stringToLocation(config.getString("editorMax"));

			config.getStringList("runnerSpawnpoints").forEach(point ->
					runnerLocations.add(CustomLocation.stringToLocation(point)));

			config.getStringList("oitcSpawnpoints").forEach(point ->
					oitcLocations.add(CustomLocation.stringToLocation(point)));
		}

		if(config.contains("sumoLocation")) {
			this.sumoLocation = CustomLocation.stringToLocation(config.getString("sumoLocation"));
			this.sumoFirst = CustomLocation.stringToLocation(config.getString("sumoFirst"));
			this.sumoSecond = CustomLocation.stringToLocation(config.getString("sumoSecond"));
		}

		if(config.contains("parkourLocation")) {
			this.parkourLocation = CustomLocation.stringToLocation(config.getString("parkourLocation"));
		}

		if(config.contains("parkourGameLocation")) {
			this.parkourGameLocation = CustomLocation.stringToLocation(config.getString("parkourGameLocation"));
		}

		if(config.contains("oitcLocation")) {
			this.oitcLocation = CustomLocation.stringToLocation(config.getString("oitcLocation"));
		}

		if(config.contains("cornersLocation")) {
			this.cornersLocation = CustomLocation.stringToLocation(config.getString("cornersLocation"));
			this.cornersMax = CustomLocation.stringToLocation(config.getString("cornersMax"));
			this.cornersMin = CustomLocation.stringToLocation(config.getString("cornersMin"));
		}
	}

	public void saveConfig() {
		FileConfiguration config = this.plugin.getMainConfig().getConfig();
		if(spawnLocation != null)
			config.set("spawnLocation", CustomLocation.locationToString(this.spawnLocation));
		if(spawnMin != null)
			config.set("spawnMin", CustomLocation.locationToString(this.spawnMin));
		if(spawnMax != null)
			config.set("spawnMax", CustomLocation.locationToString(this.spawnMax));
		if(editorLocation != null)
			config.set("editorLocation", CustomLocation.locationToString(this.editorLocation));
		if(editorMin != null)
			config.set("editorMin", CustomLocation.locationToString(this.editorMin));
		if(editorMax != null)
			config.set("editorMax", CustomLocation.locationToString(this.editorMax));
		if(runnerLocations.size() > 0)
			config.set("runnerSpawnpoints", getLocations(runnerLocations));
		if(sumoLocation != null)
			config.set("sumoLocation", CustomLocation.locationToString(this.sumoLocation));
		if(sumoFirst != null)
			config.set("sumoFirst", CustomLocation.locationToString(this.sumoFirst));
		if(sumoSecond != null)
			config.set("sumoSecond", CustomLocation.locationToString(this.sumoSecond));
		if(parkourLocation != null)
			config.set("parkourLocation", CustomLocation.locationToString(this.parkourLocation));
		if(parkourGameLocation != null)
			config.set("parkourGameLocation", CustomLocation.locationToString(this.parkourGameLocation));
		if(oitcLocation != null)
			config.set("oitcLocation", CustomLocation.locationToString(this.oitcLocation));
		if(oitcLocations.size() > 0)
			config.set("oitcSpawnpoints", getLocations(oitcLocations));
		if(cornersLocation != null)
			config.set("cornersLocation", CustomLocation.locationToString(this.cornersLocation));
		if(cornersMax != null)
			config.set("cornersMax", CustomLocation.locationToString(this.cornersMax));
		if(cornersMin != null)
			config.set("cornersMin", CustomLocation.locationToString(this.cornersMin));
		this.plugin.getMainConfig().save();
	}

	private List<String> getLocations(List<CustomLocation> locations) {
		List<String> toReturn = new ArrayList<>();
		locations.forEach(location -> toReturn.add(CustomLocation.locationToString(location)));
		return toReturn;
	}
}
