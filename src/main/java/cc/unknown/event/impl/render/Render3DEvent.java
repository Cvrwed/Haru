package cc.unknown.event.impl.render;

import cc.unknown.event.Event;

public class Render3DEvent extends Event {
	
    private final float partialTicks;

    /**
     * Constructs a new Render3DEvent object with the specified partial ticks value.
     *
     * @param partialTicks The partial ticks value for smooth rendering.
     */
    public Render3DEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    /**
     * Retrieves the partial ticks value for smooth rendering.
     *
     * @return The partial ticks value for smooth rendering.
     */
    public float getPartialTicks() {
        return partialTicks;
    }
}

