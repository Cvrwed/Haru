package cc.unknown.utils.player;

import cc.unknown.utils.helpers.MathHelper;
import cc.unknown.utils.interfaces.Loona;
import net.minecraft.entity.EntityLivingBase;

public enum RotationUtil implements Loona {
	instance;

	private Rotation targetRotation;
	public int keepLength;

	public float getDistanceAngles(float angle1, float angle2) {
		float angle = Math.abs(angle1 - angle2) % 360.0F;
		if (angle > 180.0F) {
			angle = 360.0F - angle;
		}
		return angle;
	}

	public float getYawDifference(float yaw1, float yaw2) {
		float yawDiff = MathHelper.wrapAngleTo180_float(yaw1) - MathHelper.wrapAngleTo180_float(yaw2);
		if (Math.abs(yawDiff) > 180)
			yawDiff = yawDiff + 360;
		return MathHelper.wrapAngleTo180_float(yawDiff);
	}

	public float[] getRotationFromPosition(double x, double z, double y) {
		double xDiff = x - mc.thePlayer.posX;
		double zDiff = z - mc.thePlayer.posZ;
		double yDiff = y - mc.thePlayer.posY - 1.2;

		double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
		float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / 3.141592653589793D) - 90.0F;
		float pitch = (float) -(Math.atan2(yDiff, dist) * 180.0D / 3.141592653589793D);
		return new float[] { yaw, pitch };
	}

	public float updateRotation(float current, float calc, float maxDelta) {
		float f = MathHelper.wrapAngleTo180_float(calc - current);
		if (f > maxDelta) {
			f = maxDelta;
		}

		if (f < -maxDelta) {
			f = -maxDelta;
		}

		return current + f;
	}

	public float[] getRotations(EntityLivingBase ent) {
		double x = ent.posX;
		double z = ent.posZ;
		double y = ent.posY + ent.getEyeHeight() / 2.0F;
		return getRotationFromPosition(x, z, y);
	}

	public Rotation getTargetRotation() {
		return targetRotation;
	}
}
