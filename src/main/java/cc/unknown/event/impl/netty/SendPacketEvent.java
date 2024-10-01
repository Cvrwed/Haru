package cc.unknown.event.impl.netty;

import cc.unknown.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.Packet;

@Getter
@Setter
@AllArgsConstructor
public class SendPacketEvent extends Event {
	private Packet<?> packet;
}
