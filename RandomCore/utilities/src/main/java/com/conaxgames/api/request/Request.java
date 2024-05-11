package com.conaxgames.api.request;

import java.util.Map;

public interface Request {

	String getPath();

	Map<String, Object> toMap();

}
