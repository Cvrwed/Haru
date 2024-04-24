package cc.unknown.utils.player;

import cc.unknown.event.impl.player.StrafeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

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
    
    public void applyStrafeToPlayer(StrafeEvent event, boolean strict) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        float diff = (player.rotationYaw - yaw) * ((float) Math.PI / 180f);

        float friction = event.getFriction();

        float calcForward;
        float calcStrafe;

        if (!strict) {
            float strafe = event.getStrafe() / 0.98f;
            float forward = event.getForward() / 0.98f;
            float modifiedForward = (float) (Math.signum(forward) * Math.ceil(Math.abs(forward)));
            float modifiedStrafe = (float) (Math.signum(strafe) * Math.ceil(Math.abs(strafe)));
            calcForward = Math.round(modifiedForward * MathHelper.cos(diff) + modifiedStrafe * MathHelper.sin(diff));
            calcStrafe = Math.round(modifiedStrafe * MathHelper.cos(diff) - modifiedForward * MathHelper.sin(diff));
            float f = (event.getForward() != 0f) ? event.getForward() : event.getStrafe();
            calcForward *= Math.abs(f);
            calcStrafe *= Math.abs(f);
        } else {
            calcForward = event.getForward();
            calcStrafe = event.getStrafe();
        }

        float d = calcStrafe * calcStrafe + calcForward * calcForward;

        if (d >= 1.0E-4f) {
            d = friction / (float) Math.sqrt(d);

            calcStrafe *= d;
            calcForward *= d;

            float yawRad = (float) Math.toRadians(yaw);
            float yawSin = MathHelper.sin(yawRad);
            float yawCos = MathHelper.cos(yawRad);

            player.motionX += calcStrafe * yawCos - calcForward * yawSin;
            player.motionZ += calcForward * yawCos + calcStrafe * yawSin;
        }
    }
    
    public Rotation fixedSensitivity(float sensitivity) {
        float gcd = getFixedAngleDelta(sensitivity);
        yaw = getFixedSensitivityAngle(yaw, RotationUtil.instance.getServerRotation().getYaw(), gcd);
        pitch = MathHelper.clamp_float(getFixedSensitivityAngle(pitch, RotationUtil.instance.getServerRotation().getPitch(), gcd), -90f, 90f);

        return this;
    }
    
    public float getFixedAngleDelta(float sensitivity) {
        return (float) (Math.pow((sensitivity * 0.6f + 0.2f), 3) * 1.2f);
    }

    public float getFixedAngleDelta() {
        return getFixedAngleDelta(Minecraft.getMinecraft().gameSettings.mouseSensitivity);
    }

    public float getFixedSensitivityAngle(float targetAngle, float startAngle, float gcd) {
        return startAngle + Math.round((targetAngle - startAngle) / gcd) * gcd;
    }

    public float getFixedSensitivityAngle(float targetAngle, float startAngle) {
        return getFixedSensitivityAngle(targetAngle, startAngle, getFixedAngleDelta());
    }

    public Rotation fixedSensitivity() {
        return fixedSensitivity(Minecraft.getMinecraft().gameSettings.mouseSensitivity);
    }
    
    public Vec3 toDirection() {
        float f = MathHelper.cos(-yaw * 0.017453292f - (float) Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292f - (float) Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292f);
        float f3 = MathHelper.sin(-pitch * 0.017453292f);
        return new Vec3(f1 * f2, f3, f * f2);
    }
}