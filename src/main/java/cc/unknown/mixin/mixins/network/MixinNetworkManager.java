package cc.unknown.mixin.mixins.network;

import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Queues;

import cc.unknown.Haru;
import cc.unknown.event.impl.netty.ReceivePacketEvent;
import cc.unknown.event.impl.netty.SendPacketEvent;
import cc.unknown.mixin.interfaces.network.INetworkManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.ThreadQuickExitException;

@Mixin(NetworkManager.class)
public abstract class MixinNetworkManager extends SimpleChannelInboundHandler<Packet> implements INetworkManager {
	
	@Shadow
	private Channel channel;
	@Shadow
	public abstract boolean isChannelOpen();
	@Shadow
	public abstract boolean flushOutboundQueue();
	@Shadow
	public abstract void setConnectionState(final EnumConnectionState newState);
	@Shadow
	private INetHandler packetListener;
	@Shadow
	@Final
    private final Queue<NetworkManager.InboundHandlerTuplePacketListener> outboundPacketsQueue = Queues.newConcurrentLinkedQueue();
	@Shadow
	@Final
    private static final Logger logger = LogManager.getLogger();
	@Shadow
	@Final
    public static final AttributeKey<EnumConnectionState> attrKeyConnectionState = AttributeKey.valueOf("protocol");
	@Shadow
	@Final
	private final ReentrantReadWriteLock field_181680_j = new ReentrantReadWriteLock();
    
    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    private void receivePacket(ChannelHandlerContext p_channelRead0_1_, @SuppressWarnings("rawtypes") Packet p_channelRead0_2_, CallbackInfo ci) {
    	final ReceivePacketEvent e = new ReceivePacketEvent(p_channelRead0_2_);
		Haru.instance.getEventBus().post(e);

        if(e.isCancelled()) {
            ci.cancel();
        }
    }
    
    @Overwrite
    private void dispatchPacket(final Packet inPacket, final GenericFutureListener<? extends Future<? super Void>>[] futureListeners) {

        final SendPacketEvent event = new SendPacketEvent(inPacket);
        Haru.instance.getEventBus().post(event);

        if (event.isCancelled()) {
            return;
        }

        final EnumConnectionState enumconnectionstate = EnumConnectionState.getFromPacket(event.getPacket());
        final EnumConnectionState enumconnectionstate1 = this.channel.attr(attrKeyConnectionState).get();

        if (enumconnectionstate1 != enumconnectionstate) {
            logger.debug("Disabled auto read");
            this.channel.config().setAutoRead(false);
        }

        if (this.channel.eventLoop().inEventLoop()) {
            if (enumconnectionstate != enumconnectionstate1) {
                this.setConnectionState(enumconnectionstate);
            }

            final ChannelFuture channelfuture = this.channel.writeAndFlush(event.getPacket());

            if (futureListeners != null) {
                channelfuture.addListeners(futureListeners);
            }

            channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        } else {
            this.channel.eventLoop().execute(new Runnable() {
                public void run() {
                    if (enumconnectionstate != enumconnectionstate1) {
                        setConnectionState(enumconnectionstate);
                    }

                    final ChannelFuture channelfuture1 = channel.writeAndFlush(event.getPacket());

                    if (futureListeners != null) {
                        channelfuture1.addListeners(futureListeners);
                    }

                    channelfuture1.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                }
            });
        }
    }

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
	public void receivePacketNoEvent(@SuppressWarnings("rawtypes") final Packet packet) {
		if (this.channel.isOpen()) {
			try {
				packet.processPacket(this.packetListener);
			} catch (final ThreadQuickExitException var4) {
			}
		}
	}
}