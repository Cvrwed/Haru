package cc.unknown.event.impl.move;

import cc.unknown.event.Event;

public class SilentMoveEvent extends Event {
	private boolean silent;
	private float yaw;
	private boolean advanced;

	public SilentMoveEvent(final float yaw) {
		this.yaw = yaw;
	}

	public boolean isSilent() {
		return this.silent;
	}

	public void setSilent(final boolean silent) {
		this.silent = silent;
	}

	public float getYaw() {
		return this.yaw;
	}

	public void setYaw(final float yaw) {
		this.yaw = yaw;
	}

	public boolean isAdvanced() {
		return this.advanced;
	}

	public void setAdvanced(final boolean advanced) {
		this.advanced = advanced;
	}
}