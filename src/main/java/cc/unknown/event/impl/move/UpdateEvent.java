package cc.unknown.event.impl.move;

import cc.unknown.event.Event;

public class UpdateEvent extends Event {
	private final Action action;
    private double x, y, z;
    private float yaw, pitch;
    private boolean onGround;
    
    public UpdateEvent(Action action) {
        this.action = action;
    }

    /**
     * Private constructor of the UpdateEvent class.
     * Static methods are used to create instances of this event.
     * @param action Action of the event (PRE or POST)
     * @param x Player's x coordinate
     * @param y Player's y coordinate
     * @param z Player's z coordinate
     * @param yaw Player's horizontal (yaw) rotation
     * @param pitch Player's vertical (pitch) rotation
     * @param onGround Indicates whether the player is on the ground
     */
    public UpdateEvent(Action action, double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.action = action;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    /**
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * @return the z
	 */
	public double getZ() {
		return z;
	}

	/**
	 * @param z the z to set
	 */
	public void setZ(double z) {
		this.z = z;
	}

	/**
	 * @return the yaw
	 */
	public float getYaw() {
		return yaw;
	}

	/**
	 * @param yaw the yaw to set
	 */
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	/**
	 * @return the pitch
	 */
	public float getPitch() {
		return pitch;
	}

	/**
	 * @param pitch the pitch to set
	 */
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	/**
	 * @return the onGround
	 */
	public boolean isOnGround() {
		return onGround;
	}

	/**
	 * @param onGround the onGround to set
	 */
	public void setOnGround(boolean onGround) {
		this.onGround = onGround;
	}

	/**
	 * @return the action
	 */
	public Action getAction() {
		return action;
	}

	public enum Action {
        PRE,
        POST,
        BOTH
    }
}