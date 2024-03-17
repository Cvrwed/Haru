package cc.unknown.event.impl.move;

import cc.unknown.event.Event;

public class PreUpdateEvent extends Event {

	private double x, y, z;
	private float yaw, pitch;
	private boolean onGround;

    /**
     * Constructs a new PreUpdateEvent object with the specified player position and orientation values.
     *
     * @param x          The x-coordinate of the player's position.
     * @param y          The y-coordinate of the player's position.
     * @param z          The z-coordinate of the player's position.
     * @param yaw        The yaw (horizontal rotation) of the player's orientation.
     * @param pitch      The pitch (vertical rotation) of the player's orientation.
     * @param onGround   Indicates if the player is on the ground.
     */
    public PreUpdateEvent(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    /**
     * Retrieves the x-coordinate of the player's position.
     *
     * @return The x-coordinate of the player's position.
     */
    public double getX() {
        return x;
    }

    /**
     * Sets the x-coordinate of the player's position.
     *
     * @param x The new x-coordinate of the player's position.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Retrieves the y-coordinate of the player's position.
     *
     * @return The y-coordinate of the player's position.
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the y-coordinate of the player's position.
     *
     * @param y The new y-coordinate of the player's position.
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Retrieves the z-coordinate of the player's position.
     *
     * @return The z-coordinate of the player's position.
     */
    public double getZ() {
        return z;
    }

    /**
     * Sets the z-coordinate of the player's position.
     *
     * @param z The new z-coordinate of the player's position.
     */
    public void setZ(double z) {
        this.z = z;
    }

    /**
     * Retrieves the yaw (horizontal rotation) of the player's orientation.
     *
     * @return The yaw of the player's orientation.
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * Sets the yaw (horizontal rotation) of the player's orientation.
     *
     * @param yaw The new yaw of the player's orientation.
     */
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    /**
     * Retrieves the pitch (vertical rotation) of the player's orientation.
     *
     * @return The pitch of the player's orientation.
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * Sets the pitch (vertical rotation) of the player's orientation.
     *
     * @param pitch The new pitch of the player's orientation.
     */
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    /**
     * Checks if the player is on the ground.
     *
     * @return {@code true} if the player is on the ground, {@code false} otherwise.
     */
    public boolean isOnGround() {
        return onGround;
    }

    /**
     * Sets the on-ground state of the player.
     *
     * @param onGround The new on-ground state of the player.
     */
    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
}
