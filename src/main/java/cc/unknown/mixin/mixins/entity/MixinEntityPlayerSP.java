package cc.unknown.mixin.mixins.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import cc.unknown.Haru;
import cc.unknown.event.impl.move.PostUpdateEvent;
import cc.unknown.event.impl.move.PreUpdateEvent;
import cc.unknown.event.impl.move.SilentMoveEvent;
import cc.unknown.event.impl.move.UpdateEvent;
import cc.unknown.module.impl.player.NoSlow;
import cc.unknown.module.impl.player.Sprint;
import cc.unknown.utils.helpers.MathHelper;
import cc.unknown.utils.network.PacketUtil;
import cc.unknown.utils.player.PlayerUtil;
import cc.unknown.utils.player.RotationUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {

	public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
		super(worldIn, playerProfile);
	}

	@Unique
	private double cachedX;
	@Unique
	private double cachedY;
	@Unique
	private double cachedZ;
	@Unique
	private boolean cachedOnGround;
	@Unique
	private float cachedRotationPitch;
	@Unique
	private float cachedRotationYaw;
	@Shadow
	public int sprintingTicksLeft;
	@Shadow
	public float lastReportedYaw;

	@Shadow
	public abstract void setSprinting(boolean p_setSprinting_1_);

	@Shadow
	public int sprintToggleTimer;
	@Shadow
	public float prevTimeInPortal;
	@Shadow
	public float timeInPortal;
	@Shadow
	public Minecraft mc;
	@Shadow
	public MovementInput movementInput;

	@Shadow
	public abstract boolean pushOutOfBlocks(double p_pushOutOfBlocks_1_, double p_pushOutOfBlocks_3_,
			double p_pushOutOfBlocks_5_);

	@Shadow
	public abstract void sendPlayerAbilities();

	@Shadow
	public abstract boolean isCurrentViewEntity();

	@Shadow
	public abstract boolean isRidingHorse();

	@Shadow
	public int horseJumpPowerCounter;
	@Shadow
	public float horseJumpPower;

	@Shadow
	public abstract void sendHorseJump();

	@Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
	private void onUpdateWalkingPlayerPre(CallbackInfo ci) {
		cachedX = posX;
		cachedY = posY;
		cachedZ = posZ;

		cachedOnGround = onGround;

		cachedRotationYaw = rotationYaw;
		cachedRotationPitch = rotationPitch;

		PreUpdateEvent e = new PreUpdateEvent(posX, posY, posZ, rotationYaw, rotationPitch, onGround);
		Haru.instance.getEventBus().post(e);
		if (e.isCancelled()) {
			ci.cancel();
			return;
		}

		posX = e.getX();
		posY = e.getY();
		posZ = e.getZ();

		onGround = e.isOnGround();

		rotationYaw = e.getYaw();
		rotationPitch = e.getPitch();
	}

	@Inject(method = "onUpdateWalkingPlayer", at = @At("RETURN"))
	private void onUpdateWalkingPlayerPost(CallbackInfo ci) {
		posX = cachedX;
		posY = cachedY;
		posZ = cachedZ;

		onGround = cachedOnGround;

		rotationYaw = cachedRotationYaw;
		rotationPitch = cachedRotationPitch;

		Haru.instance.getEventBus().post(new PostUpdateEvent(posX, posY, posZ, rotationYaw, rotationPitch, onGround));
	}

	@Overwrite
	public void onLivingUpdate() {
		Haru.instance.getEventBus().post(new UpdateEvent());

		if (sprintingTicksLeft > 0) {
			--sprintingTicksLeft;

			if (sprintingTicksLeft == 0) {
				setSprinting(false);
			}
		}

		if (sprintToggleTimer > 0) {
			--sprintToggleTimer;
		}

		prevTimeInPortal = timeInPortal;

		if (inPortal) {
			if (mc.currentScreen != null && !mc.currentScreen.doesGuiPauseGame()) {
				mc.displayGuiScreen(null);
			}

			if (timeInPortal == 0.0F) {
				mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("portal.trigger"),
						rand.nextFloat() * 0.4F + 0.8F));
			}

			timeInPortal += 0.0125F;

			if (timeInPortal >= 1.0F) {
				timeInPortal = 1.0F;
			}

			inPortal = false;
		} else if (isPotionActive(Potion.confusion)
				&& getActivePotionEffect(Potion.confusion).getDuration() > 60) {
			timeInPortal += 0.006666667F;

			if (timeInPortal > 1.0F) {
				timeInPortal = 1.0F;
			}
		} else {
			if (timeInPortal > 0.0F) {
				timeInPortal -= 0.05F;
			}

			if (timeInPortal < 0.0F) {
				timeInPortal = 0.0F;
			}
		}

		if (timeUntilPortal > 0) {
			--timeUntilPortal;
		}

		float f = 0.8F;
		boolean flag = movementInput.jump;
		boolean flag1 = movementInput.sneak;
		boolean flag2 = movementInput.moveForward >= f;
        final float forward = movementInput.moveForward;
        final float strafe = movementInput.moveStrafe;
		movementInput.updatePlayerMoveState();
		
        final SilentMoveEvent e = new SilentMoveEvent(Haru.instance.getSilentHelper().realYaw);
        Haru.instance.getEventBus().post(e);
        
        if (e.isSilent()) {
            final float[] floats = RotationUtil.instance.augustusStrafe(movementInput.moveStrafe, movementInput.moveForward, e.getYaw(), e.isAdvanced());
            final float diffForward = forward - floats[1];
            final float diffStrafe = strafe - floats[0];
            if (movementInput.sneak) {
                movementInput.moveStrafe = MathHelper.clamp_float(floats[0], -0.3f, 0.3f);
                movementInput.moveForward = MathHelper.clamp_float(floats[1], -0.3f, 0.3f);
            }
            else {
                if (diffForward >= 2.0f) {
                    floats[1] = 0.0f;
                }
                if (diffForward <= -2.0f) {
                    floats[1] = 0.0f;
                }
                if (diffStrafe >= 2.0f) {
                    floats[0] = 0.0f;
                }
                if (diffStrafe <= -2.0f) {
                    floats[0] = 0.0f;
                }
                movementInput.moveStrafe = MathHelper.clamp_float(floats[0], -1.0f, 1.0f);
                movementInput.moveForward = MathHelper.clamp_float(floats[1], -1.0f, 1.0f);
            }
        }

		final Sprint sprint = (Sprint) Haru.instance.getModuleManager().getModule(Sprint.class);
		NoSlow noSlow = (NoSlow) Haru.instance.getModuleManager().getModule(NoSlow.class);

		if (isUsingItem() && !isRiding()) {
			if (noSlow.isEnabled() && PlayerUtil.isMoving()) {
				switch (noSlow.mode.getMode()) {
				case "Grim": {
					if (isBlocking() || isSprinting() || isEating()) {
						int slot = inventory.currentItem;
						PacketUtil.send(new C09PacketHeldItemChange(slot < 8 ? slot + 1 : 0));
						PacketUtil.send(new C09PacketHeldItemChange(slot));
					}
				}
					break;
				case "C16": {
					PacketUtil.send(
							new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
				}
					break;
				case "Vanilla": {
					movementInput.moveForward *= noSlow.vForward.getInputToFloat();
					movementInput.moveStrafe *= noSlow.vStrafe.getInputToFloat();
				}
					break;
				}
			} else {
				movementInput.moveStrafe *= 0.2F;
				movementInput.moveForward *= 0.2F;
				sprintToggleTimer = 0;
			}
		}

		pushOutOfBlocks(posX - (double) width * 0.35D, getEntityBoundingBox().minY + 0.5D, posZ + (double) width * 0.35D);
		pushOutOfBlocks(posX - (double) width * 0.35D, getEntityBoundingBox().minY + 0.5D, posZ - (double) width * 0.35D);
		pushOutOfBlocks(posX + (double) width * 0.35D, getEntityBoundingBox().minY + 0.5D, posZ - (double) width * 0.35D);
		pushOutOfBlocks(posX + (double) width * 0.35D, getEntityBoundingBox().minY + 0.5D, posZ + (double) width * 0.35D);

		boolean flag3 = (float) getFoodStats().getFoodLevel() > 6.0F || capabilities.allowFlying;

		if (onGround && !flag1 && !flag2 && movementInput.moveForward >= f && !isSprinting() && flag3 && !isUsingItem() && !isPotionActive(Potion.blindness)) {
			if (sprintToggleTimer <= 0 && (!mc.gameSettings.keyBindSprint.isKeyDown() || !sprint.isEnabled())) {
				sprintToggleTimer = 7;
			} else {
				setSprinting(true);
			}
		}

		if (!isSprinting() && movementInput.moveForward >= f && flag3 && !isUsingItem() && !isPotionActive(Potion.blindness) && (mc.gameSettings.keyBindSprint.isKeyDown() || sprint.isEnabled())) {
			setSprinting(true);
		}

		if (isSprinting() && movementInput.moveForward < f || isCollidedHorizontally || !flag3) {
			setSprinting(false);
		}
		
		/*
		 * if (scaffold.isEnabled() && !scaffold.sprint.isToggled())
		 * setSprinting(false);
		 */

		if (capabilities.allowFlying) {
			if (mc.playerController.isSpectatorMode()) {
				if (!capabilities.isFlying) {
					capabilities.isFlying = true;
					sendPlayerAbilities();
				}
			} else if (!flag && movementInput.jump) {
				if (flyToggleTimer == 0) {
					flyToggleTimer = 7;
				} else {
					capabilities.isFlying = !capabilities.isFlying;
					sendPlayerAbilities();
					flyToggleTimer = 0;
				}
			}
		}

		if (capabilities.isFlying && isCurrentViewEntity()) {
			if (movementInput.sneak) {
				motionY -= capabilities.getFlySpeed() * 3.0F;
			}

			if (movementInput.jump) {
				motionY += capabilities.getFlySpeed() * 3.0F;
			}
		}

		if (isRidingHorse()) {
			if (horseJumpPowerCounter < 0) {
				++horseJumpPowerCounter;

				if (horseJumpPowerCounter == 0) {
					horseJumpPower = 0.0F;
				}
			}

			if (flag && !movementInput.jump) {
				horseJumpPowerCounter = -10;
				sendHorseJump();
			} else if (!flag && movementInput.jump) {
				horseJumpPowerCounter = 0;
				horseJumpPower = 0.0F;
			} else if (flag) {
				++horseJumpPowerCounter;

				if (horseJumpPowerCounter < 10) {
					horseJumpPower = (float) horseJumpPowerCounter * 0.1F;
				} else {
					horseJumpPower = 0.8F + 2.0F / (float) (horseJumpPowerCounter - 9) * 0.1F;
				}
			}
		} else {
			horseJumpPower = 0.0F;
		}

		super.onLivingUpdate();

		if (onGround && capabilities.isFlying && !mc.playerController.isSpectatorMode()) {
			capabilities.isFlying = false;
			sendPlayerAbilities();
		}
	}
}