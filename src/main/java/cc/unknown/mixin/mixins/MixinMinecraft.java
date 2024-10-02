package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.Haru;
import cc.unknown.event.impl.other.GameEvent;
import cc.unknown.event.impl.other.MouseEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.event.impl.world.ChangeWorldEvent;
import cc.unknown.mixin.interfaces.IMinecraft;
import cc.unknown.utils.client.Cold;
import cc.unknown.utils.helpers.CPSHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.util.Session;

@Mixin(value = Minecraft.class, priority = 1001)
public abstract class MixinMinecraft implements IMinecraft {

	@Shadow
	public GuiScreen currentScreen;

	@Shadow
	@Mutable
	@Final
	private Session session;

	@Shadow
	public WorldClient theWorld;

	@Shadow
	public EntityRenderer entityRenderer;
	
	@Unique public Cold cold = new Cold();

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;checkGLError(Ljava/lang/String;)V", ordinal = 2, shift = At.Shift.AFTER))
	private void startGame(CallbackInfo callbackInfo) {
		Haru.instance.startClient();
	}
	
	@Inject(method = ("shutdown"), at = @At("HEAD"))
	public void shutdown(CallbackInfo ci) {
		Haru.instance.getEventBus().post(new GameEvent.ShutdownEvent());
	}

    @Inject(method = "runTick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;joinPlayerCounter:I", ordinal = 0))
	private void onTick(final CallbackInfo callbackInfo) {
		Haru.instance.getEventBus().post(new TickEvent());
	}

	@Inject(method = "runTick", at = @At("HEAD"))
	private void runTickPre(CallbackInfo ci) {
		Haru.instance.getEventBus().post(new TickEvent.Pre());
	}

	@Inject(method = "runTick", at = @At("RETURN"))
	public void runTickPost(final CallbackInfo ci) {
		Haru.instance.getEventBus().post(new TickEvent.Post());
	}

    @Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V", ordinal = 1))
	public void runGameLoop(CallbackInfo ci) {
		if (cold.finished(50 * 20 * 5)) {
			cold.reset();

			Haru.instance.getEventBus().post(new GameEvent());
		}
	}

	@Redirect(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Ljava/lang/System;gc()V"))
	public void removeSystemGC() {
	}

	@Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
	private void clearLoadedMaps(WorldClient worldClientIn, String loadingMessage, CallbackInfo ci) {
		Haru.instance.getEventBus().post(new ChangeWorldEvent(worldClientIn));
		if (worldClientIn != theWorld) {
			entityRenderer.getMapItemRenderer().clearLoadedMaps();
		}
	}

	@Override
	public void setSession(final Session session) {
		this.session = session;
	}


	@Inject(method = "clickMouse", at = @At("HEAD"))
	private void clickMouse(CallbackInfo callbackInfo) {
		Haru.instance.getEventBus().post(new MouseEvent(0));
		CPSHelper.registerClick(CPSHelper.MouseButton.LEFT);
	}

	@Inject(method = "rightClickMouse", at = @At("HEAD"))
	private void rightClickMouse(CallbackInfo callbackInfo) {
		Haru.instance.getEventBus().post(new MouseEvent(1));
		CPSHelper.registerClick(CPSHelper.MouseButton.RIGHT);
	}

	@Inject(method = "middleClickMouse", at = @At("HEAD"))
	private void middleClickMouse(CallbackInfo callbackInfo) {
		Haru.instance.getEventBus().post(new MouseEvent(2));
		CPSHelper.registerClick(CPSHelper.MouseButton.MIDDLE);
	}
}