package cc.unknown.event.impl.move;

import cc.unknown.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostUpdateEvent extends Event {
    private double x, y, z;
    private float yaw, pitch;
    private boolean onGround;
}