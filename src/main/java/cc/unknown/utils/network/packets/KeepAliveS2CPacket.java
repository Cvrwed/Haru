package cc.unknown.utils.network.packets;

import net.minecraft.network.play.client.C00PacketKeepAlive;

public class KeepAliveS2CPacket extends C00PacketKeepAlive {

	private final long time;

	public KeepAliveS2CPacket(int key, long time) {
		super(key);
		this.time = time;
	}

	public long getTime() {
		return this.time;
	}
}
