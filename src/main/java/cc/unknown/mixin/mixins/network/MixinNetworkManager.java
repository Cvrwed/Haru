package cc.unknown.mixin.mixins.network;

import java.util.Queue;
import java.util.logging.Logger;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Queues;

import cc.unknown.Haru;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.event.impl.packet.PacketType;
import cc.unknown.mixin.interfaces.network.INetworkManager;
import cc.unknown.utils.Loona;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.NetworkManager.InboundHandlerTuplePacketListener;
import net.minecraft.network.Packet;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraft.util.IChatComponent;

@Mixin(NetworkManager.class)
public abstract class MixinNetworkManager implements INetworkManager, Loona {
	@Shadow
	private Channel channel;
	@Shadow
	private INetHandler packetListener;
	@Final
	@Shadow
	private final Queue<InboundHandlerTuplePacketListener> outboundPacketsQueue = Queues.newConcurrentLinkedQueue();
    @Shadow
    public abstract void dispatchPacket(final Packet<?> inPacket, final GenericFutureListener <? extends Future <? super Void >> [] futureListeners);
    @Shadow
    protected abstract void flushOutboundQueue();
	
    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void sendPacket(Packet<?> p_sendPacket_1_, CallbackInfo ci) {
        PacketEvent e = new PacketEvent(p_sendPacket_1_, PacketType.Send);

        Haru.instance.getEventBus().post(e);

        p_sendPacket_1_ = e.getPacket();
        if (e.isCancelled())
        	ci.cancel();
    }
    
    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void receivePacket(ChannelHandlerContext p_channelRead0_1_, Packet<INetHandler> p_channelRead0_2_, CallbackInfo ci) {
        PacketEvent e = new PacketEvent(p_channelRead0_2_, PacketType.Receive);
        Haru.instance.getEventBus().post(e);

        p_channelRead0_2_ = e.getPacket();
        if (e.isCancelled())
        	ci.cancel();
    }

    @Inject(method = ("closeChannel(Lnet/minecraft/util/IChatComponent;)V"),at = @At("RETURN"))
    private void onClose(IChatComponent chatComponent, CallbackInfo ci) {
    	Logger.getLogger("Closed");
    }

    @Override
	public void sendPacketNoEvent(Packet<?> packet) {
    	if (this.channel != null && this.channel.isOpen()) {
    		this.flushOutboundQueue();
    		this.dispatchPacket(packet, (GenericFutureListener<? extends Future<? super Void>>[])null);
    	} else {
    		this.outboundPacketsQueue.add(new InboundHandlerTuplePacketListener(packet, (GenericFutureListener<? extends Future<? super Void>>[])null));
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