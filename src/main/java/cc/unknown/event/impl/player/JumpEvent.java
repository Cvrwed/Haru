package cc.unknown.event.impl.player;

import cc.unknown.event.Event;

public class JumpEvent extends Event {
	private float yaw;

    /**
     * Constructs a new JumpEvent with the specified yaw angle.
     *
     * @param yaw The yaw angle of the jump.
     */
    public JumpEvent(float yaw) {
        this.yaw = yaw;
    }

    /**
     * Gets the yaw angle of the jump.
     *
     * @return The yaw angle of the jump.
     */
    public float getYaw() {
        return this.yaw;
    }

    /**
     * Sets the yaw angle of the jump.
     *
     * @param yaw The yaw angle of the jump.
     */
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}
