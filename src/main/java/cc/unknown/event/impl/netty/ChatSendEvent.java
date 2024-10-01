package cc.unknown.event.impl.netty;

import cc.unknown.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatSendEvent extends Event {
	private String message;
}
