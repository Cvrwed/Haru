package cc.unknown.event.impl.render;

import cc.unknown.event.Event;
import net.minecraft.entity.Entity;

public class RenderEvent extends Event {
	
    private float partialTicks;
    private Entity target;
    private double x;
    private double y;
    private double z;
    private final RenderType renderType;

    /**
     * Constructs a RenderEvent with the specified render type, target entity, and coordinates.
     *
     * @param renderType The render type associated with the event.
     * @param target     The target entity associated with the event.
     * @param x          The x-coordinate associated with the event.
     * @param y          The y-coordinate associated with the event.
     * @param z          The z-coordinate associated with the event.
     */
    public RenderEvent(RenderType renderType, Entity target, double x, double y, double z) {
        this.renderType = renderType;
        this.target = target;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Constructs a RenderEvent with the specified render type and partial ticks.
     *
     * @param renderType    The render type associated with the event.
     * @param partialTicks  The partial ticks associated with the event.
     */
    public RenderEvent(RenderType renderType, float partialTicks) {
        this.renderType = renderType;
        this.partialTicks = partialTicks;
    }

    /**
     * Constructs a RenderEvent with the specified render type.
     *
     * @param renderType The render type associated with the event.
     */
    public RenderEvent(RenderType renderType) {
        this.renderType = renderType;
    }

    /**
     * Gets the partial ticks associated with the event.
     *
     * @return The partial ticks associated with the event.
     */
    public float getPartialTicks() {
        return partialTicks;
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

    /**
     * Gets the render type associated with the event.
     *
     * @return The render type associated with the event.
     */
    public RenderType getRenderType() {
        return renderType;
    }

    /**
     * Checks if the render type of the event is "Render3D".
     *
     * @return true if the render type is "Render3D", false otherwise.
     */
    public boolean is3D() {
        return renderType == RenderType.Render3D;
    }

    /**
     * Checks if the render type of the event is "Render2D".
     *
     * @return true if the render type is "Render2D", false otherwise.
     */
    public boolean is2D() {
        return renderType == RenderType.Render2D;
    }

    /**
     * Checks if the render type of the event is "RenderLabel".
     *
     * @return true if the render type is "RenderLabel", false otherwise.
     */
    public boolean isLabel() {
        return renderType == RenderType.RenderLabel;
    }

    /**
     * Enumerates the possible render types of a render event (Render3D, Render2D, RenderLabel).
     */
    public enum RenderType {
        Render3D,
        Render2D,
        RenderLabel
    }
 }
