package cc.unknown.mixin.interfaces.network;

import net.minecraft.network.Packet;

public interface INetworkManager {
	void sendPacketNoEvent(Packet<?> var1);
	void receivePacketNoEvent(@SuppressWarnings("rawtypes") final Packet packet);
}