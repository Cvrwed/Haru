package cc.unknown.event.impl.move;

import cc.unknown.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MoveInputEvent extends Event {
    private float forward;
    private float strafe;
    private boolean jump;
    private boolean sneak;
    private double sneakMultiplier;
}
