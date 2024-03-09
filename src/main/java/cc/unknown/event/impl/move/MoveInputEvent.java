package cc.unknown.event.impl.move;

import cc.unknown.event.Event;

public class MoveInputEvent extends Event {

    private float forward;
    private float strafe;
    private boolean jump;
    private boolean sneak;
    private double sneakMultiplier;
    
	public MoveInputEvent(float forward, float strafe, boolean jump, boolean sneak, double sneakMultiplier) {
		this.forward = forward;
		this.strafe = strafe;
		this.jump = jump;
		this.sneak = sneak;
		this.sneakMultiplier = sneakMultiplier;
	}

	public float getForward() {
		return forward;
	}

	public void setForward(float forward) {
		this.forward = forward;
	}

	public float getStrafe() {
		return strafe;
	}

	public void setStrafe(float strafe) {
		this.strafe = strafe;
	}

	public boolean isJump() {
		return jump;
	}

	public void setJump(boolean jump) {
		this.jump = jump;
	}

	public boolean isSneak() {
		return sneak;
	}

	public void setSneak(boolean sneak) {
		this.sneak = sneak;
	}

	public double getSneakMultiplier() {
		return sneakMultiplier;
	}

	public void setSneakMultiplier(double sneakMultiplier) {
		this.sneakMultiplier = sneakMultiplier;
	}

}
