package cc.unknown.event.impl.packet;

public interface IPacketType {
    
    /**
     * Retrieves the type of the packet.
     *
     * @return The type of the packet.
     */
    PacketType getType();

    /**
     * Checks if the packet type is a receive type.
     *
     * @return {@code true} if the packet type is receive, {@code false} otherwise.
     */
    default boolean isReceive() {
        return getType() == PacketType.Receive;
    }

    /**
     * Checks if the packet type is a send type.
     *
     * @return {@code true} if the packet type is send, {@code false} otherwise.
     */
    default boolean isSend() {
        return getType() == PacketType.Send;
    }
}