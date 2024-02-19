package cc.unknown.event.impl;

import cc.unknown.Haru;

public class Event {
	
	public Event call() {
		Haru.instance.getEventBus().post(this);
		return this;
	}
}