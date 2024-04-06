package cc.unknown.mixin.mixins.render;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.Haru;
import cc.unknown.module.impl.visuals.Fullbright;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mixin(ItemRenderer.class)
@SideOnly(Side.CLIENT)
public abstract class MixinItemRenderer {
	@Shadow
	private float equippedProgress;
	@Shadow
	private float prevEquippedProgress;
	@Shadow
	private ItemStack itemToRender;
	@Final
	@Shadow
	private Minecraft mc;

	@Shadow
	protected abstract void func_178109_a(AbstractClientPlayer var1);

	@Shadow
	protected abstract void func_178105_d(float var1);

	@Shadow
	protected abstract void func_178098_a(float var1, AbstractClientPlayer var2);

	@Shadow
	protected abstract void func_178110_a(EntityPlayerSP var1, float var2);

	@Shadow
	protected abstract void func_178104_a(AbstractClientPlayer var1, float var2);

	@Shadow
	protected abstract void transformFirstPersonItem(float equipProgress, float swingProgress);

	@Shadow
	protected abstract void func_178103_d();

	@Shadow
	protected abstract void func_178095_a(AbstractClientPlayer var1, float var2, float var3);

	@Shadow
	public abstract void renderItem(EntityLivingBase var1, ItemStack var2, ItemCameraTransforms.TransformType var3);

	@Shadow
	protected abstract void func_178101_a(float var1, float var2);

	@Shadow
	protected abstract void renderItemMap(AbstractClientPlayer var1, float var2, float var3, float var4);

	@Inject(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderItem;renderItemModelForEntity(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;)V"))
	public void renderItem(EntityLivingBase entity, ItemStack item, ItemCameraTransforms.TransformType transformType, CallbackInfo ci) {
		if (!(item.getItem() instanceof ItemSword)) return;
		if (!(entity instanceof EntityPlayer)) return;
		if (!(((EntityPlayer) entity).getItemInUseCount() > 0)) return;
		if (transformType != ItemCameraTransforms.TransformType.THIRD_PERSON) return;
		GlStateManager.rotate(-45.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-20.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(-60.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.translate(-0.04F, -0.04F, 0.0F);
	}

	@Inject(method = "func_178103_d", at = @At("HEAD"), cancellable = true)
	public void swordBlockTransformations(CallbackInfo ci) {
		GlStateManager.translate(-0.24F, 0.17F, 0.0F);
		GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(0.0F, 0.18F, 0.00F);
		ci.cancel();
	}
	
	@Inject(method = "renderWaterOverlayTexture", at = @At("HEAD"), cancellable = true)
	public void renderWaterOverlayTexture(float partialTicks, CallbackInfo ci) {
		ci.cancel();
	}

	/**
	 * Renders the item in the first-person the perspective
	 * 
	 * @param partialTicks The float value representing the perspective.
	 * @reason Renders the item in the firts-person perspective for an immersive
	 *         gameplay experience.
	 * @author Cvrwed
	 */

	@Overwrite
	public void renderItemInFirstPerson(float partialTicks) {
		float f = 1.0F - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
		EntityPlayerSP player = this.mc.thePlayer;
		float f1 = player.getSwingProgress(partialTicks);
		float f2 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
		float f3 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks;
		this.func_178101_a(f2, f3);
		this.func_178109_a(player);
		this.func_178110_a(player, partialTicks);
		GlStateManager.enableRescaleNormal();
		GlStateManager.pushMatrix();

		if (this.itemToRender != null) {
			if (this.itemToRender.getItem() instanceof ItemMap) {
				this.renderItemMap(player, f2, f, f1);
			} else if (player.getItemInUseCount() > 0) {
				EnumAction action = this.itemToRender.getItemUseAction();
				switch (action) {
				case NONE:
					this.transformFirstPersonItem(f, 0.0F);
					break;
				case EAT:
				case DRINK:
					this.func_178104_a(player, partialTicks);
					this.transformFirstPersonItem(f, f1);
					break;
				case BLOCK:
					this.transformFirstPersonItem(f, f1);
					this.func_178103_d();
					break;
				case BOW:
					this.transformFirstPersonItem(f, f1);
					this.func_178098_a(partialTicks, player);
				}
			} else {
				this.func_178105_d(f1);
				if (this.itemToRender.getItem() instanceof ItemFishingRod) {
					GlStateManager.translate(0.0F, 0.0F, -0.35F);
				}
				this.transformFirstPersonItem(f, f1);
			}

			this.renderItem(player, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
		} else if (!player.isInvisible()) {
			this.func_178095_a(player, f, f1);
		}

		GlStateManager.popMatrix();
		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
	}

	@Redirect(method = "renderFireInFirstPerson", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;color(FFFF)V"))
	private void renderFireInFirstPerson(float p_color_0_, float p_color_1_, float p_color_2_, float p_color_3_) {
		final Fullbright fb = (Fullbright) Haru.instance.getModuleManager().getModule(Fullbright.class);
		if (p_color_3_ != 1F && fb.isEnabled()) {
			GlStateManager.color(p_color_0_, p_color_1_, p_color_2_, fb.fire.getInputToFloat());
		} else {
			GlStateManager.color(p_color_0_, p_color_1_, p_color_2_, p_color_3_);
		}
	}
}