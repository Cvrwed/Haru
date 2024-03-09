package cc.unknown.event.impl.other;

import cc.unknown.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MouseEvent extends Event {
	private int button;
}