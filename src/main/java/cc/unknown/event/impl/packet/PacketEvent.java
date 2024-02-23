package cc.unknown.event.impl.packet;

import cc.unknown.event.impl.api.CancellableEvent;
import net.minecraft.network.Packet;

@SuppressWarnings("unchecked")
public class PacketEvent extends CancellableEvent implements IPacketType {

	private Packet<?> packet;
    private final PacketType type;

    public PacketEvent(Packet<?> packet, PacketType packetType) {
        this.packet = packet;
        this.type = packetType;
    }

	public <T extends Packet<?>> T getPacket() {
        return (T) this.packet;
    }

    public <T extends Packet<?>> void setPacket(T newPacket) {
        this.packet = newPacket;
    }

	@Override
	public PacketType getType() {
		return type;
	}
}
