package cc.unknown.event;

import cc.unknown.Haru;

public class Event {
	
	private boolean cancelled;
	
	/**
	 * Calls the event, posting it to the event bus.
	 * 
	 * @return The event instance.
	 */
	public Event call() {
	    Haru.instance.getEventBus().post(this);
	    return this;
	}

	/**
	 * Checks if the event is cancelled.
	 * 
	 * @return {@code true} if the event is cancelled, {@code false} otherwise.
	 */
	public boolean isCancelled() {
	    return cancelled;
	}

	/**
	 * Sets the cancelled state of the event.
	 * 
	 * @param cancelled {@code true} to cancel the event, {@code false} to keep it active.
	 */
	public void setCancelled(boolean cancelled) {
	    this.cancelled = cancelled;
	}
}