package cc.unknown.event.impl.player;

import cc.unknown.event.Event;

public class StrafeEvent extends Event {
	
    private float strafe, forward, friction, yaw;

    /**
     * Constructs a new StrafeEvent with the specified strafe, forward, friction, and yaw values.
     *
     * @param strafe The amount of strafe movement.
     * @param forward The amount of forward movement.
     * @param friction The friction coefficient.
     * @param yaw The yaw angle.
     */
    public StrafeEvent(float strafe, float forward, float friction, float yaw) {
        this.strafe = strafe;
        this.forward = forward;
        this.friction = friction;
        this.yaw = yaw;
    }

    /**
     * Gets the amount of strafe movement.
     *
     * @return The amount of strafe movement.
     */
    public float getStrafe() {
        return strafe;
    }

    /**
     * Sets the amount of strafe movement.
     *
     * @param strafe The amount of strafe movement.
     */
    public void setStrafe(float strafe) {
        this.strafe = strafe;
    }

    /**
     * Gets the amount of forward movement.
     *
     * @return The amount of forward movement.
     */
    public float getForward() {
        return forward;
    }

    /**
     * Sets the amount of forward movement.
     *
     * @param forward The amount of forward movement.
     */
    public void setForward(float forward) {
        this.forward = forward;
    }

    /**
     * Gets the friction coefficient.
     *
     * @return The friction coefficient.
     */
    public float getFriction() {
        return friction;
    }

    /**
     * Sets the friction coefficient.
     *
     * @param friction The friction coefficient.
     */
    public void setFriction(float friction) {
        this.friction = friction;
    }

    /**
     * Gets the yaw angle.
     *
     * @return The yaw angle.
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * Sets the yaw angle.
     *
     * @param yaw The yaw angle.
     */
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}
