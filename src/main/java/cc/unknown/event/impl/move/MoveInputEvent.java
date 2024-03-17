package cc.unknown.event.impl.move;

import cc.unknown.event.Event;

public class MoveInputEvent extends Event {

	private float forward;
	private float strafe;
	private boolean jump;
	private boolean sneak;
	private double sneakMultiplier;

    /**
     * Constructs a new MoveInputEvent object with the specified movement input values.
     *
     * @param forward           The forward movement input.
     * @param strafe            The strafe (sideways) movement input.
     * @param jump              Indicates if the player is jumping.
     * @param sneak             Indicates if the player is sneaking.
     * @param sneakMultiplier   The sneak movement multiplier.
     */
    public MoveInputEvent(float forward, float strafe, boolean jump, boolean sneak, double sneakMultiplier) {
        this.forward = forward;
        this.strafe = strafe;
        this.jump = jump;
        this.sneak = sneak;
        this.sneakMultiplier = sneakMultiplier;
    }

    /**
     * Retrieves the forward movement input.
     *
     * @return The forward movement input.
     */
    public float getForward() {
        return forward;
    }

    /**
     * Sets the forward movement input.
     *
     * @param forward The forward movement input to set.
     */
    public void setForward(float forward) {
        this.forward = forward;
    }

    /**
     * Retrieves the strafe (sideways) movement input.
     *
     * @return The strafe movement input.
     */
    public float getStrafe() {
        return strafe;
    }

    /**
     * Sets the strafe (sideways) movement input.
     *
     * @param strafe The strafe movement input to set.
     */
    public void setStrafe(float strafe) {
        this.strafe = strafe;
    }

    /**
     * Checks if the player is jumping.
     *
     * @return {@code true} if the player is jumping, {@code false} otherwise.
     */
    public boolean isJump() {
        return jump;
    }

    /**
     * Sets the jumping state of the player.
     *
     * @param jump The jumping state to set.
     */
    public void setJump(boolean jump) {
        this.jump = jump;
    }

    /**
     * Checks if the player is sneaking.
     *
     * @return {@code true} if the player is sneaking, {@code false} otherwise.
     */
    public boolean isSneak() {
        return sneak;
    }

    /**
     * Sets the sneaking state of the player.
     *
     * @param sneak The sneaking state to set.
     */
    public void setSneak(boolean sneak) {
        this.sneak = sneak;
    }

    /**
     * Retrieves the sneak movement multiplier.
     *
     * @return The sneak movement multiplier.
     */
    public double getSneakMultiplier() {
        return sneakMultiplier;
    }

    /**
     * Sets the sneak movement multiplier.
     *
     * @param sneakMultiplier The sneak movement multiplier to set.
     */
    public void setSneakMultiplier(double sneakMultiplier) {
        this.sneakMultiplier = sneakMultiplier;
    }
}
