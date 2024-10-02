package cc.unknown.mixin.mixins.network;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.Haru;
import cc.unknown.event.impl.netty.PacketEvent;
import cc.unknown.utils.Loona;
import cc.unknown.utils.network.PacketUtil;
import cc.unknown.utils.player.PlayerUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

@Mixin(NetworkManager.class)
public abstract class MixinNetworkManager implements Loona {

	@Shadow
	private Channel channel;

	@Shadow
	public abstract boolean isChannelOpen();

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void receivePacket(ChannelHandlerContext p_channelRead0_1_, Packet<?> p_channelRead0_2_, CallbackInfo ci) {
		if (!PlayerUtil.inGame() || !this.isChannelOpen()) return;
		if (PacketUtil.skipReceiveEvent.contains(p_channelRead0_2_)) {
			PacketUtil.skipReceiveEvent.remove(p_channelRead0_2_);
			return;
		}

		final PacketEvent event = new PacketEvent(p_channelRead0_2_, EnumPacketDirection.SERVERBOUND);
		Haru.instance.getEventBus().post(event);

		if (event.isCancelled()) {
			ci.cancel();
		}
	}
	
    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void sendPacket(Packet<?> p_sendPacket_1_, CallbackInfo ci) {
    	if (!PlayerUtil.inGame() || !this.isChannelOpen()) return;
		if (PacketUtil.skipSendEvent.contains(p_sendPacket_1_)) {
			PacketUtil.skipSendEvent.remove(p_sendPacket_1_);
            return;
        }
		
		final PacketEvent event = new PacketEvent(p_sendPacket_1_, EnumPacketDirection.CLIENTBOUND);
		Haru.instance.getEventBus().post(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}