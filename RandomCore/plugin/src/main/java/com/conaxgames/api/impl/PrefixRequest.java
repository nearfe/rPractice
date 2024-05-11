package com.conaxgames.api.impl;

import com.conaxgames.api.request.Request;
import com.conaxgames.util.finalutil.MapUtil;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.UUID;

public class PrefixRequest {
    @RequiredArgsConstructor
    public static final class PrefixAddRequest implements Request {
        private final String prefix;
        private final String name;

        @Override public String getPath() {
            return "/player/" + this.name + "/prefixes/" + this.prefix;
        }

        @Override public Map<String, Object> toMap() {
            return null;
        }
    }

    @RequiredArgsConstructor
    public static final class PrefixUpdateRequest implements Request {
        private final String prefix;
        private final String name;

        @Override public String getPath() {
            return "/player/" + this.name + "/update-prefix";
        }

        @Override public Map<String, Object> toMap() {
            return MapUtil.of("prefix", this.prefix);
        }
    }

    @RequiredArgsConstructor
    public static final class PrefixListRequest implements Request {
        private final UUID uniqueId;

        @Override public String getPath() {
            return "/player/" + this.uniqueId.toString() + "/prefixes";
        }

        @Override public Map<String, Object> toMap() {
            return null;
        }
    }
}
