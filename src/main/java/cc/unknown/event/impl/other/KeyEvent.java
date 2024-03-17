package cc.unknown.event.impl.other;

import cc.unknown.event.Event;

public class KeyEvent extends Event {
	
    private int key;

    /**
     * Constructs a new KeyEvent object with the specified key code.
     *
     * @param key The key code of the pressed or released key.
     */
    public KeyEvent(int key) {
        this.key = key;
    }

    /**
     * Retrieves the key code of the pressed or released key.
     *
     * @return The key code of the pressed or released key.
     */
    public int getKey() {
        return key;
    }

    /**
     * Sets the key code of the pressed or released key.
     *
     * @param key The new key code of the pressed or released key.
     */
    public void setKey(int key) {
        this.key = key;
    }
}
