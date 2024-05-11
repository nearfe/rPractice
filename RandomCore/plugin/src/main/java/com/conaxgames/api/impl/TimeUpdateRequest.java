package com.conaxgames.api.impl;

import com.conaxgames.util.finalutil.MapUtil;
import lombok.RequiredArgsConstructor;
import com.conaxgames.api.request.Request;

import java.util.Map;

/**
 * Created by Marko on 19.12.2018.
 */
@RequiredArgsConstructor
public final class TimeUpdateRequest implements Request {

    private final String time;
    private final int id;

    @Override public String getPath() {
        return "/player/" + this.id + "/update-time";
    }

    @Override public Map<String, Object> toMap() {
        return MapUtil.of("time", this.time);
    }
}