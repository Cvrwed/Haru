package cc.unknown.mixin.mixins.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import cc.unknown.Haru;
import cc.unknown.event.impl.player.JumpEvent;
import cc.unknown.module.impl.visuals.Fullbright;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends MixinEntity {

	@Shadow
	public float swingProgress;

	@Shadow
	public float renderYawOffset;

	@Shadow
	public float rotationYawHead;

	@Shadow
	protected abstract float getJumpUpwardsMotion();

	@Shadow
	public abstract PotionEffect getActivePotionEffect(Potion potionIn);

	@Shadow
	public abstract boolean isOnLadder();

	@Shadow
	public abstract IAttributeInstance getEntityAttribute(IAttribute attribute);
	
	@Shadow
	public abstract boolean isPotionActive(Potion potionIn);

	@Shadow
	protected abstract void updateArmSwingProgress();

	@Shadow
	public abstract void setLastAttacker(Entity entityIn);

	@Overwrite
	protected void jump() {
		JumpEvent e = new JumpEvent(rotationYaw);
		if (((EntityLivingBase) (Object) this) == Minecraft.getMinecraft().thePlayer)
			e.call();
		
		motionY = getJumpUpwardsMotion();
		
		if (isPotionActive(Potion.jump))
			motionY += ((getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
		
		if (isSprinting()) {
			float f = e.getYaw() * 0.017453292F;
			motionX -= (MathHelper.sin(f) * 0.2F);
			motionZ += (MathHelper.cos(f) * 0.2F);
		}
		
		isAirBorne = true;
	}

	@Inject(method = "isPotionActive(Lnet/minecraft/potion/Potion;)Z", at = @At("HEAD"), cancellable = true)
	private void isPotionActive(Potion p_isPotionActive_1_,
			final CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		final Fullbright fb = (Fullbright) Haru.instance.getModuleManager().getModule(Fullbright.class);

		if ((p_isPotionActive_1_ == Potion.confusion || p_isPotionActive_1_ == Potion.blindness) && fb.isEnabled()
				&& fb.confusion.isToggled())
			callbackInfoReturnable.setReturnValue(false);
	}
}
