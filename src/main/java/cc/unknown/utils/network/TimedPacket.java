package cc.unknown.utils.network;

import net.minecraft.network.Packet;

public class TimedPacket {

	private final Packet<?> packet;
    private final long time;

    public TimedPacket(Packet<?> packet, long time) {
        this.packet = packet;
        this.time = time;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public long getTime() {
        return time;
    }

}
