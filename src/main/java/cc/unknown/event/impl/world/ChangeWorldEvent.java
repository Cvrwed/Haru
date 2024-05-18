package cc.unknown.event.impl.world;

import cc.unknown.event.Event;
import net.minecraft.client.multiplayer.WorldClient;

public class ChangeWorldEvent extends Event {
	
    private final WorldClient worldClient;

    /**
     * Constructs a new WorldEvent object with the specified world client.
     *
     * @param worldClient The world client associated with the event.
     */
    public ChangeWorldEvent(WorldClient worldClient) {
        this.worldClient = worldClient;
    }

    /**
     * Retrieves the world client associated with the event.
     *
     * @return The world client associated with the event.
     */
    public WorldClient getWorldClient() {
        return worldClient;
    }
}