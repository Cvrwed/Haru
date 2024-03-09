package cc.unknown.event.impl.render;

import cc.unknown.event.Event;
import net.minecraft.entity.Entity;

public class RenderLabelEvent extends Event {
    private final Entity target;
    private final double x;
    private final double y;
    private final double z;
    
	public RenderLabelEvent(Entity target, double x, double y, double z) {
		this.target = target;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Entity getTarget() {
		return target;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}
}