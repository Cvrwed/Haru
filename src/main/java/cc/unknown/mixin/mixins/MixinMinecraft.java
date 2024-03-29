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

import cc.unknown.Haru;
import cc.unknown.event.impl.other.MouseEvent;
import cc.unknown.event.impl.other.ShutdownEvent;
import cc.unknown.event.impl.other.WorldEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.mixin.interfaces.IMinecraft;
import cc.unknown.module.Module;
import cc.unknown.utils.Loona;
import cc.unknown.utils.helpers.CPSHelper;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.stream.IStream;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.Session;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements IMinecraft {
	
	@Shadow public GuiScreen currentScreen;
	
	@Shadow @Mutable @Final private Session session;
	
	@Shadow public WorldClient theWorld;
	
	@Shadow public EntityRenderer entityRenderer;
	
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
				}
			}
		}
		} catch (ConcurrentModificationException ignore) { }
		
		Haru.instance.getEventBus().post(new TickEvent.Post());
	}

	@Inject(method = ("crashed"), at = @At("HEAD"))
	public void crashed(CrashReport crash, CallbackInfo callbackInfo) {
		Haru.instance.saveConfig();
	}

	@Inject(method = ("shutdown"), at = @At("HEAD"))
	public void shutdown(CallbackInfo callbackInfo) {
		Haru.instance.getEventBus().post(new ShutdownEvent());
		Haru.instance.saveConfig();
	}

	@Redirect(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Ljava/lang/System;gc()V"))
	public void removeSystemGC() {
	}

	@Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
	private void clearLoadedMaps(WorldClient worldClientIn, String loadingMessage, CallbackInfo ci) {
		Haru.instance.getEventBus().post(new WorldEvent(worldClientIn));
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
		return 200;
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