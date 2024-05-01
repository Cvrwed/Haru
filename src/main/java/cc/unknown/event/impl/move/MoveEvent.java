package cc.unknown.event.impl.move;

import cc.unknown.event.Event;

public class MoveEvent extends Event {
	
	private double motionX;
	private double motionY;
	private double motionZ;
	private boolean saveWalk;
	private boolean disableSneak;

    /**
     * Constructs a new SafeWalkEvent object with the specified motion values.
     *
     * @param x The motion in the X direction.
     * @param y The motion in the Y direction.
     * @param z The motion in the Z direction.
     */
    public MoveEvent(double x, double y, double z) {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
    }

    /**
     * Retrieves the motion in the X direction.
     *
     * @return The motion in the X direction.
     */
    public double getMotionX() {
        return motionX;
    }

    /**
     * Sets the motion in the X direction.
     *
     * @param motionX The new motion in the X direction.
     */
    public void setMotionX(double motionX) {
        this.motionX = motionX;
    }

    /**
     * Retrieves the motion in the Y direction.
     *
     * @return The motion in the Y direction.
     */
    public double getMotionY() {
        return motionY;
    }

    /**
     * Sets the motion in the Y direction.
     *
     * @param motionY The new motion in the Y direction.
     */
    public void setMotionY(double motionY) {
        this.motionY = motionY;
    }

    /**
     * Retrieves the motion in the Z direction.
     *
     * @return The motion in the Z direction.
     */
    public double getMotionZ() {
        return motionZ;
    }

    /**
     * Sets the motion in the Z direction.
     *
     * @param motionZ The new motion in the Z direction.
     */
    public void setMotionZ(double motionZ) {
        this.motionZ = motionZ;
    }

    /**
     * Checks if the safe walk feature is enabled.
     *
     * @return {@code true} if the safe walk feature is enabled, {@code false} otherwise.
     */
    public boolean isSaveWalk() {
        return saveWalk;
    }

    /**
     * Sets the state of the safe walk feature.
     *
     * @param saveWalk The new state of the safe walk feature.
     */
    public void setSaveWalk(boolean saveWalk) {
        this.saveWalk = saveWalk;
    }

    /**
     * Checks if sneaking is disabled.
     *
     * @return {@code true} if sneaking is disabled, {@code false} otherwise.
     */
    public boolean isDisableSneak() {
        return disableSneak;
    }

    /**
     * Sets the state of sneaking.
     *
     * @param disableSneak The new state of sneaking.
     */
    public void setDisableSneak(boolean disableSneak) {
        this.disableSneak = disableSneak;
    }
}