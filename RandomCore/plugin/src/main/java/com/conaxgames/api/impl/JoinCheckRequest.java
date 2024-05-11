package com.conaxgames.api.impl;

import lombok.RequiredArgsConstructor;
import com.conaxgames.api.request.Request;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Marko on 20.12.2018.
 */
@RequiredArgsConstructor
public final class JoinCheckRequest implements Request {

    private final UUID uniqueId;

    @Override public String getPath() {
        return "/player/" + this.uniqueId.toString() + "/get-alts";
    }

    @Override public Map<String, Object> toMap() {
        return null;
    }

}
