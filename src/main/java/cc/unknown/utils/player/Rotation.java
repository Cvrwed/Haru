package cc.unknown.utils.player;

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
    
    public Vec3 toDirection() {
        float f = MathHelper.cos(-yaw * 0.017453292f - (float) Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292f - (float) Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292f);
        float f3 = MathHelper.sin(-pitch * 0.017453292f);
        return new Vec3(f1 * f2, f3, f * f2);
    }
}