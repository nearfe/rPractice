package club.minion.practice.request;

import com.conaxgames.api.request.Request;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class PracticeFetchLeaderboardRequest implements Request {
    private final String ladderName;

    @Override
    public String getPath() {
        return "/practice/leaderboards/" + this.ladderName;
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }
}
