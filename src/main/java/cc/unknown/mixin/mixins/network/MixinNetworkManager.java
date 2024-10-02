package cc.unknown.mixin.mixins.network;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
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

	@Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
	private void receivePacket(ChannelHandlerContext p_channelRead0_1_, Packet<?> p_channelRead0_2_, CallbackInfo ci) {
		if (!PlayerUtil.inGame() || !this.isChannelOpen()) return;
		if (PacketUtil.skipReceiveEvent.contains(p_channelRead0_2_)) {
			PacketUtil.skipReceiveEvent.remove(p_channelRead0_2_);
			return;
		}

		final PacketEvent e = new PacketEvent(p_channelRead0_2_, EnumPacketDirection.SERVERBOUND);
		Haru.instance.getEventBus().post(e);

		if (e.isCancelled()) {
			ci.cancel();
		}
	}

	@ModifyArg(method = "dispatchPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkManager;writeAndFlush(Lnet/minecraft/network/Packet;)Lio/netty/channel/ChannelFuture;"))
	public Packet<?> modifyPacket(Packet<?> inPacket) {
		if (!PlayerUtil.inGame() || !this.isChannelOpen())
			return inPacket;

		if (PacketUtil.skipSendEvent.contains(inPacket)) {
			PacketUtil.skipSendEvent.remove(inPacket);
			return inPacket;
		}

		final PacketEvent event = new PacketEvent(inPacket, EnumPacketDirection.CLIENTBOUND);
		Haru.instance.getEventBus().post(event);

		return event.getPacket();
	}

}