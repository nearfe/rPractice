package com.conaxgames.api.impl;

import com.conaxgames.util.finalutil.MapUtil;
import lombok.RequiredArgsConstructor;
import com.conaxgames.api.request.Request;

import java.util.Map;

/**
 * Created by Marko on 19.12.2018.
 */

@RequiredArgsConstructor
public final class ChatUpdateRequest implements Request {

    private final String type;
    private final boolean value;
    private final int id;

    @Override public String getPath() {
        return "/player/" + this.id + "/update-chat";
    }

    @Override public Map<String, Object> toMap() {
        return MapUtil.of("type", this.type,
                "value", this.value);
    }
}
