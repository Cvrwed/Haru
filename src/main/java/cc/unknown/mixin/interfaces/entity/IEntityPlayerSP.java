package cc.unknown.mixin.interfaces.entity;

public interface IEntityPlayerSP {
	float[] augustusStrafe(final float strafe, final float forward, final float yaw, final boolean advanced);
	double[] getMotion(final double speed, final float strafe, final float forward, final float yaw);
}
