package cc.unknown.event.impl.netty;

import cc.unknown.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;

@Getter
@Setter
@AllArgsConstructor
public class PacketEvent extends Event {
	private Packet<?> packet;
	private EnumPacketDirection packetDirection;
	
	public boolean isSend() {
		return packetDirection == EnumPacketDirection.CLIENTBOUND;
	}
	
	public boolean isReceive() {
		return packetDirection == EnumPacketDirection.SERVERBOUND;
	}
}
