package com.conaxgames.api.impl;

import com.conaxgames.api.request.Request;
import com.conaxgames.util.finalutil.MapUtil;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class AuthenticationRequest implements Request {
    private final String name;
    private final String address;
    private final String secret;
    private final boolean success, exempt;

    @Override
    public String getPath() {
        return "/player/" + this.name + "/authentication";
    }

    @Override
    public Map<String, Object> toMap() {
        return MapUtil.of(
                "address", this.address == null ? "" : this.address,
                "secret", this.secret == null ? "" : this.secret,
                "success", this.success,
                "exempt", this.exempt
        );
    }
}
