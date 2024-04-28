package cc.unknown.event.impl.move;

import cc.unknown.event.Event;

public class MotionEvent extends Event {

    private double x, y, z;
    private float yaw, pitch;
    private boolean onGround;
    private final MotionType motionType;

    /**
     * Constructs a MotionEvent with the specified motion type, coordinates, yaw, pitch, and on-ground status.
     *
     * @param motionType The motion type of the event (Pre or Post).
     * @param x          The x-coordinate associated with the event.
     * @param y          The y-coordinate associated with the event.
     * @param z          The z-coordinate associated with the event.
     * @param yaw        The yaw value associated with the event.
     * @param pitch      The pitch value associated with the event.
     * @param onGround   The on-ground status associated with the event.
     */
    public MotionEvent(MotionType motionType, double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.motionType = motionType;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    /**
     * Gets the x-coordinate associated with the event.
     *
     * @return The x-coordinate associated with the event.
     */
    public double getX() {
        return x;
    }

    /**
     * Sets the x-coordinate associated with the event.
     *
     * @param x The x-coordinate to set.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Gets the y-coordinate associated with the event.
     *
     * @return The y-coordinate associated with the event.
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the y-coordinate associated with the event.
     *
     * @param y The y-coordinate to set.
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Gets the z-coordinate associated with the event.
     *
     * @return The z-coordinate associated with the event.
     */
    public double getZ() {
        return z;
    }

    /**
     * Sets the z-coordinate associated with the event.
     *
     * @param z The z-coordinate to set.
     */
    public void setZ(double z) {
        this.z = z;
    }

    /**
     * Gets the yaw value associated with the event.
     *
     * @return The yaw value associated with the event.
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * Sets the yaw value associated with the event.
     *
     * @param yaw The yaw value to set.
     */
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    /**
     * Gets the pitch value associated with the event.
     *
     * @return The pitch value associated with the event.
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * Sets the pitch value associated with the event.
     *
     * @param pitch The pitch value to set.
     */
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    /**
     * Checks if the on-ground status of the event is true.
     *
     * @return true if the on-ground status is true, false otherwise.
     */
    public boolean isOnGround() {
        return onGround;
    }

    /**
     * Sets the on-ground status associated with the event.
     *
     * @param onGround The on-ground status to set.
     */
    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    /**
     * Checks if the motion type of the event is "Pre".
     *
     * @return true if the motion type is "Pre", false otherwise.
     */
    public boolean isPre() {
        return motionType == MotionType.Pre;
    }

    /**
     * Checks if the motion type of the event is "Post".
     *
     * @return true if the motion type is "Post", false otherwise.
     */
    public boolean isPost() {
        return motionType == MotionType.Post;
    }

	/**
     * Enumerates the possible motion types of a motion event (Pre or Post).
     */
    public enum MotionType {
        Pre, Post
    }
}
