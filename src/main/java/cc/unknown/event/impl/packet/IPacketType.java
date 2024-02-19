package cc.unknown.event.impl.packet;

public interface IPacketType {
	PacketType getType();

    default boolean isReceive() {
        return getType() == PacketType.Receive;
    }

    default boolean isSend() {
        return getType() == PacketType.Send;
    }

}