package cc.unknown.event.impl.api;

import cc.unknown.event.impl.Event;

public class CancellableEvent extends Event {

	private boolean cancelled;

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}