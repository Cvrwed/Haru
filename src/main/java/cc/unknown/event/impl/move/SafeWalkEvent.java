package cc.unknown.event.impl.move;

import cc.unknown.event.Event;

public class SafeWalkEvent extends Event {
	
	private double motionX;
	private double motionY;
	private double motionZ;
	private boolean saveWalk;
	private boolean disableSneak;

	public SafeWalkEvent(double x, double y, double z) {
		this.motionX = x;
		this.motionY = y;
		this.motionZ = z;
	}

	public double getMotionX() {
		return motionX;
	}

	public void setMotionX(double motionX) {
		this.motionX = motionX;
	}

	public double getMotionY() {
		return motionY;
	}

	public void setMotionY(double motionY) {
		this.motionY = motionY;
	}

	public double getMotionZ() {
		return motionZ;
	}

	public void setMotionZ(double motionZ) {
		this.motionZ = motionZ;
	}

	public boolean isSaveWalk() {
		return saveWalk;
	}

	public void setSaveWalk(boolean saveWalk) {
		this.saveWalk = saveWalk;
	}

	public boolean isDisableSneak() {
		return disableSneak;
	}

	public void setDisableSneak(boolean disableSneak) {
		this.disableSneak = disableSneak;
	}
}