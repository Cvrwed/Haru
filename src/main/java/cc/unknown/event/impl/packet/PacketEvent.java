package cc.unknown.event.impl.packet;

import cc.unknown.event.Event;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

@SuppressWarnings("unchecked")
public class PacketEvent extends Event implements IPacketType {

	private Packet<?> packet;
	private final PacketType type;
	private final INetHandler netHandler;

	/**
	 * Constructs a new PacketEvent object.
	 *
	 * @param packet     The data packet.
	 * @param netHandler The network handler.
	 * @param packetType The type of the packet.
	 */
	public PacketEvent(Packet<?> packet, final INetHandler netHandler, final PacketType packetType) {
		this.packet = packet;
		this.netHandler = netHandler;
		this.type = packetType;
	}

	/**
	 * Retrieves the data packet.
	 *
	 * @param <T> The specific type of packet.
	 * @return The data packet.
	 */
	public <T extends Packet<?>> T getPacket() {
		return (T) this.packet;
	}

	/**
	 * Sets a new data packet.
	 *
	 * @param newPacket The new data packet.
	 */
	public <T extends Packet<?>> void setPacket(T newPacket) {
		this.packet = newPacket;
	}

	/**
	 * Retrieves the network handler.
	 *
	 * @return The network handler.
	 */
	public INetHandler getNetHandler() {
		return this.netHandler;
	}

	/**
	 * Retrieves the type of the packet.
	 *
	 * @return The type of the packet.
	 */
	@Override
	public PacketType getType() {
		return type;
	}
}
