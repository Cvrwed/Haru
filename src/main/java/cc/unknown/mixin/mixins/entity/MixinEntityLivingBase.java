package cc.unknown.mixin.mixins.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import cc.unknown.Haru;
import cc.unknown.event.impl.player.JumpEvent;
import cc.unknown.module.impl.player.Sprint;
import cc.unknown.module.impl.visuals.Fullbright;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends MixinEntity {

	@Shadow
	public float rotationYawHead;
	
    @Shadow
    public float swingProgress;
    
    @Shadow
    public float renderYawOffset;

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

	@Inject(method = "isPotionActive(Lnet/minecraft/potion/Potion;)Z", at = @At("HEAD"), cancellable = true)
	private void isPotionActive(Potion p_isPotionActive_1_,
			final CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		final Fullbright fb = (Fullbright) Haru.instance.getModuleManager().getModule(Fullbright.class);

		if ((p_isPotionActive_1_ == Potion.confusion || p_isPotionActive_1_ == Potion.blindness) && fb.isEnabled()
				&& fb.confusion.isToggled())
			callbackInfoReturnable.setReturnValue(false);
	}

	@Inject(method = "getLook", at = @At("HEAD"), cancellable = true)
	private void getLook(CallbackInfoReturnable<Vec3> callbackInfoReturnable) {
		if (((EntityLivingBase) (Object) this instanceof EntityPlayerSP))
			callbackInfoReturnable.setReturnValue(getVectorForRotation(rotationPitch, rotationYaw));
	}

	@Overwrite
	protected void jump() {
		if (((EntityLivingBase) (Object) this instanceof EntityPlayerSP)) {
			final Sprint sprint = (Sprint) Haru.instance.getModuleManager().getModule(Sprint.class);
			JumpEvent e = new JumpEvent(this.rotationYaw);
			Haru.instance.getEventBus().post(e);
			this.motionY = (double) getJumpUpwardsMotion();
			if (isPotionActive(Potion.jump)) {
				this.motionY += (double) ((float) (this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
			}

			if (this.isSprinting()) {
				if (sprint != null && sprint.isEnabled()) {
					if (mc.thePlayer.moveForward < 0.0F) {
						e.setYaw(e.getYaw() - 180.0F);
					}

					if (mc.thePlayer.moveStrafing != 0.0F && mc.thePlayer.moveForward == 0.0F) {
						if (mc.thePlayer.moveStrafing > 0.0F) {
							e.setYaw(e.getYaw() - 45.0F);
						} else if (mc.thePlayer.moveStrafing < 0.0F) {
							e.setYaw(e.getYaw() + 45.0F);
						}
					}
				}

				float f = e.getYaw() * (float) (Math.PI / 180.0);
				this.motionX -= (double) (MathHelper.sin(f) * 0.2F);
				this.motionZ += (double) (MathHelper.cos(f) * 0.2F);

			}
		} else {
			this.motionY = (double) this.getJumpUpwardsMotion();
			if (this.isPotionActive(Potion.jump)) {
				this.motionY += (double) ((float) (this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
			}

			if (this.isSprinting()) {
				float f = this.rotationYaw * (float) (Math.PI / 180.0);
				this.motionX -= (double) (MathHelper.sin(f) * 0.2F);
				this.motionZ += (double) (MathHelper.cos(f) * 0.2F);
			}
		}

		this.isAirBorne = true;
	}

}
