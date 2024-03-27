package cc.unknown.mixin.mixins.entity;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.Haru;
import cc.unknown.event.impl.move.PostUpdateEvent;
import cc.unknown.event.impl.move.PreUpdateEvent;
import cc.unknown.event.impl.move.UpdateEvent;
import cc.unknown.event.impl.network.ChatSendEvent;
import cc.unknown.module.impl.player.NoSlow;
import cc.unknown.module.impl.player.Sprint;
import cc.unknown.utils.network.PacketUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {
	
	public MixinEntityPlayerSP(World worldIn, NetHandlerPlayClient netHandler) {
		super(worldIn, netHandler.getGameProfile());
		this.sendQueue = netHandler;
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
	@Final
    public final NetHandlerPlayClient sendQueue;
	@Shadow
	public int horseJumpPowerCounter;
	@Shadow
	public float horseJumpPower;

	@Shadow
	public abstract void sendHorseJump();

	@Overwrite
	public void sendChatMessage(String message) {
		ChatSendEvent e = new ChatSendEvent(message);
		Haru.instance.getEventBus().post(e);
		if (e.isCancelled()) return;
		this.sendQueue.addToSendQueue(new C01PacketChatMessage(message));
	}

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

		if (this.sprintingTicksLeft > 0) {
			--this.sprintingTicksLeft;

			if (this.sprintingTicksLeft == 0) {
				this.setSprinting(false);
			}
		}

		if (this.sprintToggleTimer > 0) {
			--this.sprintToggleTimer;
		}

		this.prevTimeInPortal = this.timeInPortal;

		if (this.inPortal) {
			if (this.mc.currentScreen != null && !this.mc.currentScreen.doesGuiPauseGame()) {
				this.mc.displayGuiScreen(null);
			}

			if (this.timeInPortal == 0.0F) {
				this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("portal.trigger"),
						this.rand.nextFloat() * 0.4F + 0.8F));
			}

			this.timeInPortal += 0.0125F;

			if (this.timeInPortal >= 1.0F) {
				this.timeInPortal = 1.0F;
			}

			this.inPortal = false;
		} else if (this.isPotionActive(Potion.confusion)
				&& this.getActivePotionEffect(Potion.confusion).getDuration() > 60) {
			this.timeInPortal += 0.006666667F;

			if (this.timeInPortal > 1.0F) {
				this.timeInPortal = 1.0F;
			}
		} else {
			if (this.timeInPortal > 0.0F) {
				this.timeInPortal -= 0.05F;
			}

			if (this.timeInPortal < 0.0F) {
				this.timeInPortal = 0.0F;
			}
		}

		if (this.timeUntilPortal > 0) {
			--this.timeUntilPortal;
		}

		boolean flag = this.movementInput.jump;
		boolean flag1 = this.movementInput.sneak;
		float f = 0.8F;
		boolean flag2 = this.movementInput.moveForward >= f;
		this.movementInput.updatePlayerMoveState();

		final Sprint sprint = (Sprint) Haru.instance.getModuleManager().getModule(Sprint.class);
		NoSlow noSlow = (NoSlow) Haru.instance.getModuleManager().getModule(NoSlow.class);

		if (this.isUsingItem() && !this.isRiding()) {
			if (noSlow.isEnabled() && PlayerUtil.isMoving()) {
				switch (noSlow.mode.getMode()) {
				case "Grim": {
					if (this.isBlocking() || this.isSprinting() || this.isEating()) {
						int slot = this.inventory.currentItem;
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
					this.movementInput.moveForward *= noSlow.vForward.getInputToFloat();
					this.movementInput.moveStrafe *= noSlow.vStrafe.getInputToFloat();
				}
					break;
				}
			} else {
				this.movementInput.moveStrafe *= 0.2F;
				this.movementInput.moveForward *= 0.2F;
				this.sprintToggleTimer = 0;
			}
		}

		this.pushOutOfBlocks(this.posX - (double) this.width * 0.35D, this.getEntityBoundingBox().minY + 0.5D,
				this.posZ + (double) this.width * 0.35D);
		this.pushOutOfBlocks(this.posX - (double) this.width * 0.35D, this.getEntityBoundingBox().minY + 0.5D,
				this.posZ - (double) this.width * 0.35D);
		this.pushOutOfBlocks(this.posX + (double) this.width * 0.35D, this.getEntityBoundingBox().minY + 0.5D,
				this.posZ - (double) this.width * 0.35D);
		this.pushOutOfBlocks(this.posX + (double) this.width * 0.35D, this.getEntityBoundingBox().minY + 0.5D,
				this.posZ + (double) this.width * 0.35D);

		boolean flag3 = (float) this.getFoodStats().getFoodLevel() > 6.0F || this.capabilities.allowFlying;

		if (this.onGround && !flag1 && !flag2 && this.movementInput.moveForward >= f && !this.isSprinting() && flag3
				&& !this.isUsingItem() && !this.isPotionActive(Potion.blindness)) {
			if (this.sprintToggleTimer <= 0
					&& (!this.mc.gameSettings.keyBindSprint.isKeyDown() || !sprint.isEnabled())) {
				this.sprintToggleTimer = 7;
			} else {
				this.setSprinting(true);
			}
		}

		if (!this.isSprinting() && this.movementInput.moveForward >= f && flag3 && !this.isUsingItem()
				&& !this.isPotionActive(Potion.blindness)
				&& (this.mc.gameSettings.keyBindSprint.isKeyDown() || sprint.isEnabled())) {
			this.setSprinting(true);
		}

		if (this.isSprinting() && this.movementInput.moveForward < f || this.isCollidedHorizontally || !flag3) {
			this.setSprinting(false);
		}

		if (this.capabilities.allowFlying) {
			if (this.mc.playerController.isSpectatorMode()) {
				if (!this.capabilities.isFlying) {
					this.capabilities.isFlying = true;
					this.sendPlayerAbilities();
				}
			} else if (!flag && this.movementInput.jump) {
				if (this.flyToggleTimer == 0) {
					this.flyToggleTimer = 7;
				} else {
					this.capabilities.isFlying = !this.capabilities.isFlying;
					this.sendPlayerAbilities();
					this.flyToggleTimer = 0;
				}
			}
		}

		if (this.capabilities.isFlying && this.isCurrentViewEntity()) {
			if (this.movementInput.sneak) {
				this.motionY -= this.capabilities.getFlySpeed() * 3.0F;
			}

			if (this.movementInput.jump) {
				this.motionY += this.capabilities.getFlySpeed() * 3.0F;
			}
		}

		if (this.isRidingHorse()) {
			if (this.horseJumpPowerCounter < 0) {
				++this.horseJumpPowerCounter;

				if (this.horseJumpPowerCounter == 0) {
					this.horseJumpPower = 0.0F;
				}
			}

			if (flag && !this.movementInput.jump) {
				this.horseJumpPowerCounter = -10;
				this.sendHorseJump();
			} else if (!flag && this.movementInput.jump) {
				this.horseJumpPowerCounter = 0;
				this.horseJumpPower = 0.0F;
			} else if (flag) {
				++this.horseJumpPowerCounter;

				if (this.horseJumpPowerCounter < 10) {
					this.horseJumpPower = (float) this.horseJumpPowerCounter * 0.1F;
				} else {
					this.horseJumpPower = 0.8F + 2.0F / (float) (this.horseJumpPowerCounter - 9) * 0.1F;
				}
			}
		} else {
			this.horseJumpPower = 0.0F;
		}

		super.onLivingUpdate();

		if (this.onGround && this.capabilities.isFlying && !this.mc.playerController.isSpectatorMode()) {
			this.capabilities.isFlying = false;
			this.sendPlayerAbilities();
		}
	}

}