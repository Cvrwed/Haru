package cc.unknown.mixin.mixins.render;

import java.awt.Color;
import java.nio.FloatBuffer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.Haru;
import cc.unknown.event.impl.render.RenderLabelEvent;
import cc.unknown.mixin.mixins.entity.MixinEntityLivingBase;
import cc.unknown.module.impl.settings.Targets;
import cc.unknown.module.impl.visuals.HitColor;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;

@Mixin(RendererLivingEntity.class)
public abstract class MixinRendererLivingEntity<T extends MixinEntityLivingBase> extends Render<EntityLivingBase> {

	protected MixinRendererLivingEntity(RenderManager renderManager) {
		super(renderManager);
	}

	@Redirect(method = "setBrightness", at = @At(value = "INVOKE", target = "Ljava/nio/FloatBuffer;put(F)Ljava/nio/FloatBuffer;", ordinal = 0))
	public FloatBuffer setRed(FloatBuffer instance, float v) {
		HitColor hit = (HitColor) Haru.instance.getModuleManager().getModule(HitColor.class);
		if (hit.isEnabled() && Targets.getTarget() != null) {
			instance.put((Color.getHSBColor((float)(hit.color.getInput() % 360) / 360.0f, 1.0f, 1.0f)).getRed());
		} else {
			instance.put(1f);
		}
		return instance;
	}

	@Redirect(method = "setBrightness", at = @At(value = "INVOKE", target = "Ljava/nio/FloatBuffer;put(F)Ljava/nio/FloatBuffer;", ordinal = 1))
	public FloatBuffer setGreen(FloatBuffer instance, float v) {
		HitColor hit = (HitColor) Haru.instance.getModuleManager().getModule(HitColor.class);
		if (hit.isEnabled() && Targets.getTarget() != null) {
			instance.put((Color.getHSBColor((float)(hit.color.getInput() % 360) / 360.0f, 1.0f, 1.0f)).getGreen());
		} else {
			instance.put(0f);
		}
		return instance;
	}

	@Redirect(method = "setBrightness", at = @At(value = "INVOKE", target = "Ljava/nio/FloatBuffer;put(F)Ljava/nio/FloatBuffer;", ordinal = 2))
	public FloatBuffer setBlue(FloatBuffer instance, float v) {
		HitColor hit = (HitColor) Haru.instance.getModuleManager().getModule(HitColor.class);
		if (hit.isEnabled() && Targets.getTarget() != null) {
			instance.put((Color.getHSBColor((float)(hit.color.getInput() % 360) / 360.0f, 1.0f, 1.0f)).getBlue());
		} else {
			instance.put(0f);
		}
		return instance;
	}
	
	@Redirect(method = "setBrightness", at = @At(value = "INVOKE", target = "Ljava/nio/FloatBuffer;put(F)Ljava/nio/FloatBuffer;", ordinal = 3))
	public FloatBuffer setAlpha(FloatBuffer instance, float v) {
		HitColor hit = (HitColor) Haru.instance.getModuleManager().getModule(HitColor.class);
		if (hit.isEnabled() && Targets.getTarget() != null) {
			instance.put((Color.getHSBColor((float)(hit.color.getInput() % 360) / 360.0f, 1.0f, 1.0f)).getAlpha());
		} else {
			instance.put(0.3f);
		}
		return instance;
	}

	@Inject(method = "renderName(Lnet/minecraft/entity/EntityLivingBase;DDD)V", at = @At("HEAD"), cancellable = true)
	private void onRenderLabel(EntityLivingBase entity, double x, double y, double z, CallbackInfo ci) {
		RenderLabelEvent e = new RenderLabelEvent(entity, x, y, z);
		Haru.instance.getEventBus().post(e);
		if (e.isCancelled())
			ci.cancel();
	}
}