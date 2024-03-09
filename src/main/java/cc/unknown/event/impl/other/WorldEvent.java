package cc.unknown.event.impl.other;

import cc.unknown.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.multiplayer.WorldClient;

@Getter
@Setter
@AllArgsConstructor
public class WorldEvent extends Event {
    private final WorldClient worldClient;
}