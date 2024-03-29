package cc.unknown.utils.network;

import cc.unknown.utils.client.Cold;
import net.minecraft.network.Packet;

public class TimedPacket {

	private final Packet<?> packet;
    private final Cold time;

    public TimedPacket(Packet<?> packet) {
        this.packet = packet;
        this.time = new Cold();
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public Cold getCold() {
        return time;
    }

}
