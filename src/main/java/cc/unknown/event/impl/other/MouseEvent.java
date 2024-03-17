package cc.unknown.event.impl.other;

import cc.unknown.event.Event;

public class MouseEvent extends Event {
	
	private int button;

    /**
     * Constructs a new MouseEvent object with the specified button code.
     *
     * @param button The button code of the pressed or released mouse button.
     */
    public MouseEvent(int button) {
        this.button = button;
    }

    /**
     * Retrieves the button code of the pressed or released mouse button.
     *
     * @return The button code of the pressed or released mouse button.
     */
    public int getButton() {
        return button;
    }

    /**
     * Sets the button code of the pressed or released mouse button.
     *
     * @param button The new button code of the pressed or released mouse button.
     */
    public void setButton(int button) {
        this.button = button;
    }
}