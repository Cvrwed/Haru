package cc.unknown.event.impl.player;

import cc.unknown.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LookEvent extends Event {

	private float pitch, prevPitch, yaw, prevYaw;

	public LookEvent(float pitch, float yaw) {
	    this.pitch = pitch;
	    this.yaw = yaw;
    }
}
