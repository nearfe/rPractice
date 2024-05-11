package com.conaxgames.api.impl;

import com.conaxgames.api.request.Request;
import com.conaxgames.util.finalutil.MapUtil;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class DisguiseRequest implements Request {
    private final UUID uniqueId;
    private final boolean status; // true for disguise, false for undisguise
    private final String disguiseName, disguiseSkin, disguiseRank;

    @Override
    public String getPath() {
        return "/disguise/" + this.uniqueId.toString() + "/update";
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);
        if (status) {
            map.put("disguiseName", disguiseName);
            map.put("disguiseSkin", disguiseSkin);
            map.put("disguiseRank", disguiseRank);
        }
        return map;
    }

    public static final class DisguiseNameRequest implements Request {
        public String getPath() {
            return "/disguise/random-name";
        }

        public Map<String, Object> toMap() {
            return null;
        }
    }

    public static final class DisguiseListRequest implements Request {
        public String getPath() {
            return "/disguise/list";
        }

        public Map<String, Object> toMap() {
            return null;
        }
    }

    @RequiredArgsConstructor
    public static final class DisguiseAddRequest implements Request {
        private final String name, skin;

        public String getPath() {
            return "/disguise/update";
        }

        public Map<String, Object> toMap() {
            return MapUtil.of("add", true,
                    "name", name,
                    "skin", skin);
        }
    }

    @RequiredArgsConstructor
    public static final class DisguiseDeleteRequest implements Request {
        private final int id;

        public String getPath() {
            return "/disguise/update";
        }

        public Map<String, Object> toMap() {
            return MapUtil.of("add", false,
                    "id", id);
        }
    }
}
