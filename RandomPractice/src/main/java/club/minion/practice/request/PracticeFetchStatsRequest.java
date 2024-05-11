package club.minion.practice.request;

import com.conaxgames.api.request.Request;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PracticeFetchStatsRequest implements Request {

	private final UUID playerUuid;

	@Override
	public String getPath() {
		return "/practice/" + this.playerUuid.toString();
	}

	@Override
	public Map<String, Object> toMap() {
		return null;
	}
}
