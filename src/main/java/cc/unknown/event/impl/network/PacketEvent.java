package cc.unknown.event.impl.network;

import cc.unknown.event.Event;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;

public class PacketEvent extends Event {
    private final EnumPacketDirection direction;
    private Packet<?> packet;

    /**
     * Constructs a PacketEvent with the specified direction and packet.
     *
     * @param direction The direction of the packet (CLIENTBOUND or SERVERBOUND).
     * @param packet    The packet associated with the event.
     */
    public PacketEvent(EnumPacketDirection direction, Packet<?> packet) {
        this.direction = direction;
        this.packet = packet;
    }

    /**
     * Gets the packet associated with the event.
     *
     * @return The packet associated with the event.
     */
    public Packet<?> getPacket() {
        return packet;
    }

    /**
     * Sets the packet associated with the event.
     *
     * @param packet The packet to set.
     */
    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }

    /**
     * Checks if the direction of the packet is "CLIENTBOUND".
     *
     * @return true if the direction of the packet is "CLIENTBOUND", false otherwise.
     */
    public boolean isSend() {
        return direction == EnumPacketDirection.CLIENTBOUND;
    }

    /**
     * Checks if the direction of the packet is "SERVERBOUND".
     *
     * @return true if the direction of the packet is "SERVERBOUND", false otherwise.
     */
    public boolean isReceive() {
        return direction == EnumPacketDirection.SERVERBOUND;
    }
}