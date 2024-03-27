package cc.unknown.utils.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

import cc.unknown.mixin.interfaces.network.INetHandlerPlayClient;
import cc.unknown.mixin.interfaces.network.INetworkManager;
import cc.unknown.utils.Loona;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

@SuppressWarnings("unchecked")
public class PacketUtil implements Loona {
    public static final ConcurrentLinkedQueue<TimedPacket> packets = new ConcurrentLinkedQueue<>();
    public static ArrayList<Packet<?>> packet = new ArrayList<Packet<?>>();;
	
    public static void sendPacketNoEvent(Packet<?> i) {
    	packet.add(i);
        ((INetworkManager)mc.getNetHandler().getNetworkManager()).sendPacketNoEvent(i);
     }
    
    public static void receivePacketNoEvent(final Packet<INetHandler> i) {
        ((INetHandlerPlayClient) mc.getNetHandler()).receiveQueueNoEvent(i);
    }
    
    public static void send(Packet<?> i) {
    	packet.add(i);
        mc.getNetHandler().addToSendQueue(i);
    }
    
	public static void send(Packet<?>[] i) {
        NetworkManager netManager = mc.getNetHandler() != null ? mc.getNetHandler().getNetworkManager() : null;
        if (netManager != null && netManager.isChannelOpen()) {
            netManager.flushOutboundQueue();
            for (Packet<?> packet : i) {
                netManager.dispatchPacket(packet, null);
            }
        } else if (netManager != null) {
            try {
                netManager.field_181680_j.writeLock().lock();
                for (Packet<?> packet : i) {
                    netManager.outboundPacketsQueue.add(new NetworkManager.InboundHandlerTuplePacketListener(packet, Arrays.asList((GenericFutureListener<? extends Future<? super Void>>) null).toArray(new GenericFutureListener[0])));
                }
            } finally {
                netManager.field_181680_j.writeLock().unlock();
            }
        }
    }
}
