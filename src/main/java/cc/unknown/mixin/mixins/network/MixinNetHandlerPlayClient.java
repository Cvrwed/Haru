package cc.unknown.mixin.mixins.network;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.Haru;
import cc.unknown.event.impl.netty.DisconnectionEvent;
import cc.unknown.event.impl.netty.PostVelocityEvent;
import cc.unknown.event.impl.netty.PreVelocityEvent;
import cc.unknown.ui.clickgui.raven.ClickGUI;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.util.IChatComponent;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {
	@Shadow
	@Final
	private NetworkManager netManager;
	@Shadow
	private Minecraft gameController;
	@Shadow
	private WorldClient clientWorldController;

	@Inject(method = "handleDisconnect", at = @At("HEAD"))
	private void handleDisconnect(final S40PacketDisconnect packetIn, final CallbackInfo ci) {
		if (packetIn instanceof S40PacketDisconnect) {
			Haru.instance.getEventBus().post(new DisconnectionEvent());
		}
	}

	@Inject(method = "handleEntityVelocity", at = @At("HEAD"), cancellable = true)
	private void handleEntityVelocity(S12PacketEntityVelocity packetIn, final CallbackInfo ci) {
		PacketThreadUtil.checkThreadAndEnqueue(packetIn, (NetHandlerPlayClient) (Object)this, this.gameController);
		Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityID());

		if (entity != null) {
			PreVelocityEvent knockBack = new PreVelocityEvent((double) packetIn.getMotionX() / 8000.0D, (double) packetIn.getMotionY() / 8000.0D, (double) packetIn.getMotionZ() / 8000.0D);
			if (entity.getEntityId() == this.gameController.thePlayer.getEntityId()) {
				Haru.instance.getEventBus().post(knockBack);
			}
			entity.setVelocity(knockBack.getX(), knockBack.getY(), knockBack.getZ());
		}
		
		ci.cancel();
	}
	
    @Inject(method = "handleEntityVelocity", at = @At("RETURN"))
    public void onPostHandleEntityVelocity(S12PacketEntityVelocity packet, CallbackInfo ci) {
        if (!PlayerUtil.inGame()) return;

        if (packet.getEntityID() == this.gameController.thePlayer.getEntityId()) {
            Haru.instance.getEventBus().post(new PostVelocityEvent());
        }
    }

	@Inject(method = "handleCloseWindow", at = @At("HEAD"), cancellable = true)
	private void handleCloseWindow(final S2EPacketCloseWindow packetIn, final CallbackInfo ci) {
		if (this.gameController.currentScreen instanceof ClickGUI) {
			ci.cancel();
		}
	}

	@Redirect(method = "handleUpdateSign", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=Unable to locate sign at ", ordinal = 0)), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;addChatMessage(Lnet/minecraft/util/IChatComponent;)V", ordinal = 0))
	private void patcher$removeDebugMessage(EntityPlayerSP instance, IChatComponent component) {
	}
}
