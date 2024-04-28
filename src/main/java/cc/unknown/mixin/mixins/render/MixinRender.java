package cc.unknown.mixin.mixins.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

@Mixin(Render.class)
public abstract class MixinRender {

    @Shadow
    public <T extends Entity> void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
    }
}