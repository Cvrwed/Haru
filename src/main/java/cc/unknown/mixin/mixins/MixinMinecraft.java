package cc.unknown.mixin.mixins;

import java.util.ConcurrentModificationException;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import cc.unknown.Haru;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.event.impl.other.GameEvent;
import cc.unknown.event.impl.other.MouseEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.event.impl.world.ChangeWorldEvent;
import cc.unknown.mixin.interfaces.IMinecraft;
import cc.unknown.module.impl.Module;
import cc.unknown.ui.clickgui.raven.HaruGui;
import cc.unknown.utils.Loona;
import cc.unknown.utils.helpers.CPSHelper;
import cc.unknown.utils.player.PlayerUtil;
import cc.unknown.utils.player.RotationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.stream.IStream;
import net.minecraft.entity.Entity;
import net.minecraft.util.Session;

@Mixin(Minecraft.class)
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

	@Inject(method = "getRenderViewEntity", at = @At("HEAD"))
	public void getRenderViewEntity(CallbackInfoReturnable<Entity> cir) {
		if (RotationUtils.targetRotation != null && Loona.mc.thePlayer != null) {
			final float yaw = RotationUtils.targetRotation.getYaw();
			Loona.mc.thePlayer.rotationYawHead = yaw;
			Loona.mc.thePlayer.renderYawOffset = yaw;
		}
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;checkGLError(Ljava/lang/String;)V", ordinal = 2, shift = At.Shift.AFTER))
	private void startGame(CallbackInfo callbackInfo) {
		Haru.instance.startClient();
	}

	@Inject(method = "runTick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;joinPlayerCounter:I", shift = At.Shift.BEFORE))
	private void onTick(final CallbackInfo callbackInfo) {
		Haru.instance.getEventBus().post(new TickEvent());
	}

	@Inject(method = "runTick", at = @At("HEAD"))
	private void runTickPre(CallbackInfo ci) {
		Haru.instance.getEventBus().post(new TickEvent.Pre());
	}

	@Inject(method = "runTick", at = @At("RETURN"))
	public void runTickPost(final CallbackInfo ci) {
		try {
			if (PlayerUtil.inGame()) {
				for (Module module : Haru.instance.getModuleManager().getModule()) {
					if (Loona.mc.currentScreen == null) {
						module.keybind();
					} else if (Loona.mc.currentScreen instanceof HaruGui) {
						Haru.instance.getEventBus().post(new ClickGuiEvent());
					}
				}
			}
		} catch (ConcurrentModificationException ignore) {
		}

		Haru.instance.getEventBus().post(new TickEvent.Post());
	}

	@Inject(method = ("shutdown"), at = @At("HEAD"))
	public void shutdown(CallbackInfo ci) {
		Haru.instance.getEventBus().post(new GameEvent.ShutdownEvent());
	}

	@Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V", ordinal = 1))
	public void runGameLoop(CallbackInfo ci) {
		Haru.instance.getEventBus().post(new GameEvent());
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

	@Redirect(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/stream/IStream;func_152935_j()V"))
	private void skipTwitchCode1(IStream instance) {
	}

	@Redirect(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/stream/IStream;func_152922_k()V"))
	private void skipTwitchCode2(IStream instance) {
	}

	@Override
	public void setSession(final Session session) {
		this.session = session;
	}

	@ModifyConstant(method = "getLimitFramerate", constant = @Constant(intValue = 30))
	public int getLimitFramerate(int constant) {
		return 900;
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