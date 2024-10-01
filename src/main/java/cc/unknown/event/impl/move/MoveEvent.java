package cc.unknown.event.impl.move;

import cc.unknown.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class MoveEvent extends Event {
	private final double motionX;
	private final double motionY;
	private final double motionZ;
	private boolean saveWalk;
	private boolean disableSneak;
}