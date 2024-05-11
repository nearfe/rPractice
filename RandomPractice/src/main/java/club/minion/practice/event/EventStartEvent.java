package club.minion.practice.event;

import com.conaxgames.event.BaseEvent;
import club.minion.practice.events.PracticeEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EventStartEvent extends BaseEvent {
	private final PracticeEvent event;
}
