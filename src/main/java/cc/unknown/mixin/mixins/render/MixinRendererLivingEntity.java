package cc.unknown.mixin.mixins.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.Haru;
import cc.unknown.event.impl.render.RenderEvent;
import cc.unknown.event.impl.render.RenderEvent.RenderType;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;

@Mixin(RendererLivingEntity.class)
public abstract class MixinRendererLivingEntity<T extends EntityLivingBase> extends MixinRender {
    
	@Inject(method = "renderName(Lnet/minecraft/entity/EntityLivingBase;DDD)V", at = @At("HEAD"), cancellable = true)
	private void onRenderLabel(EntityLivingBase entity, double x, double y, double z, CallbackInfo ci) {
		RenderEvent e = new RenderEvent(RenderType.RenderLabel, entity, x, y, z);
		Haru.instance.getEventBus().post(e);
		if (e.isCancelled())
			ci.cancel();
	}
}
