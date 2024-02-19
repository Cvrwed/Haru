package cc.unknown.mixin.interfaces.network;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

public interface INetworkManager {
	void sendPacketNoEvent(Packet<?> var1);
	void receivePacketNoEvent(final Packet<INetHandler> packet);
}