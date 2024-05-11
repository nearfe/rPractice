package server.pvptemple.api.model;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "players")
public class Player implements Serializable {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private int playerId;

	@Column(name = "mute_time")
	private Timestamp muteTime = null;
	@Column(name = "ban_time")
	private Timestamp banTime = null;

	@Column(name = "first_login")
	private Timestamp firstLogin = null;
	@Column(name = "last_login")
	private Timestamp lastLogin = null;

	@Column
	private String ipAddress = null;
	@Column
	private String name = null;

	@Column(name = "uuid")
	private String uniqueId;

	@Column(name = "worldTime")
	private String worldTime = "DAY";
	@Column(name = "customPrefix")
	private String customPrefix = "";
	@Column(name = "chat_color")
	private String chatColor = null;
	@Column(name = "rank")
	private String rank = null;
	@Column(name = "authSecret")
	private String authSecret = null;
	@Column(name = "lastAuthAddress")
	private String lastAuthAddress = null;
	@Column(name = "disguiseName")
	private String disguiseName = null;
	@Column(name = "disguiseRank")
	private String disguiseRank = null;
	@Column(name = "disguiseSkin")
	private String disguiseSkin = null;

	@Column(name = "lastDisguise")
	private Long lastDisguise = null;

	@Column(name = "canSeeMessages")
	private boolean canSeeMessages = true;
	@Column(name = "canSeeStaffMessages")
	private boolean canSeeStaffMessages = true;
	@Column(name = "chatEnabled")
	private boolean chatEnabled = true;
	@Column(name = "authExempt")
	private boolean authExempt = false;

	@Column(name = "blacklisted")
	private boolean blacklisted = false;
	@Column(name = "ip_banned")
	private boolean ipBanned = false;
	@Column(name = "banned")
	private boolean banned = false;
	@Column(name = "muted")
	private boolean muted = false;

	public JsonObject toJson() {
		JsonObject object = new JsonObject();
		object.addProperty("name", this.name);
		object.addProperty("rank", this.rank);
		object.addProperty("banned", this.banned);
		object.addProperty("ip_banned", this.ipBanned);
		object.addProperty("muted", this.muted);
		object.addProperty("blacklisted", this.blacklisted);
		object.addProperty("ip", this.ipAddress);
		return object;
	}
}
