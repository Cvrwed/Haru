package cc.unknown.event.impl.player;

import cc.unknown.event.Event;

public class JumpEvent extends Event {
    private float yaw;

    /**
     * Constructs a JumpEvent with the specified yaw value.
     *
     * @param yaw The yaw value associated with the event.
     */
    public JumpEvent(final float yaw) {
        this.yaw = yaw;
    }

    /**
     * Gets the yaw value associated with the event.
     *
     * @return The yaw value associated with the event.
     */
    public float getYaw() {
        return this.yaw;
    }

    /**
     * Sets the yaw value associated with the event.
     *
     * @param yaw The yaw value to set.
     */
    public void setYaw(final float yaw) {
        this.yaw = yaw;
    }
}