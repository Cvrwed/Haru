package cc.unknown.mixin.mixins.network;

import java.util.logging.Logger;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.Haru;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.network.PacketEvent.Type;
import cc.unknown.mixin.interfaces.network.INetworkManager;
import cc.unknown.utils.Loona;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraft.util.IChatComponent;

@Mixin(NetworkManager.class)
public abstract class MixinNetworkManager implements INetworkManager, Loona {
	
	@Shadow
	private Channel channel;
	@Shadow
	public abstract boolean isChannelOpen();
	@Shadow
	private INetHandler packetListener;

	@Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
	public void sendPacket(Packet<?> p_sendPacket_1_, CallbackInfo ci) {
		PacketEvent e = new PacketEvent(Type.SEND, p_sendPacket_1_, null, null);

		Haru.instance.getEventBus().post(e);

		p_sendPacket_1_ = e.getPacket();
		if (e.isCancelled())
			ci.cancel();
	}
    
    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    private void receivePacket(ChannelHandlerContext p_channelRead0_1_, Packet<INetHandler> p_channelRead0_2_, CallbackInfo ci) {
    	final PacketEvent e = new PacketEvent(Type.RECEIVE, p_channelRead0_2_, p_channelRead0_1_, packetListener);
		Haru.instance.getEventBus().post(e);

        if(e.isCancelled()) {
            ci.cancel();
        }
    }
    
	@Inject(method = ("closeChannel(Lnet/minecraft/util/IChatComponent;)V"), at = @At("RETURN"))
	private void onClose(IChatComponent chatComponent, CallbackInfo ci) {
		Logger.getLogger("Closed");
	}

	@Override
	public void sendPacketNoEvent(Packet<?> packetIn) {
		if (this.isChannelOpen()) {
			mc.getNetHandler().getNetworkManager().flushOutboundQueue();
			mc.getNetHandler().getNetworkManager().dispatchPacket(packetIn, (GenericFutureListener <? extends Future <? super Void >> [])null);
		} else {
			mc.getNetHandler().getNetworkManager().field_181680_j.writeLock().lock();

			try {
				mc.getNetHandler().getNetworkManager().outboundPacketsQueue.add(new NetworkManager.InboundHandlerTuplePacketListener(packetIn, (GenericFutureListener[])null));
			} finally {
				mc.getNetHandler().getNetworkManager().field_181680_j.writeLock().unlock();
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