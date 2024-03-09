package cc.unknown.event.impl.render;

import cc.unknown.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;

@Getter
@Setter
@AllArgsConstructor
public class RenderLabelEvent extends Event {
    private final Entity target;
    private final double x;
    private final double y;
    private final double z;
}