package cc.unknown.utils.network;

import cc.unknown.utils.client.Cold;
import net.minecraft.network.Packet;

public class TimedPacket {

	private final Packet<?> packet;
    private final Cold time;
    private final long millis;

    public TimedPacket(Packet<?> packet) {
        this.packet = packet;
        this.time = new Cold();
        this.millis = System.currentTimeMillis();
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public Cold getCold() {
        return getTime();
    }

	public Cold getTime() {
		return time;
	}

	public long getMillis() {
		return millis;
	}

}
