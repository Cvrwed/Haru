package cc.unknown.event.impl.render;

import cc.unknown.event.Event;
import net.minecraft.entity.Entity;

public class RenderLabelEvent extends Event {

	private final Entity target;
	private final double x;
	private final double y;
	private final double z;
	
    /**
     * Constructs a new RenderLabelEvent object with the specified target entity and label position.
     *
     * @param target The target entity for the label.
     * @param x      The x-coordinate of the label position.
     * @param y      The y-coordinate of the label position.
     * @param z      The z-coordinate of the label position.
     */
    public RenderLabelEvent(Entity target, double x, double y, double z) {
        this.target = target;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Retrieves the target entity for the label.
     *
     * @return The target entity for the label.
     */
    public Entity getTarget() {
        return target;
    }

    /**
     * Retrieves the x-coordinate of the label position.
     *
     * @return The x-coordinate of the label position.
     */
    public double getX() {
        return x;
    }

    /**
     * Retrieves the y-coordinate of the label position.
     *
     * @return The y-coordinate of the label position.
     */
    public double getY() {
        return y;
    }

    /**
     * Retrieves the z-coordinate of the label position.
     *
     * @return The z-coordinate of the label position.
     */
    public double getZ() {
        return z;
    }
}