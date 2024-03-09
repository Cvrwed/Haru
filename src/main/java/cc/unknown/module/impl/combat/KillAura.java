package cc.unknown.module.impl.combat;

import org.lwjgl.input.Mouse;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MoveInputEvent;
import cc.unknown.event.impl.move.PreUpdateEvent;
import cc.unknown.event.impl.player.JumpEvent;
import cc.unknown.event.impl.player.LookEvent;
import cc.unknown.event.impl.player.StrafeEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.utils.client.AdvancedTimer;
import cc.unknown.utils.helpers.MathHelper;
import cc.unknown.utils.misc.ClickUtil;
import cc.unknown.utils.player.CombatUtil;
import cc.unknown.utils.player.PlayerUtil;
import cc.unknown.utils.player.RotationUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class KillAura extends Module {

	private EntityPlayer target = null;

	private DoubleSliderValue cps = new DoubleSliderValue("CPS", 18, 20, 1, 60, 0.5);
	private BooleanValue moveFix = new BooleanValue("Move Fix", true);
	private ModeValue rotationMode = new ModeValue("Rotation", "Default", "None", "Default");
	private AdvancedTimer coolDown = new AdvancedTimer(1);
	private boolean leftDown, leftn, locked;
	private long leftDownTime, leftUpTime, leftk, leftl;
	private float yaw, pitch, prevYaw, prevPitch;
	private double leftm;

	public KillAura() {
		super("KillAura", ModuleCategory.Combat);
		this.registerSetting(cps, moveFix, rotationMode);
	}

	@EventLink
	public void onTick(TickEvent e) {
		if (!PlayerUtil.inGame())
			return;
		Mouse.poll();
		EntityPlayer pTarget = CombatUtil.instance.getTarget();
		if (pTarget == null || mc.currentScreen != null || !coolDown.hasFinished()) {
			target = null;
			rotate(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
			return;
		}
		target = pTarget;
		this.leftClickExecute(mc.gameSettings.keyBindAttack.getKeyCode());
		float[] rotations;
		if (rotationMode.is("Default")) {
			rotations = getTargetRotations(target, 0);
		} else {
			rotations = new float[] { mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch };
		}
		locked = false;
		rotate(rotations[0], rotations[1]);
	}

	@EventLink
	public void onPre(PreUpdateEvent e) {
		if (!PlayerUtil.inGame() || locked) {
			return;
		}

		float[] currentRots = new float[] { yaw, pitch };
		float[] prevRots = new float[] { prevYaw, prevPitch };
		float[] gcdPatch = CombatUtil.instance.getPatchedRots(currentRots, prevRots);

		e.setYaw(gcdPatch[0]);
		e.setPitch(gcdPatch[1]);

		mc.thePlayer.renderYawOffset = gcdPatch[0];
		mc.thePlayer.rotationYawHead = gcdPatch[0];

		prevYaw = e.getYaw();
		prevPitch = e.getPitch();

	}
	
	@EventLink
	public void onMoveInput(final MoveInputEvent e) {
		if (moveFix.isToggled()) {
			if (RotationUtil.getTargetRotation() == null)
				return;
			final float forward = e.getForward();
			final float strafe = e.getStrafe();
			final float yaw = RotationUtil.getTargetRotation().getYaw();
			locked = true;

			final double angle = MathHelper
					.wrapAngleTo180_double(Math.toDegrees(direction(mc.thePlayer.rotationYaw, forward, strafe)));

			if (forward == 0 && strafe == 0) {
				return;
			}

			float closestForward = 0, closestStrafe = 0, closestDifference = Float.MAX_VALUE;

			for (float predictedForward = -1f; predictedForward <= 1f; predictedForward += 1f) {
				for (float predictedStrafe = -1f; predictedStrafe <= 1f; predictedStrafe += 1f) {
					if (predictedStrafe == 0 && predictedForward == 0)
						continue;

					final double predictedAngle = MathHelper
							.wrapAngleTo180_double(Math.toDegrees(direction(yaw, predictedForward, predictedStrafe)));
					final double difference = Math.abs(angle - predictedAngle);

					if (difference < closestDifference) {
						closestDifference = (float) difference;
						closestForward = predictedForward;
						closestStrafe = predictedStrafe;
					}
				}
			}

			e.setForward(closestForward);
			e.setStrafe(closestStrafe);

		}
	}

	@EventLink
	public void onStrafe(StrafeEvent e) {
		if (!moveFix.isToggled() && locked) {
			locked = false;
			e.setYaw(0f);
		}
	}

	@EventLink
	public void onJump(JumpEvent e) {
		if (!moveFix.isToggled() && locked) {
			e.setYaw(0f);
		}
	}

	@EventLink
	public void lookEvent(LookEvent e) {
		if (locked || !PlayerUtil.inGame())
			return;
		e.setPrevYaw(prevYaw);
		e.setPrevPitch(prevPitch);
		e.setYaw(yaw);
		e.setPitch(pitch);
	}

	private void leftClickExecute(int key) {
		if (!PlayerUtil.inGame())
			return;
		if ((this.leftUpTime > 0L) && (this.leftDownTime > 0L)) {
			if ((System.currentTimeMillis() > this.leftUpTime) && leftDown) {
				if (mc.thePlayer.isUsingItem())
					mc.thePlayer.stopUsingItem();
				KeyBinding.onTick(key);
				this.genLeftTimings();
				leftDown = false;
			} else if (System.currentTimeMillis() > this.leftDownTime) {
				if (Mouse.isButtonDown(1))
					KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
				leftDown = true;
			}
		} else
			this.genLeftTimings();

	}

	private void genLeftTimings() {
		double clickSpeed = ClickUtil.instance.ranModuleVal(cps, MathHelper.rand())
				+ (0.4D * MathHelper.rand().nextDouble());
		long delay = (int) Math.round(1000.0D / clickSpeed);
		if (System.currentTimeMillis() > this.leftk) {
			if (!this.leftn && (MathHelper.rand().nextInt(100) >= 85)) {
				this.leftn = true;
				this.leftm = 1.1D + (MathHelper.rand().nextDouble() * 0.15D);
			} else
				this.leftn = false;

			this.leftk = System.currentTimeMillis() + 500L + MathHelper.rand().nextInt(1500);
		}

		if (this.leftn)
			delay = (long) (delay * this.leftm);

		if (System.currentTimeMillis() > this.leftl) {
			if (MathHelper.rand().nextInt(100) >= 80)
				delay += 50L + MathHelper.rand().nextInt(100);

			this.leftl = System.currentTimeMillis() + 500L + MathHelper.rand().nextInt(1500);
		}

		this.leftUpTime = System.currentTimeMillis() + delay;
		this.leftDownTime = (System.currentTimeMillis() + (delay / 2L)) - MathHelper.rand().nextInt(10);
	}

	private float[] getTargetRotations(Entity entityIn, float ps) {
		if (entityIn == null)
			return null;
		double diffX = entityIn.posX - mc.thePlayer.posX;
		double diffY;
		if (entityIn instanceof EntityLivingBase) {
			EntityLivingBase en = (EntityLivingBase) entityIn;
			diffY = (en.posY + ((double) en.getEyeHeight() * 0.9D))
					- (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());
		} else
			diffY = (((entityIn.getEntityBoundingBox().minY + entityIn.getEntityBoundingBox().maxY) / 2.0D) + ps)
					- (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());

		double diffZ = entityIn.posZ - mc.thePlayer.posZ;
		double dist = MathHelper.sqrt_double((diffX * diffX) + (diffZ * diffZ));
		float yaw = (float) ((Math.atan2(diffZ, diffX) * 180.0D) / 3.141592653589793D) - 90.0F;
		float pitch = (float) (-((Math.atan2(diffY, dist) * 180.0D) / 3.141592653589793D));
		return new float[] { mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw),
				mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch) };
	}


	private void rotate(float yaw, float pitch) {
		this.yaw = yaw;
		this.pitch = pitch;
	}

	private double direction(float rotationYaw, final double moveForward, final double moveStrafing) {
		if (moveForward < 0F)
			rotationYaw += 180F;

		float forward = 1F;

		if (moveForward < 0F)
			forward = -0.5F;
		else if (moveForward > 0F)
			forward = 0.5F;

		if (moveStrafing > 0F)
			rotationYaw -= 90F * forward;
		if (moveStrafing < 0F)
			rotationYaw += 90F * forward;

		return Math.toRadians(rotationYaw);
	}
}