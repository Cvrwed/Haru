package cc.unknown.mixin.mixins.network;

import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Queues;

import cc.unknown.Haru;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.mixin.interfaces.network.INetworkManager;
import cc.unknown.utils.Loona;
import cc.unknown.utils.network.PacketUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.NetworkManager.InboundHandlerTuplePacketListener;
import net.minecraft.network.Packet;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.IChatComponent;

@Mixin(NetworkManager.class)
public abstract class MixinNetworkManager implements INetworkManager, Loona {
	
	@Shadow
	private Channel channel;
	@Shadow
	public abstract boolean isChannelOpen();
	@Shadow
	private INetHandler packetListener;
	@Final
	@Shadow
	public final ReentrantReadWriteLock field_181680_j = new ReentrantReadWriteLock();
	@Final
	@Shadow
	private final Queue<InboundHandlerTuplePacketListener> outboundPacketsQueue = Queues.newConcurrentLinkedQueue();
	@Shadow
	public abstract void dispatchPacket(final Packet<?> inPacket, final GenericFutureListener<? extends Future<? super Void>>[] futureListeners);
	@Shadow
	protected abstract void flushOutboundQueue();

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void onSend(final Packet<INetHandlerPlayServer> p_sendPacket_1_, final CallbackInfo ci) {
    	final PacketEvent e = PacketEvent.Send(p_sendPacket_1_, null, null);

        Haru.instance.getEventBus().post(e);

        if (!PacketUtil.packet.contains(p_sendPacket_1_) && e.isCancelled()) {
            ci.cancel();
        }
    }
    
    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    public void onReceive(final ChannelHandlerContext channel, final Packet<INetHandlerPlayClient> p_channelRead0_2_, final CallbackInfo ci) {
        final PacketEvent e = PacketEvent.Receive(p_channelRead0_2_, channel, packetListener);
        Haru.instance.getEventBus().post(e);

        if (!PacketUtil.packet.contains(p_channelRead0_2_) && e.isCancelled()) {
            ci.cancel();
        }
    }
    
	@Inject(method = ("closeChannel(Lnet/minecraft/util/IChatComponent;)V"), at = @At("RETURN"))
	private void onClose(IChatComponent chatComponent, CallbackInfo ci) {
		Logger.getLogger("Closed");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sendPacketNoEvent(Packet<?> packetIn) {
		if (this.isChannelOpen()) {
			this.flushOutboundQueue();
            this.dispatchPacket(packetIn, (GenericFutureListener <? extends Future <? super Void >> [])null);
		} else {
			this.field_181680_j.writeLock().lock();

			try {
                this.outboundPacketsQueue.add(new NetworkManager.InboundHandlerTuplePacketListener(packetIn, (GenericFutureListener[])null));
			} finally {
				this.field_181680_j.writeLock().unlock();
			}
		}
	}

	@Override
	public void receivePacketNoEvent(final Packet<INetHandler> packet) {
		if (this.channel.isOpen()) {
			try {
				packet.processPacket(this.packetListener);
			} catch (final ThreadQuickExitException var4) {
			}
		}
	}
}