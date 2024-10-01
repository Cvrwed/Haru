package cc.unknown.event.impl.move;

import cc.unknown.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class HitSlowDownEvent extends Event {
    private double slowDown;
    private boolean sprint;
}
