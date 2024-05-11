package club.minion.practice.settings;

import club.minion.practice.killmessages.KillMessages;
import lombok.Getter;

@Getter
public class SettingsInfo {

    private boolean duelRequests = true;
    private boolean partyInvites = true;
    private boolean scoreboardToggled = true;
    private boolean spectatorsAllowed = true;
    private boolean playerVisibility = true;
    private boolean deathLightning = false;
    private boolean pingScoreboardToggled = true;

    private boolean Sidebar = true;

    private KillMessages killMessages = KillMessages.NONE;

}
