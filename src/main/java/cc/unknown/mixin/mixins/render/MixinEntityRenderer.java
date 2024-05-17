package cc.unknown.mixin.mixins.render;

import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.Haru;
import cc.unknown.event.impl.render.RenderEvent;
import cc.unknown.event.impl.render.RenderEvent.RenderType;
import cc.unknown.module.impl.other.Tweaks;
import cc.unknown.module.impl.visuals.FreeLook;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mixin(EntityRenderer.class)
@SideOnly(Side.CLIENT)
public abstract class MixinEntityRenderer implements IResourceManagerReloadListener {

	@Shadow
	private Minecraft mc;
	@Shadow
	public float thirdPersonDistanceTemp;

	@Shadow
	public float thirdPersonDistance;

	@Shadow
	private boolean cloudFog;

	@Inject(method = "renderStreamIndicator", at = @At("HEAD"), cancellable = true)
	private void cancelStreamIndicator(CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "renderWorldPass", slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/util/EnumWorldBlockLayer;TRANSLUCENT:Lnet/minecraft/util/EnumWorldBlockLayer;")), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;renderBlockLayer(Lnet/minecraft/util/EnumWorldBlockLayer;DILnet/minecraft/entity/Entity;)I", ordinal = 0))
	private void enablePolygonOffset(CallbackInfo ci) {
		GlStateManager.enablePolygonOffset();
		GlStateManager.doPolygonOffset(-0.325F, -0.325F);
	}

	@Inject(method = "renderWorldPass", slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/util/EnumWorldBlockLayer;TRANSLUCENT:Lnet/minecraft/util/EnumWorldBlockLayer;")), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;renderBlockLayer(Lnet/minecraft/util/EnumWorldBlockLayer;DILnet/minecraft/entity/Entity;)I", ordinal = 0, shift = At.Shift.AFTER))
	private void disablePolygonOffset(CallbackInfo ci) {
		GlStateManager.disablePolygonOffset();
	}

	@Inject(method = "renderWorldPass", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;renderHand:Z", shift = At.Shift.BEFORE))
	private void renderWorldPass(int pass, float partialTicks, long finishTimeNano, CallbackInfo callbackInfo) {
		Haru.instance.getEventBus().post(new RenderEvent(RenderType.Render3D, partialTicks));
	}

	@Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
	private void hurtCameraEffect(float partialTicks, CallbackInfo ci) {
		Tweaks tw = (Tweaks) Haru.instance.getModuleManager().getModule(Tweaks.class);
		if (tw.noHurtCam.isToggled())
			ci.cancel();
	}

	@Inject(method = "orientCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Vec3;distanceTo(Lnet/minecraft/util/Vec3;)D"), cancellable = true)
	private void cameraClip(float partialTicks, CallbackInfo callbackInfo) {
		final FreeLook freeLook = (FreeLook) Haru.instance.getModuleManager().getModule(FreeLook.class);

		if (!freeLook.isEnabled() && freeLook != null) {
			callbackInfo.cancel();

			Entity entity = this.mc.getRenderViewEntity();
			float f = entity.getEyeHeight();

			if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isPlayerSleeping()) {
				f = (float) ((double) f + 1D);
				GlStateManager.translate(0F, 0.3F, 0.0F);

				if (!this.mc.gameSettings.debugCamEnable) {
					BlockPos blockpos = new BlockPos(entity);
					IBlockState iblockstate = this.mc.theWorld.getBlockState(blockpos);
					ForgeHooksClient.orientBedCamera(this.mc.theWorld, blockpos, iblockstate, entity);

					GlStateManager.rotate(entity.prevRotationYaw
							+ (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F, 0.0F, -1.0F, 0.0F);
					GlStateManager.rotate(
							entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks,
							-1.0F, 0.0F, 0.0F);
				}
			} else if (this.mc.gameSettings.thirdPersonView > 0) {
				double d3 = this.thirdPersonDistanceTemp
						+ (this.thirdPersonDistance - this.thirdPersonDistanceTemp) * partialTicks;

				if (this.mc.gameSettings.debugCamEnable) {
					GlStateManager.translate(0.0F, 0.0F, (float) (-d3));
				} else {
					float f1 = entity.rotationYaw;
					float f2 = entity.rotationPitch;

					if (this.mc.gameSettings.thirdPersonView == 2)
						f2 += 180.0F;

					if (this.mc.gameSettings.thirdPersonView == 2)
						GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);

					GlStateManager.rotate(entity.rotationPitch - f2, 1.0F, 0.0F, 0.0F);
					GlStateManager.rotate(entity.rotationYaw - f1, 0.0F, 1.0F, 0.0F);
					GlStateManager.translate(0.0F, 0.0F, (float) (-d3));
					GlStateManager.rotate(f1 - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
					GlStateManager.rotate(f2 - entity.rotationPitch, 1.0F, 0.0F, 0.0F);
				}
			} else
				GlStateManager.translate(0.0F, 0.0F, -0.1F);

			if (!this.mc.gameSettings.debugCamEnable) {
				float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks
						+ 180.0F;
				float pitch = entity.prevRotationPitch
						+ (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
				float roll = 0.0F;
				if (entity instanceof EntityAnimal) {
					EntityAnimal entityanimal = (EntityAnimal) entity;
					yaw = entityanimal.prevRotationYawHead
							+ (entityanimal.rotationYawHead - entityanimal.prevRotationYawHead) * partialTicks + 180.0F;
				}

				Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, entity, partialTicks);
				EntityViewRenderEvent.CameraSetup event = new EntityViewRenderEvent.CameraSetup(
						(EntityRenderer) (Object) this, entity, block, partialTicks, yaw, pitch, roll);
				MinecraftForge.EVENT_BUS.post(event);
				GlStateManager.rotate(event.roll, 0.0F, 0.0F, 1.0F);
				GlStateManager.rotate(event.pitch, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(event.yaw, 0.0F, 1.0F, 0.0F);
			}

			GlStateManager.translate(0.0F, -f, 0.0F);
			double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double) partialTicks;
			double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double) partialTicks + (double) f;
			double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) partialTicks;
			this.cloudFog = this.mc.renderGlobal.hasCloudFog(d0, d1, d2, partialTicks);
		}
	}

	@Redirect(method = "updateCameraAndRender", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;inGameHasFocus:Z", opcode = 1))
	public boolean updateCameraAndRender(Minecraft minecraft) {
		final FreeLook freeLook = (FreeLook) Objects
				.requireNonNull(Haru.instance.getModuleManager().getModule(FreeLook.class));
		return freeLook.overrideMouse();
	}

	@Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;rotationYaw:F", opcode = 1))
	public float getRotationYaw(Entity entity) {
		return FreeLook.isPerspectiveToggled() ? FreeLook.getCameraYaw() : entity.rotationYaw;
	}

	@Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevRotationYaw:F", opcode = 1))
	public float getPrevRotationYaw(Entity entity) {
		return FreeLook.isPerspectiveToggled() ? FreeLook.getCameraYaw() : entity.prevRotationYaw;
	}

	@Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;rotationPitch:F", opcode = 1))
	public float getRotationPitch(Entity entity) {
		return FreeLook.isPerspectiveToggled() ? FreeLook.getCameraPitch() : entity.rotationPitch;
	}

	@Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevRotationPitch:F"))
	public float getPrevRotationPitch(Entity entity) {
		return FreeLook.isPerspectiveToggled() ? FreeLook.getCameraPitch() : entity.prevRotationPitch;
	}
}