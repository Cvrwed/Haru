package cc.unknown.mixin.mixins.entity;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import cc.unknown.Haru;
import cc.unknown.event.impl.move.LivingEvent;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.event.impl.move.MotionEvent.MotionType;
import cc.unknown.event.impl.move.PreUpdateEvent;
import cc.unknown.event.impl.network.ChatSendEvent;
import cc.unknown.module.impl.player.NoSlow;
import cc.unknown.module.impl.player.Sprint;
import cc.unknown.utils.player.PlayerUtil;
import cc.unknown.utils.player.Rotation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {

	public MixinEntityPlayerSP() {
		super((World) null, (GameProfile) null);
	}

	@Shadow
	public int sprintingTicksLeft;

	@Shadow
	public float lastReportedYaw;

	@Override
	@Shadow
	public abstract void setSprinting(boolean p_setSprinting_1_);

	@Shadow
	public abstract boolean pushOutOfBlocks(double p_pushOutOfBlocks_1_, double p_pushOutOfBlocks_3_,
			double p_pushOutOfBlocks_5_);

	@Shadow
	protected int sprintToggleTimer;
	@Shadow
	public float prevTimeInPortal;
	@Shadow
	public float timeInPortal;
	@Shadow
	protected Minecraft mc;
	@Shadow
	public MovementInput movementInput;

	@Override
	@Shadow
	public abstract void sendPlayerAbilities();

	@Shadow
	protected abstract boolean isCurrentViewEntity();

	@Shadow
	public abstract boolean isRidingHorse();

	@Shadow
	private int horseJumpPowerCounter;
	@Shadow
	private float horseJumpPower;

	@Shadow
	protected abstract void sendHorseJump();

	@Shadow
	private boolean serverSprintState;
	@Shadow
	@Final
	public NetHandlerPlayClient sendQueue;

	@Override
	@Shadow
	public abstract boolean isSneaking();

	@Shadow
	private boolean serverSneakState;
	@Shadow
	private double lastReportedPosX;
	@Shadow
	private double lastReportedPosY;
	@Shadow
	private double lastReportedPosZ;
	@Shadow
	private float lastReportedPitch;
	@Shadow
	private int positionUpdateTicks;

	@Inject(method = "onUpdate()V", at = @At("HEAD"), cancellable = true)
	public void onUpdateCallback(CallbackInfo ci) {
		final PreUpdateEvent e = new PreUpdateEvent();
		Haru.instance.getEventBus().post(e);
		if (e.isCancelled()) {
			ci.cancel();
		}
	}

	@Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
	public void sendChatMessage(String message, CallbackInfo ci) {
		ChatSendEvent e = new ChatSendEvent(message);
		Haru.instance.getEventBus().post(e);
		if (e.isCancelled()) {
			ci.cancel();
		}
	}

	@Overwrite
	public void onUpdateWalkingPlayer() {
		MotionEvent pre = new MotionEvent(MotionType.Pre, posX, getEntityBoundingBox().minY, posZ, rotationYaw,
				rotationPitch, onGround);
		Haru.instance.getEventBus().post(pre);

		boolean flag = isSprinting();
		if (flag != serverSprintState) {
			if (flag) {
				sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.START_SPRINTING));
			} else {
				sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.STOP_SPRINTING));
			}

			serverSprintState = flag;
		}

		boolean flag1 = isSneaking();
		if (flag1 != serverSneakState) {
			if (flag1) {
				sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.START_SNEAKING));
			} else {
				sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.STOP_SNEAKING));
			}

			serverSneakState = flag1;
		}

		if (isCurrentViewEntity()) {
			float yaw = rotationYaw;
			float pitch = rotationPitch;

			if (Rotation.instance != null) {
				yaw = Rotation.instance.getYaw();
				pitch = Rotation.instance.getPitch();
			}

			double xDiff = posX - lastReportedPosX;
			double yDiff = getEntityBoundingBox().minY - lastReportedPosY;
			double zDiff = posZ - lastReportedPosZ;
			double yawDiff = yaw - lastReportedYaw;
			double pitchDiff = pitch - lastReportedPitch;
			boolean moved = xDiff * xDiff + yDiff * yDiff + zDiff * zDiff > 9.0E-4 || positionUpdateTicks >= 20;
			boolean rotated = yawDiff != 0 || pitchDiff != 0;

			if (ridingEntity == null) {
				if (moved && rotated) {
					sendQueue.addToSendQueue(
							new C06PacketPlayerPosLook(posX, getEntityBoundingBox().minY, posZ, yaw, pitch, onGround));
				} else if (moved) {
					sendQueue.addToSendQueue(
							new C04PacketPlayerPosition(posX, getEntityBoundingBox().minY, posZ, onGround));
				} else if (rotated) {
					sendQueue.addToSendQueue(new C05PacketPlayerLook(yaw, pitch, onGround));
				} else {
					sendQueue.addToSendQueue(new C03PacketPlayer(onGround));
				}
			} else {
				sendQueue.addToSendQueue(new C06PacketPlayerPosLook(motionX, -999, motionZ, yaw, pitch, onGround));
				moved = false;
			}

			++positionUpdateTicks;

			if (moved) {
				lastReportedPosX = posX;
				lastReportedPosY = getEntityBoundingBox().minY;
				lastReportedPosZ = posZ;
				positionUpdateTicks = 0;
			}

			if (rotated) {
				lastReportedYaw = yaw;
				lastReportedPitch = pitch;
			}
		}

		Haru.instance.getEventBus()
				.post(new MotionEvent(MotionType.Post, posX, posY, posZ, rotationYaw, rotationPitch, onGround));
	}

	@Overwrite
	public void onLivingUpdate() {
		Haru.instance.getEventBus().post(new LivingEvent());

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
		} else if (isPotionActive(Potion.confusion) && getActivePotionEffect(Potion.confusion).getDuration() > 60) {
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

		boolean flag = movementInput.jump;
		boolean flag1 = movementInput.sneak;
		float f = 0.8F;
		boolean flag2 = movementInput.moveForward >= f;
		movementInput.updatePlayerMoveState();

		final Sprint sprint = (Sprint) Haru.instance.getModuleManager().getModule(Sprint.class);
		final NoSlow noSlow = (NoSlow) Haru.instance.getModuleManager().getModule(NoSlow.class);

		if (isUsingItem() && !isRiding()) {	
			if (noSlow.isEnabled() && PlayerUtil.isMoving() && noSlow != null) {
				noSlow.slow();
			} else {
				movementInput.moveStrafe *= 0.2F;
				movementInput.moveForward *= 0.2F;
				sprintToggleTimer = 0;
			}
		}

		pushOutOfBlocks(posX - (double) width * 0.35D, getEntityBoundingBox().minY + 0.5D,
				posZ + (double) width * 0.35D);
		pushOutOfBlocks(posX - (double) width * 0.35D, getEntityBoundingBox().minY + 0.5D,
				posZ - (double) width * 0.35D);
		pushOutOfBlocks(posX + (double) width * 0.35D, getEntityBoundingBox().minY + 0.5D,
				posZ - (double) width * 0.35D);
		pushOutOfBlocks(posX + (double) width * 0.35D, getEntityBoundingBox().minY + 0.5D,
				posZ + (double) width * 0.35D);

		boolean flag3 = (float) getFoodStats().getFoodLevel() > 6.0F || capabilities.allowFlying;

		if (onGround && !flag1 && !flag2 && movementInput.moveForward >= f && !isSprinting() && flag3 && !isUsingItem()
				&& !isPotionActive(Potion.blindness)) {
			if (sprintToggleTimer <= 0 && (!mc.gameSettings.keyBindSprint.isKeyDown() || !sprint.isEnabled())) {
				sprintToggleTimer = 7;
			} else {
				setSprinting(true);
			}
		}

		if (!isSprinting() && movementInput.moveForward >= f && flag3 && !isUsingItem()
				&& !isPotionActive(Potion.blindness)
				&& (mc.gameSettings.keyBindSprint.isKeyDown() || sprint.isEnabled())) {
			setSprinting(true);
		}

		if (isSprinting() && movementInput.moveForward < f || isCollidedHorizontally || !flag3) {
			setSprinting(false);
		}

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