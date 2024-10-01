package cc.unknown.event.impl.other;

import cc.unknown.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MouseEvent extends Event {
	private int button;
}