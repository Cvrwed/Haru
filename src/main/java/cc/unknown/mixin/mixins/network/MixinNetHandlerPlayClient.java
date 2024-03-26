package cc.unknown.mixin.mixins.network;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.mixin.interfaces.network.INetHandlerPlayClient;
import cc.unknown.mixin.interfaces.network.INetworkManager;
import cc.unknown.ui.clickgui.raven.ClickGui;
import io.netty.buffer.Unpooled;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldSettings;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient implements INetHandlerPlayClient {
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
		if (Minecraft.getMinecraft().isIntegratedServerRunning())
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

	@Inject(method = "handleCloseWindow", at = @At("HEAD"), cancellable = true)
	private void handleCloseWindow(final S2EPacketCloseWindow packetIn, final CallbackInfo ci) {
		if (this.gameController.currentScreen instanceof ClickGui) {
			ci.cancel();
		}
	}

	@Override
	public void receiveQueueNoEvent(Packet<INetHandler> var1) {
		((INetworkManager) this.netManager).receivePacketNoEvent(var1);
	}
	
    @Redirect(method = "handleUpdateSign", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=Unable to locate sign at ", ordinal = 0)), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;addChatMessage(Lnet/minecraft/util/IChatComponent;)V", ordinal = 0))
    private void patcher$removeDebugMessage(EntityPlayerSP instance, IChatComponent component) { }
}
