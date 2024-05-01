package cc.unknown.event.impl.other;

import cc.unknown.event.Event;

public class GameEvent extends Event {
	public static class ShutdownEvent extends GameEvent { }
	public static class StartEvent extends GameEvent { }
}
