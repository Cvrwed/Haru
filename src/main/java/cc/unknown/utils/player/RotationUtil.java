package cc.unknown.utils.player;

import java.util.ArrayList;

import cc.unknown.Haru;
import cc.unknown.utils.helpers.MathHelper;
import cc.unknown.utils.interfaces.Loona;
import net.minecraft.client.Minecraft;
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
	
    public float[] augustusStrafe(final float strafe, final float forward, final float yaw, final boolean advanced) {
        final Minecraft mc = Minecraft.getMinecraft();
        final float diff = MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw);
        float newForward = 0.0f;
        float newStrafe = 0.0f;
        if (!advanced) {
            if (diff >= 22.5 && diff < 67.5) {
                newStrafe += strafe;
                newForward += forward;
                newStrafe -= forward;
                newForward += strafe;
            }
            else if (diff >= 67.5 && diff < 112.5) {
                newStrafe -= forward;
                newForward += strafe;
            }
            else if (diff >= 112.5 && diff < 157.5) {
                newStrafe -= strafe;
                newForward -= forward;
                newStrafe -= forward;
                newForward += strafe;
            }
            else if (diff >= 157.5 || diff <= -157.5) {
                newStrafe -= strafe;
                newForward -= forward;
            }
            else if (diff > -157.5 && diff <= -112.5) {
                newStrafe -= strafe;
                newForward -= forward;
                newStrafe += forward;
                newForward -= strafe;
            }
            else if (diff > -112.5 && diff <= -67.5) {
                newStrafe += forward;
                newForward -= strafe;
            }
            else if (diff > -67.5 && diff <= -22.5) {
                newStrafe += strafe;
                newForward += forward;
                newStrafe += forward;
                newForward -= strafe;
            }
            else {
                newStrafe += strafe;
                newForward += forward;
            }
            return new float[] { newStrafe, newForward };
        }
        final double[] realMotion = getMotion(0.22, strafe, forward, Haru.instance.getSilentHelper().realYaw);
        final double[] array;
        final double[] realPos = array = new double[] { mc.thePlayer.posX, mc.thePlayer.posZ };
        final int n = 0;
        array[n] += realMotion[0];
        final double[] array2 = realPos;
        final int n2 = 1;
        array2[n2] += realMotion[1];
        final ArrayList<float[]> possibleForwardStrafe = new ArrayList<float[]>();
        int i = 0;
        boolean b = false;
        while (!b) {
            newForward = 0.0f;
            newStrafe = 0.0f;
            if (i == 0) {
                newStrafe += strafe;
                newForward += forward;
                newStrafe -= forward;
                newForward += strafe;
                possibleForwardStrafe.add(new float[] { newForward, newStrafe });
            }
            else if (i == 1) {
                newStrafe -= forward;
                newForward += strafe;
                possibleForwardStrafe.add(new float[] { newForward, newStrafe });
            }
            else if (i == 2) {
                newStrafe -= strafe;
                newForward -= forward;
                newStrafe -= forward;
                newForward += strafe;
                possibleForwardStrafe.add(new float[] { newForward, newStrafe });
            }
            else if (i == 3) {
                newStrafe -= strafe;
                newForward -= forward;
                possibleForwardStrafe.add(new float[] { newForward, newStrafe });
            }
            else if (i == 4) {
                newStrafe -= strafe;
                newForward -= forward;
                newStrafe += forward;
                newForward -= strafe;
                possibleForwardStrafe.add(new float[] { newForward, newStrafe });
            }
            else if (i == 5) {
                newStrafe += forward;
                newForward -= strafe;
                possibleForwardStrafe.add(new float[] { newForward, newStrafe });
            }
            else if (i == 6) {
                newStrafe += strafe;
                newForward += forward;
                newStrafe += forward;
                newForward -= strafe;
                possibleForwardStrafe.add(new float[] { newForward, newStrafe });
            }
            else {
                newStrafe += strafe;
                newForward += forward;
                possibleForwardStrafe.add(new float[] { newForward, newStrafe });
                b = true;
            }
            ++i;
        }
        double distance = 5000.0;
        float[] floats = new float[2];
        for (final float[] flo : possibleForwardStrafe) {
            if (flo[0] > 1.0f) {
                flo[0] = 1.0f;
            }
            else if (flo[0] < -1.0f) {
                flo[0] = -1.0f;
            }
            if (flo[1] > 1.0f) {
                flo[1] = 1.0f;
            }
            else if (flo[1] < -1.0f) {
                flo[1] = -1.0f;
            }
            final double[] motion2;
            final double[] motion = motion2 = getMotion(0.22, flo[1], flo[0], mc.thePlayer.rotationYaw);
            final int n3 = 0;
            motion2[n3] += mc.thePlayer.posX;
            final double[] array3 = motion;
            final int n4 = 1;
            array3[n4] += mc.thePlayer.posZ;
            final double diffX = Math.abs(realPos[0] - motion[0]);
            final double diffZ = Math.abs(realPos[1] - motion[1]);
            final double d0 = diffX * diffX + diffZ * diffZ;
            if (d0 < distance) {
                distance = d0;
                floats = flo;
            }
        }
        return new float[] { floats[1], floats[0] };
    }
   
    public double[] getMotion(final double speed, final float strafe, final float forward, final float yaw) {
        final float friction = (float)speed;
        final float f1 = MathHelper.sin(yaw * 3.1415927f / 180.0f);
        final float f2 = MathHelper.cos(yaw * 3.1415927f / 180.0f);
        final double motionX = strafe * friction * f2 - forward * friction * f1;
        final double motionZ = forward * friction * f2 + strafe * friction * f1;
        return new double[] { motionX, motionZ };
    }
}
