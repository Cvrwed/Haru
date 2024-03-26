package cc.unknown.utils.network.packets.move;

import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;

public class PacketServerStorage {
    private final Packet<INetHandlerPlayClient> packet;
    private final long time;

    public PacketServerStorage(Packet<INetHandlerPlayClient> packet) {
        this.packet = packet;
        this.time = System.currentTimeMillis();
    }

    public Packet<INetHandlerPlayClient> getPacket() {
        return packet;
    }

    public long getTime() {
        return time;
    }
}