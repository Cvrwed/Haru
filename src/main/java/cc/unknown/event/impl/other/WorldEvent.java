package cc.unknown.event.impl.other;

import cc.unknown.event.impl.Event;
import net.minecraft.client.multiplayer.WorldClient;

public class WorldEvent extends Event {
    private final WorldClient worldClient;

    public WorldEvent(WorldClient worldClient) {
        this.worldClient = worldClient;
    }

	public WorldClient getWorldClient() {
		return worldClient;
	}
}