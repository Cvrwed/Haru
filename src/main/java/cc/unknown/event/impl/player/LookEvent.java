package cc.unknown.event.impl.player;

import cc.unknown.event.Event;

public class LookEvent extends Event {

	private float pitch, prevPitch, yaw, prevYaw;

    /**
     * Constructs a new LookEvent with the specified pitch and yaw angles.
     *
     * @param pitch The current pitch angle.
     * @param yaw The current yaw angle.
     */
    public LookEvent(float pitch, float yaw) {
        this.pitch = pitch;
        this.yaw = yaw;
    }

    /**
     * Constructs a new LookEvent with the specified pitch, previous pitch, yaw, and previous yaw angles.
     *
     * @param pitch The current pitch angle.
     * @param prevPitch The previous pitch angle.
     * @param yaw The current yaw angle.
     * @param prevYaw The previous yaw angle.
     */
    public LookEvent(float pitch, float prevPitch, float yaw, float prevYaw) {
        this.pitch = pitch;
        this.prevPitch = prevPitch;
        this.yaw = yaw;
        this.prevYaw = prevYaw;
    }

    /**
     * Gets the current pitch angle.
     *
     * @return The current pitch angle.
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * Sets the current pitch angle.
     *
     * @param pitch The current pitch angle.
     */
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    /**
     * Gets the previous pitch angle.
     *
     * @return The previous pitch angle.
     */
    public float getPrevPitch() {
        return prevPitch;
    }

    /**
     * Sets the previous pitch angle.
     *
     * @param prevPitch The previous pitch angle.
     */
    public void setPrevPitch(float prevPitch) {
        this.prevPitch = prevPitch;
    }

    /**
     * Gets the current yaw angle.
     *
     * @return The current yaw angle.
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * Sets the current yaw angle.
     *
     * @param yaw The current yaw angle.
     */
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    /**
     * Gets the previous yaw angle.
     *
     * @return The previous yaw angle.
     */
    public float getPrevYaw() {
        return prevYaw;
    }

    /**
     * Sets the previous yaw angle.
     *
     * @param prevYaw The previous yaw angle.
     */
    public void setPrevYaw(float prevYaw) {
        this.prevYaw = prevYaw;
    }
}
