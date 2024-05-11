package club.minion.practice.profile;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.UUID;

public class Profile {

    public static final int DEFAULT_ELO = 1000;
    public static Map<UUID, Profile> players = Maps.newHashMap();

}
