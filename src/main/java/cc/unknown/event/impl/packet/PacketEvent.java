package cc.unknown.event.impl.packet;

import cc.unknown.event.impl.api.CancellableEvent;
import net.minecraft.network.Packet;

public class PacketEvent extends CancellableEvent implements IPacketType {

    private Packet<?> packet;
    private final PacketType packetType;
    

    public PacketEvent(Packet<?> packet, PacketType packetType) {
        this.packet = packet;
        this.packetType = packetType;
    }

	public Packet<?> getPacket() {
        return (Packet<?>) this.packet;
    }

    public void setPacket(Packet<?> newPacket) {
        this.packet = newPacket;
    }

	@Override
	public PacketType getType() {
		return packetType;
	}

}
