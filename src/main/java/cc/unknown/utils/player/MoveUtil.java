package cc.unknown.utils.player;

import cc.unknown.utils.Loona;
import cc.unknown.utils.helpers.MathHelper;
import net.minecraft.potion.Potion;

public class MoveUtil implements Loona {

	public static float getSpeed() {
		return (float) Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
	}

	public static void stop() {
		mc.thePlayer.motionX = 0.0D;
		mc.thePlayer.motionY = 0.0D;
		mc.thePlayer.motionZ = 0.0D;
	}

	public static double jumpMotion() {
		return jumpBoostMotion(0.42F);
	}

	public static double jumpBoostMotion(final double motionY) {
		if (mc.thePlayer.isPotionActive(Potion.jump)) {
			return motionY + (mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;
		}

		return motionY;
	}

	public void strafe() {
		strafe(speed());
	}

	public static void strafe(final double speed) {
		if (!PlayerUtil.isMoving()) {
			return;
		}

		final double yaw = direction();
		mc.thePlayer.motionX = -MathHelper.sin((float) yaw) * speed;
		mc.thePlayer.motionZ = MathHelper.cos((float) yaw) * speed;
	}

	public static double direction() {
		float rotationYaw = mc.thePlayer.rotationYaw;

		if (mc.thePlayer.moveForward < 0) {
			rotationYaw += 180;
		}

		float forward = 1;

		if (mc.thePlayer.moveForward < 0) {
			forward = -0.5F;
		} else if (mc.thePlayer.moveForward > 0) {
			forward = 0.5F;
		}

		if (mc.thePlayer.moveStrafing > 0) {
			rotationYaw -= 70 * forward;
		}

		if (mc.thePlayer.moveStrafing < 0) {
			rotationYaw += 70 * forward;
		}

		return Math.toRadians(rotationYaw);
	}

	public double speed() {
		return Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
	}

}