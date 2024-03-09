package cc.unknown.event.impl.render;

import cc.unknown.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Render3DEvent extends Event {
    private final float partialTicks;
}

