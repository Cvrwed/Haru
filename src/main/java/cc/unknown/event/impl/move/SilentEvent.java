package cc.unknown.event.impl.move;

import cc.unknown.event.impl.Event;

public class SilentEvent extends Event {

    private final float initYaw;
    private final float initPitch;
    private float yaw, pitch;
    private float speed;
    private boolean doMovementFix = true;
    private boolean doJumpFix = true;
    
    public SilentEvent(float yaw, float pitch, float speed) {
        this.initYaw = yaw;
        this.initPitch = pitch;
        this.yaw = yaw;
        this.pitch = pitch;
        this.speed = speed;
    }

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public boolean isDoMovementFix() {
		return doMovementFix;
	}

	public void setDoMovementFix(boolean doMovementFix) {
		this.doMovementFix = doMovementFix;
	}

	public boolean isDoJumpFix() {
		return doJumpFix;
	}

	public void setDoJumpFix(boolean doJumpFix) {
		this.doJumpFix = doJumpFix;
	}

	public float getInitYaw() {
		return initYaw;
	}

	public float getInitPitch() {
		return initPitch;
	}
	
    public boolean hasBeenModified() {
        return initYaw != yaw || initPitch != pitch;
    }
}
