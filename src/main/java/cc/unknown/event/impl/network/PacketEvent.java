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
     * @param action      The action of the event (PRE or POST).
     * @param type        The type of the event (SEND or RECEIVE).
     * @param packet      The packet associated with the event.
     * @param channel     The channel context associated with the event.
     * @param netHandler The network handler associated with the event.
     */
    private PacketEvent(Type type, Packet<?> packet, ChannelHandlerContext channel, INetHandler netHandler) {
        this.type = type;
        this.packet = packet;
        this.channel = channel;
        this.netHandler = netHandler;
    }

    /**
     * Creates a PacketEvent representing a pre-send event.
     *
     * @param packet      The packet being sent.
     * @param channel     The channel context for the packet.
     * @param netHandler The network handler for the packet.
     * @return The created PacketEvent.
     */
    public static PacketEvent Send(Packet<?> packet, ChannelHandlerContext channel, INetHandler netHandler) {
        return new PacketEvent(Type.SEND, packet, channel, netHandler);
    }

    /**
     * Creates a PacketEvent representing a pre-receive event.
     *
     * @param packet      The packet being received.
     * @param channel     The channel context for the packet.
     * @param netHandler The network handler for the packet.
     * @return The created PacketEvent.
     */
    public static PacketEvent Receive(Packet<?> packet, ChannelHandlerContext channel, INetHandler netHandler) {
        return new PacketEvent(Type.RECEIVE, packet, channel, netHandler);
    }

    /**
     * Gets the type of the event (SEND or RECEIVE).
     *
     * @return The type of the event.
     */
    public Type getType() {
        return type;
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
     * Enumerates the possible types of a packet event (SEND or RECEIVE).
     */
    public enum Type {
        SEND,
        RECEIVE
    }
}