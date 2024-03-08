package cc.unknown.event.impl.packet;

import cc.unknown.event.impl.api.CancellableEvent;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

@SuppressWarnings("unchecked")
public class PacketEvent extends CancellableEvent implements IPacketType {

	private Packet<?> packet;
    private final PacketType type;
    private final INetHandler netHandler;

    public PacketEvent(Packet<?> packet, final INetHandler netHandler, final PacketType packetType) {
        this.packet = packet;
        this.netHandler = netHandler;
        this.type = packetType;
    }

	public <T extends Packet<?>> T getPacket() {
        return (T) this.packet;
    }

    public <T extends Packet<?>> void setPacket(T newPacket) {
        this.packet = newPacket;
    }
    
    public INetHandler getNetHandler() {
        return this.netHandler;
    }
    
	@Override
	public PacketType getType() {
		return type;
	}
}
