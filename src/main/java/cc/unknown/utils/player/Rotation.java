package cc.unknown.utils.player;

import cc.unknown.event.impl.player.StrafeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MathHelper;

public class Rotation {
	public static Rotation instance;
    public float yaw;
    public float pitch;
    
    public Rotation() {
    	instance = this;
    }

    public Rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float f) {
        this.yaw = f;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float f) {
        this.pitch = f;
    }
    
    /**
     * Apply fixed sensitivity to the rotation.
     *
     * @param sensitivity The mouse sensitivity value (default: mc.gameSettings.mouseSensitivity).
     * @return The updated rotation with fixed sensitivity applied.
     */
    public Rotation fixedSensitivity(float sensitivity) {
        float gcd = getFixedAngleDelta(sensitivity);

        this.yaw = getFixedSensitivityAngle(this.yaw, RotationUtils.serverRotation.getYaw(), gcd);
        this.pitch = MathHelper.clamp_float(getFixedSensitivityAngle(this.pitch, RotationUtils.serverRotation.getPitch(), gcd), -90f, 90f);

        return this;
    }

    // Placeholder methods for getFixedAngleDelta and getFixedSensitivityAngle
    private float getFixedAngleDelta(float sensitivity) {
        return (float) Math.pow(sensitivity * 0.6f + 0.2f, 3) * 1.2f;
    }

    private float getFixedSensitivityAngle(float targetAngle, float startAngle, float gcd) {
        return startAngle + Math.round((targetAngle - startAngle) / gcd) * gcd;
    }
    
    /**
     * Apply strafe movement to the player.
     *
     * @param event  The strafe event containing movement parameters.
     * @param strict Whether to apply strict strafe movement.
     */
    public void applyStrafeToPlayer(StrafeEvent event, boolean strict) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;

        float yawDifference = MathHelper.wrapAngleTo180_float(player.rotationYaw - yaw - 23.5f - 135) + 180;
        int diff = (int) (yawDifference / 45);

        float strafe = event.getStrafe();
        float forward = event.getForward();
        float friction = event.getFriction();

        float calcForward;
        float calcStrafe;

        if (!strict) {
            switch (diff) {
                case 0:
                    calcForward = forward;
                    calcStrafe = strafe;
                    break;
                case 1:
                    calcForward = forward + strafe;
                    calcStrafe = strafe - forward;
                    break;
                case 2:
                    calcForward = strafe;
                    calcStrafe = -forward;
                    break;
                case 3:
                    calcForward = forward - strafe;
                    calcStrafe = -forward - strafe;
                    break;
                case 4:
                    calcForward = -forward;
                    calcStrafe = -strafe;
                    break;
                case 5:
                    calcForward = -forward - strafe;
                    calcStrafe = strafe - forward;
                    break;
                case 6:
                    calcForward = -strafe;
                    calcStrafe = forward;
                    break;
                case 7:
                    calcForward = forward + strafe;
                    calcStrafe = forward + strafe;
                    break;
                default:
                    calcForward = forward;
                    calcStrafe = strafe;
                    break;
            }

            if (Math.abs(calcForward) > 1f || (Math.abs(calcForward) > 0.3f && Math.abs(calcForward) < 0.9f)) {
                calcForward *= 0.5f;
            }

            if (Math.abs(calcStrafe) > 1f || (Math.abs(calcStrafe) > 0.3f && Math.abs(calcStrafe) < 0.9f)) {
                calcStrafe *= 0.5f;
            }
        } else {
            calcForward = event.getForward();
            calcStrafe = event.getStrafe();
        }

        float d = calcStrafe * calcStrafe + calcForward * calcForward;

        if (d >= 1.0E-4f) {
            d = friction / MathHelper.sqrt_float(d);

            float yawRad = (float) Math.toRadians(yaw);
            float yawSin = MathHelper.sin(yawRad);
            float yawCos = MathHelper.cos(yawRad);

            player.motionX += calcStrafe * yawCos - calcForward * yawSin;
            player.motionZ += calcForward * yawCos + calcStrafe * yawSin;
        }
    }
}