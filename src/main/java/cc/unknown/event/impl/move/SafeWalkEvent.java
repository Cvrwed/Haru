package cc.unknown.event.impl.move;

import cc.unknown.event.Event;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}