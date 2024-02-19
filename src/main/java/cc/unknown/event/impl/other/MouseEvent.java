package cc.unknown.event.impl.other;

import cc.unknown.event.impl.Event;

public class MouseEvent extends Event {
	private int button;

	public MouseEvent(int button) {
		this.button = button;
	}

	public int getButton() {
		return this.button;
	}

	public void setButton(int button) {
		this.button = button;
	}
}