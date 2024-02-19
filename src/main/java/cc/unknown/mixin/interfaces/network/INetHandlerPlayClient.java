package cc.unknown.mixin.interfaces.network;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

public interface INetHandlerPlayClient {
	void receiveQueueNoEvent(final Packet<INetHandler> var1);
}
