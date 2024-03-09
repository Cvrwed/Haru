package cc.unknown.module.impl.combat;

import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.input.Mouse;

import cc.unknown.Haru;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MoveInputEvent;
import cc.unknown.event.impl.move.PostUpdateEvent;
import cc.unknown.event.impl.player.JumpEvent;
import cc.unknown.event.impl.player.StrafeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.helpers.MathHelper;
import cc.unknown.utils.misc.ClickUtil;
import cc.unknown.utils.player.CombatUtil;
import cc.unknown.utils.player.PlayerUtil;
import cc.unknown.utils.player.RotationUtil;
import net.minecraft.entity.Entity;

public class AimAssist extends Module {
	private SliderValue speedYaw = new SliderValue("Speed Yaw", 50.0, 1.0, 100.0, 1.0);
	private SliderValue complimentYaw = new SliderValue("Compliment Yaw", 50.0, 1.0, 100.0, 1.0);
	private BooleanValue clickAim = new BooleanValue("Click Aim", true);
	private BooleanValue center = new BooleanValue("Center", false);
	private BooleanValue rayCast = new BooleanValue("Ray Cast", false);
	private BooleanValue moveFix = new BooleanValue("Move Fix", false);
	private BooleanValue weaponOnly = new BooleanValue("Weapon Only", false);

	private float fixedYaw = 0f;
	private boolean fixed = false;

	public AimAssist() {
		super("AimAssist", ModuleCategory.Combat);
		this.registerSetting(speedYaw, complimentYaw, clickAim, center, rayCast, moveFix, weaponOnly);
	}

	@EventLink
	public void onMoveInput(final MoveInputEvent e) {
		if (moveFix.isToggled()) {
			if (RotationUtil.getTargetRotation() == null)
				return;
			final float forward = e.getForward();
			final float strafe = e.getStrafe();
			final float yaw = fixedYaw = RotationUtil.getTargetRotation().getYaw();
			fixed = true;

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
		if (!moveFix.isToggled() && fixed) {
			fixed = false;
			e.setYaw(fixedYaw);
		}
	}

	@EventLink
	public void onJump(JumpEvent e) {
		if (!moveFix.isToggled() && fixed) {
			e.setYaw(fixedYaw);
		}
	}

	@EventLink
	public void onPost(PostUpdateEvent e) {
		if (mc.thePlayer == null || mc.currentScreen != null || !mc.inGameHasFocus)
			return;

		if (!weaponOnly.isToggled() || PlayerUtil.isHoldingWeapon()) {
			AutoClick clicker = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);
			if ((clickAim.isToggled() && ClickUtil.instance.isClicking())
					|| (Mouse.isButtonDown(0) && clicker != null && !clicker.isEnabled()) || !clickAim.isToggled()) {
				Entity enemy = getEnemy();
				if (enemy != null) {
					if (center.isToggled()) {
						CombatUtil.instance.aim(enemy, 0.0f);
					} else {
						double n = PlayerUtil.fovFromEntity(enemy);
						if (n > 1.0D || n < -1.0D) {
							double compliment = n
									* (ThreadLocalRandom.current().nextDouble(complimentYaw.getInput() - 1.47328,
											complimentYaw.getInput() + 2.48293) / 100);
							float val = (float) (-(compliment + n / (101.0D - (float) ThreadLocalRandom.current()
									.nextDouble(speedYaw.getInput() - 4.723847, speedYaw.getInput()))));
							mc.thePlayer.rotationYaw += val;
						}

						if (rayCast.isToggled()) {
							CombatUtil.instance.rayCast(enemy);
						}
					}
				}
			}
		}
	}

	private Entity getEnemy() {
		return CombatUtil.instance.getTarget();
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
