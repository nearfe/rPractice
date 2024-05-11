package com.conaxgames.api.callback;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.logging.Logger;

/**
 * @since 2017-11-29
 */
@RequiredArgsConstructor
@Getter
public abstract class AbstractCallback implements Callback, ErrorCallback {

	private final String errorMessage;
	private boolean errorCalled = false;

	@Override
	public void onError(String message) {
		this.errorCalled = true;
		if (!this.errorMessage.isEmpty()) {
			Logger.getGlobal().severe(this.errorMessage);
		}

		Logger.getGlobal().severe(message);
	}

	public void throwException() throws Exception {
		if (this.errorCalled) {
			throw new Exception(this.errorMessage);
		}
	}
}
