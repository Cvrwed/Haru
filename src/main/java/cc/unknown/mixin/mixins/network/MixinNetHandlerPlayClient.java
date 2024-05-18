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
import cc.unknown.event.impl.network.DisconnectionEvent;
import cc.unknown.event.impl.network.KnockBackEvent;
import cc.unknown.mixin.interfaces.network.INetHandlerPlayClient;
import cc.unknown.mixin.interfaces.network.INetworkManager;
import cc.unknown.module.impl.combat.Velocity;
import cc.unknown.ui.clickgui.raven.HaruGui;
import cc.unknown.utils.Loona;
import io.netty.buffer.Unpooled;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldSettings;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient implements INetHandlerPlayClient, Loona {
	@Shadow
	@Final
	private NetworkManager netManager;
	@Shadow
	private Minecraft gameController;
	@Shadow
	private WorldClient clientWorldController;
	@Shadow
	public int currentServerMaxPlayers;

	@Inject(method = "handleJoinGame", at = @At("HEAD"), cancellable = true)
	private void handleJoinGame(S01PacketJoinGame packetIn, final CallbackInfo ci) {
		if (mc.isIntegratedServerRunning())
			return;

		PacketThreadUtil.checkThreadAndEnqueue(packetIn, (NetHandlerPlayClient) (Object) this, this.gameController);
		this.gameController.playerController = new PlayerControllerMP(this.gameController, (NetHandlerPlayClient) (Object) this);
		this.clientWorldController = new WorldClient((NetHandlerPlayClient) (Object) this, new WorldSettings(0L, packetIn.getGameType(), false, packetIn.isHardcoreMode(), packetIn.getWorldType()), packetIn.getDimension(), packetIn.getDifficulty(), this.gameController.mcProfiler);
		this.gameController.gameSettings.difficulty = packetIn.getDifficulty();
		this.gameController.loadWorld(this.clientWorldController);
		this.gameController.thePlayer.dimension = packetIn.getDimension();
		this.gameController.displayGuiScreen(null);
		this.gameController.thePlayer.setEntityId(packetIn.getEntityId());
		this.currentServerMaxPlayers = packetIn.getMaxPlayers();
		this.gameController.thePlayer.setReducedDebug(packetIn.isReducedDebugInfo());
		this.gameController.playerController.setGameType(packetIn.getGameType());
		this.gameController.gameSettings.sendSettingsToServer();
		this.netManager.sendPacket(new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString(ClientBrandRetriever.getClientModName())));
		ci.cancel();
	}

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
			KnockBackEvent knockBack = new KnockBackEvent((double) packetIn.getMotionX() / 8000.0D, (double) packetIn.getMotionY() / 8000.0D, (double) packetIn.getMotionZ() / 8000.0D);
			if (entity.getEntityId() == mc.thePlayer.getEntityId()) {
				Haru.instance.getEventBus().post(knockBack);
			}
			entity.setVelocity(knockBack.getX(), knockBack.getY(), knockBack.getZ());
		}
		
		ci.cancel();
	}
	
    @Inject(method = "handleEntityVelocity", at = @At("RETURN"))
    public void handleEntityVelocity2(final S12PacketEntityVelocity packetIn, final CallbackInfo ci) {
    	Velocity velo = (Velocity) Haru.instance.getModuleManager().getModule(Velocity.class);
    	
		if (velo.chance.getInput() != 100.0D) {
			if (Math.random() >= velo.chance.getInput() / 100.0D) {
				return;
			}
		}
    	
        if (packetIn.getEntityID() == mc.thePlayer.getEntityId() && velo.mode.is("Polar") && mc.thePlayer.onGround) {
            mc.thePlayer.jump();
        }
    }

	@Inject(method = "handleCloseWindow", at = @At("HEAD"), cancellable = true)
	private void handleCloseWindow(final S2EPacketCloseWindow packetIn, final CallbackInfo ci) {
		if (this.gameController.currentScreen instanceof HaruGui) {
			ci.cancel();
		}
	}

	@Override
	public void receiveQueue(@SuppressWarnings("rawtypes") Packet var1) {
		((INetworkManager) this.netManager).receivePacketNoEvent(var1);
	}

	@Redirect(method = "handleUpdateSign", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=Unable to locate sign at ", ordinal = 0)), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;addChatMessage(Lnet/minecraft/util/IChatComponent;)V", ordinal = 0))
	private void patcher$removeDebugMessage(EntityPlayerSP instance, IChatComponent component) {
	}
}
