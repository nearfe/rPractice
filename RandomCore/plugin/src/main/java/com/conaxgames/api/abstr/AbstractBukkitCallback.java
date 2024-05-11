package com.conaxgames.api.abstr;

import org.bukkit.Bukkit;
import com.conaxgames.api.callback.Callback;
import com.conaxgames.api.callback.ErrorCallback;

/**
 * @since 2017-11-28
 */
public abstract class AbstractBukkitCallback implements Callback, ErrorCallback {

	@Override
	public void onError(String message) {
		Bukkit.getLogger().severe("[WEBAPI]: " + message);
	}
}
