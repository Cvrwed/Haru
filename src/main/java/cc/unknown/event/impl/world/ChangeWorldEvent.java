package cc.unknown.event.impl.world;

import cc.unknown.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.multiplayer.WorldClient;

@RequiredArgsConstructor
@Getter
public class ChangeWorldEvent extends Event {
    private final WorldClient worldClient;
}