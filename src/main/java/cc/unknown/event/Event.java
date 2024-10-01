package cc.unknown.event;

import cc.unknown.Haru;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Event {
	private boolean cancelled;
	
	public Event call() {
	    Haru.instance.getEventBus().post(this);
	    return this;
	}
}