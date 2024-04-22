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
import cc.unknown.module.impl.settings.ClientRotations;
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
    protected float func_110146_f(float p_1101461, float p_1101462) {
        ClientRotations renderRotation = (ClientRotations) Haru.instance.getModuleManager().getModule(ClientRotations.class);
        if (renderRotation.isEnabled() && renderRotation.rotationMode.is("Smooth")) {
            float rotationYaw = this.rotationYaw;
            if ((EntityLivingBase) (Object) this instanceof EntityPlayerSP) {
                if (renderRotation.getPlayerYaw() != null) {
                    if (this.swingProgress > 0F) {
                        p_1101461 = renderRotation.getPlayerYaw();
                    }
                    rotationYaw = renderRotation.getPlayerYaw();
                }
            }
            float f = MathHelper.wrapAngleTo180_float(p_1101461 - this.renderYawOffset);
            this.renderYawOffset += f * 0.3F;
            float f1 = MathHelper.wrapAngleTo180_float(rotationYaw - this.renderYawOffset);
            boolean flag = f1 < -90.0F || f1 >= 90.0F;
            if (f1 < -75.0F) {
                f1 = -75.0F;
            }

            if (f1 >= 75.0F) {
                f1 = 75.0F;
            }

            this.renderYawOffset = rotationYaw - f1;
            if (f1 * f1 > 2500.0F) {
                this.renderYawOffset += f1 * 0.2F;
            }

            if (flag) {
                p_1101462 *= -1.0F;
            }

            return p_1101462;
        } else {
            float f = MathHelper.wrapAngleTo180_float(p_1101461 - this.renderYawOffset);
            this.renderYawOffset += f * 0.3F;
            float f1 = MathHelper.wrapAngleTo180_float(this.rotationYaw - this.renderYawOffset);
            boolean flag = f1 < -90.0F || f1 >= 90.0F;
            if (f1 < -75.0F) {
                f1 = -75.0F;
            }

            if (f1 >= 75.0F) {
                f1 = 75.0F;
            }

            this.renderYawOffset = this.rotationYaw - f1;
            if (f1 * f1 > 2500.0F) {
                this.renderYawOffset += f1 * 0.2F;
            }

            if (flag) {
                p_1101462 *= -1.0F;
            }

            return p_1101462;
        }
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
		if (((EntityLivingBase) (Object) this) instanceof EntityPlayerSP)
			callbackInfoReturnable.setReturnValue(getVectorForRotation(rotationPitch, rotationYaw));
	}
}
