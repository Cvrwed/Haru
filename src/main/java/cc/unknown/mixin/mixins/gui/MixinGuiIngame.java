package cc.unknown.mixin.mixins.gui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.Haru;
import cc.unknown.event.impl.render.RenderEvent;
import cc.unknown.event.impl.render.RenderEvent.RenderType;
import cc.unknown.module.impl.other.Tweaks;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mixin(GuiIngame.class)
public class MixinGuiIngame {

	@Inject(method = "renderTooltip", at = @At("RETURN"))
	private void renderTooltip(ScaledResolution sr, float partialTicks, CallbackInfo ci) {
		Haru.instance.getEventBus().post(new RenderEvent(RenderType.Render2D));
	}

	@Inject(method = "renderScoreboard", at = @At("HEAD"), cancellable = true)
	public void renderScoreboard(CallbackInfo ci) {
		Tweaks misc = (Tweaks) Haru.instance.getModuleManager().getModule(Tweaks.class);
		if (misc.noScoreboard.isToggled())
			ci.cancel();
	}

	@Inject(method = "renderPortal", at = @At("HEAD"), cancellable = true)
	public void renderPortal(CallbackInfo ci) {
		ci.cancel();
	}
	
	@Inject(method = "renderBossHealth", at = @At("HEAD"), cancellable = true)
	public void renderBossHealth(CallbackInfo ci) {
		ci.cancel();
	}
	
	@Inject(method = "renderPumpkinOverlay", at = @At("HEAD"), cancellable = true)
	public void renderPumpkinOverlay(CallbackInfo ci) {
		ci.cancel();
	}
	
	@Inject(method = "renderDemo", at = @At("HEAD"), cancellable = true)
	public void renderDemo(CallbackInfo ci) {
		ci.cancel();
	}
}