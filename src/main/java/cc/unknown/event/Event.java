package cc.unknown.event;

import cc.unknown.Haru;

public class Event {
	
	private boolean cancelled;
	
	public Event call() {
		Haru.instance.getEventBus().post(this);
		return this;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}