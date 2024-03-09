package cc.unknown.event.impl.player;

import cc.unknown.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StrafeEvent extends Event {
    private float strafe, forward, friction, yaw;
}
