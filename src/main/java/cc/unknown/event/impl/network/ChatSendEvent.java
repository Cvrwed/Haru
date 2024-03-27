package cc.unknown.event.impl.network;

import cc.unknown.event.Event;

public class ChatSendEvent extends Event {
	
	private String message;

	/**
	 * @param message
	 */
	public ChatSendEvent(String message) {
		this.message = message;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}
