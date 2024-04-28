package cc.unknown.event.impl.render;

import cc.unknown.event.Event;
import net.minecraft.entity.Entity;

public class RenderLabelEvent extends Event {

    private final Entity target;
    private final double x;
    private final double y;
    private final double z;

    /**
     * Constructs a RenderLabelEvent with the specified target entity and coordinates.
     *
     * @param target The target entity associated with the event.
     * @param x      The x-coordinate associated with the event.
     * @param y      The y-coordinate associated with the event.
     * @param z      The z-coordinate associated with the event.
     */
    public RenderLabelEvent(Entity target, double x, double y, double z) {
        this.target = target;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Gets the target entity associated with the event.
     *
     * @return The target entity associated with the event.
     */
    public Entity getTarget() {
        return target;
    }

    /**
     * Gets the x-coordinate associated with the event.
     *
     * @return The x-coordinate associated with the event.
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the y-coordinate associated with the event.
     *
     * @return The y-coordinate associated with the event.
     */
    public double getY() {
        return y;
    }

    /**
     * Gets the z-coordinate associated with the event.
     *
     * @return The z-coordinate associated with the event.
     */
    public double getZ() {
        return z;
    }
}