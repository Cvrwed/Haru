package cc.unknown.mixin.interfaces.network;

import net.minecraft.network.Packet;

public interface INetHandlerPlayClient {
	void receiveQueue(@SuppressWarnings("rawtypes") final Packet var1);
}
