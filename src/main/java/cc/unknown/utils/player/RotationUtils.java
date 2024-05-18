package cc.unknown.utils.player;

import java.util.Random;

import cc.unknown.Haru;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.utils.Loona;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RotationUtils implements Loona {

	public RotationUtils() {
		Haru.instance.getEventBus().register(this);
	}
	
	// LiquidX Utils

	private static Random random = new Random();

	private static int keepLength;
	private static int revTick;

	public static Rotation targetRotation;
	public static Rotation serverRotation = new Rotation(0F, 0F);
	public static float[] clientRotation = new float[]{0.0F, 0.0F};

	public static boolean keepCurrentRotation = false;

	public double x = random.nextDouble();
	public double y = random.nextDouble();
	public double z = random.nextDouble();

	public static Rotation getRotationsEntity(EntityLivingBase entity) {
		return getRotations(entity.posX, entity.posY + entity.getEyeHeight() - 0.4, entity.posZ);
	}
	
    public static float[] getRotationsToPosition(final double x, final double y, final double z) {
        final double deltaX = x - mc.thePlayer.posX;
        final double deltaY = y - mc.thePlayer.posY - mc.thePlayer.getEyeHeight();
        final double deltaZ = z - mc.thePlayer.posZ;
        final double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        final float yaw = (float)Math.toDegrees(-Math.atan2(deltaX, deltaZ));
        final float pitch = (float)Math.toDegrees(-Math.atan2(deltaY, horizontalDistance));
        return new float[] { yaw, pitch };
    }

	public static Rotation toRotation(final Vec3 vec, final boolean predict) {
		final Vec3 eyesPos = new Vec3(mc.thePlayer.posX,
				mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

		if (predict) {
			if (mc.thePlayer.onGround) {
				eyesPos.addVector(mc.thePlayer.motionX, 0.0, mc.thePlayer.motionZ);
			} else
				eyesPos.addVector(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ);
		}

		final double diffX = vec.xCoord - eyesPos.xCoord;
		final double diffY = vec.yCoord - eyesPos.yCoord;
		final double diffZ = vec.zCoord - eyesPos.zCoord;

		return new Rotation(MathHelper.wrapAngleTo180_float((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F),
				MathHelper.wrapAngleTo180_float(
						(float) (-Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ))))));
	}

	public static Vec3 getCenter(final AxisAlignedBB bb) {
		return new Vec3(bb.minX + (bb.maxX - bb.minX) * 0.5, bb.minY + (bb.maxY - bb.minY) * 0.5,
				bb.minZ + (bb.maxZ - bb.minZ) * 0.5);
	}

	public static double getRotationDifference(final Entity entity) {
		final Rotation rotation = toRotation(getCenter(entity.getEntityBoundingBox()), true);

		return getRotationDifference(rotation, new Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch));
	}

	public static double getRotationDifference(final Rotation rotation) {
		return serverRotation == null ? 0D : getRotationDifference(rotation, serverRotation);
	}

	public static double getRotationDifference(final Rotation a, final Rotation b) {
		return Math.hypot(getAngleDifference(a.getYaw(), b.getYaw()), a.getPitch() - b.getPitch());
	}

	public static Rotation limitAngleChange(final Rotation currentRotation, final Rotation targetRotation,
			final float turnSpeed) {
		final float yawDifference = getAngleDifference(targetRotation.getYaw(), currentRotation.getYaw());
		final float pitchDifference = getAngleDifference(targetRotation.getPitch(), currentRotation.getPitch());

		return new Rotation(
				currentRotation.getYaw()
						+ (yawDifference > turnSpeed ? turnSpeed : Math.max(yawDifference, -turnSpeed)),
				currentRotation.getPitch()
						+ (pitchDifference > turnSpeed ? turnSpeed : Math.max(pitchDifference, -turnSpeed)));
	}

	public static float getAngleDifference(final float a, final float b) {
		return ((((a - b) % 360F) + 540F) % 360F) - 180F;
	}

	@EventLink
	public void onTick(final TickEvent event) {
		if (targetRotation != null) {
			keepLength--;

			if (keepLength <= 0) {
				if (revTick > 0) {
					revTick--;
					reset();
				} else
					reset();
			}
		}

		if (random.nextGaussian() > 0.8D)
			x = Math.random();
		if (random.nextGaussian() > 0.8D)
			y = Math.random();
		if (random.nextGaussian() > 0.8D)
			z = Math.random();
	}

	@EventLink
	public void onPacket(final PacketEvent event) {
		final Packet<?> packet = event.getPacket();
		if (event.isSend()) {

			if (packet instanceof C03PacketPlayer) {
				final C03PacketPlayer packetPlayer = (C03PacketPlayer) packet;

				if (targetRotation != null && !keepCurrentRotation
						&& (targetRotation.getYaw() != serverRotation.getYaw()
								|| targetRotation.getPitch() != serverRotation.getPitch())) {
					packetPlayer.yaw = targetRotation.getYaw();
					packetPlayer.pitch = targetRotation.getPitch();
					packetPlayer.rotating = true;
				}

				if (packetPlayer.rotating)
					serverRotation = new Rotation(packetPlayer.yaw, packetPlayer.pitch);
			}
		}
	}

	public static void setTargetRotation(final Rotation rotation) {
		setTargetRotation(rotation, 0);
	}

	public static void setTargetRotation(final Rotation rotation, final int c) {
		if (Double.isNaN(rotation.getYaw()) || Double.isNaN(rotation.getPitch()) || rotation.getPitch() > 90
				|| rotation.getPitch() < -90)
			return;

		rotation.fixedSensitivity(mc.gameSettings.mouseSensitivity);
		targetRotation = rotation;
		keepLength = c;
		revTick = 0;
	}

	public static void reset() {
		keepLength = 0;
		if (revTick > 0) {
			targetRotation = new Rotation(
					targetRotation.getYaw()
							- getAngleDifference(targetRotation.getYaw(), mc.thePlayer.rotationYaw) / revTick,
					targetRotation.getPitch()
							- getAngleDifference(targetRotation.getPitch(), mc.thePlayer.rotationPitch) / revTick);
		} else
			targetRotation = null;
	}

	public static Rotation getRotations(Entity ent) {
		double x = ent.posX;
		double z = ent.posZ;
		double y = ent.posY + (double) (ent.getEyeHeight() / 2.0f);
		return getRotationFromPosition(x, z, y);
	}

	public static Rotation getRotations(double posX, double posY, double posZ) {
		EntityPlayerSP player = mc.thePlayer;
		double x = posX - player.posX;
		double y = posY - (player.posY + (double) player.getEyeHeight());
		double z = posZ - player.posZ;
		double dist = MathHelper.sqrt_double(x * x + z * z);
		float yaw = (float) (Math.atan2(z, x) * 180.0 / Math.PI) - 90.0f;
		float pitch = (float) (-(Math.atan2(y, dist) * 180.0 / Math.PI));
		return new Rotation(yaw, pitch);
	}

	public static Rotation getRotationFromPosition(double x, double z, double y) {
		double xDiff = x - mc.thePlayer.posX;
		double zDiff = z - mc.thePlayer.posZ;
		double yDiff = y - mc.thePlayer.posY - 1.2;
		double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
		float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0 / Math.PI) - 90.0f;
		float pitch = (float) (-Math.atan2(yDiff, dist) * 180.0 / Math.PI);
		return new Rotation(yaw, pitch);
	}

	public static Rotation getTargetRotation() {
		return targetRotation;
	}

	public static Rotation getServerRotation() {
		return serverRotation;
	}
}
