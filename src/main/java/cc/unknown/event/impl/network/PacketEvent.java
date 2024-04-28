package cc.unknown.event.impl.network;

import cc.unknown.event.Event;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

public class PacketEvent extends Event {
    private final Type type;
    private Packet<?> packet;
    private final ChannelHandlerContext channel;
    private final INetHandler netHandler;

    /**
     * Constructs a PacketEvent with the specified action, type, packet, channel, and network handler.
     *
     * @param type        The type of the event (SEND or RECEIVE).
     * @param packet      The packet associated with the event.
     * @param channel     The channel context associated with the event.
     * @param netHandler The network handler associated with the event.
     */
    public PacketEvent(Type type, Packet<?> packet, ChannelHandlerContext channel, INetHandler netHandler) {
        this.type = type;
        this.packet = packet;
        this.channel = channel;
        this.netHandler = netHandler;
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
     * Gets the channel context associated with the event.
     *
     * @return The channel context associated with the event.
     */
    public ChannelHandlerContext getChannel() {
        return channel;
    }

    /**
     * Gets the network handler associated with the event.
     *
     * @return The network handler associated with the event.
     */
    public INetHandler getNetHandler() {
        return netHandler;
    }
    
    /**
     * Checks if the type of the event is "Send".
     *
     * @return true if the type of the event is "Send", false otherwise.
     */
    public boolean isSend() {
        return type == Type.Send;
    }
    
    /**
     * Checks if the type of the event is "Receive".
     *
     * @return true if the type of the event is "Receive", false otherwise.
     */
    public boolean isReceive() {
        return type == Type.Receive;
    }

    /**
     * Enumerates the possible types of a packet event (SEND or RECEIVE).
     */
    public enum Type {
        Send,
        Receive
    }
}