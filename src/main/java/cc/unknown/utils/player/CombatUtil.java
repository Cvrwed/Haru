package cc.unknown.utils.player;

import java.util.Objects;

import cc.unknown.utils.Loona;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public enum CombatUtil implements Loona {
	instance;

	public boolean canTarget(Entity entity, boolean idk) {
		if (entity != null && entity != mc.thePlayer) {
			EntityLivingBase entityLivingBase = null;

			if (entity instanceof EntityLivingBase) {
				entityLivingBase = (EntityLivingBase) entity;
			}

			boolean isTeam = isTeam(mc.thePlayer, entity);

			return !(entity instanceof EntityArmorStand)
					&& (entity instanceof EntityPlayer && !isTeam && !entity.isInvisible() && !idk
							|| entity instanceof EntityAnimal || entity instanceof EntityMob
							|| entity instanceof EntityLivingBase && entityLivingBase.isEntityAlive());
		} else {
			return false;
		}
	}

	public boolean isTeam(EntityPlayer player, Entity entity) {
		if (entity instanceof EntityPlayer && ((EntityPlayer) entity).getTeam() != null && player.getTeam() != null) {
			Character entity_3 = entity.getDisplayName().getFormattedText().charAt(3);
			Character player_3 = player.getDisplayName().getFormattedText().charAt(3);
			Character entity_2 = entity.getDisplayName().getFormattedText().charAt(2);
			Character player_2 = player.getDisplayName().getFormattedText().charAt(2);
			boolean isTeam = false;
			if (entity_3.equals(player_3) && entity_2.equals(player_2)) {
				isTeam = true;
			} else {
				Character entity_1 = entity.getDisplayName().getFormattedText().charAt(1);
				Character player_1 = player.getDisplayName().getFormattedText().charAt(1);
				Character entity_0 = entity.getDisplayName().getFormattedText().charAt(0);
				Character player_0 = player.getDisplayName().getFormattedText().charAt(0);
				if (entity_1.equals(player_1) && Character.isDigit(0) && entity_0.equals(player_0)) {
					isTeam = true;
				}
			}

			return isTeam;
		} else {
			return true;
		}
	}

	public interface IEntityFilter {
		boolean canRaycast(final EntityPlayer entity);
	}

	public EntityPlayer rayCast(final double range, final IEntityFilter entityFilter) {
		return rayCast(range, Objects.requireNonNull(RotationUtil.instance.getServerRotation()).getYaw(),
				RotationUtil.instance.getServerRotation().getPitch(), entityFilter);
	}

	public EntityPlayer rayCast(double range, float yaw, float pitch, IEntityFilter entityFilter) {
		if (mc.getRenderViewEntity() == null || mc.theWorld == null)
			return null;

		double blockReachDistance = range;
		Vec3 eyePosition = mc.getRenderViewEntity().getPositionEyes(1.0f);
		Vec3 entityLook = getVectorForRotation(yaw, pitch);
		Vec3 vec = eyePosition.addVector(entityLook.xCoord * blockReachDistance, entityLook.yCoord * blockReachDistance,
				entityLook.zCoord * blockReachDistance);

		Iterable<EntityPlayer> entityList = mc.theWorld.getEntities(EntityPlayer.class,
				entity -> entity != null
						&& (entity instanceof EntityLivingBase)
						&& (!(entity instanceof EntityPlayer) || !((EntityPlayer) entity).isSpectator())
						&& entity.canBeCollidedWith() && entity != mc.getRenderViewEntity());

		EntityPlayer pointedEntity = null;

		for (EntityPlayer entity : entityList) {
			if (!entityFilter.canRaycast(entity))
				continue;

			AxisAlignedBB axisAlignedBB = entity.getEntityBoundingBox();
			MovingObjectPosition movingObjectPosition = axisAlignedBB.calculateIntercept(eyePosition, vec);

			if (axisAlignedBB.isVecInside(eyePosition)) {
				if (blockReachDistance >= 0.0) {
					pointedEntity = entity;
					blockReachDistance = 0.0;
				}
			} else if (movingObjectPosition != null) {
				double eyeDistance = eyePosition.distanceTo(movingObjectPosition.hitVec);

				if (eyeDistance < blockReachDistance || blockReachDistance == 0.0) {
					if (entity == mc.getRenderViewEntity().ridingEntity
							&& !mc.getRenderViewEntity().canRiderInteract()) {
						if (blockReachDistance == 0.0)
							pointedEntity = entity;
					} else {
						pointedEntity = entity;
						blockReachDistance = eyeDistance;
					}
				}
			}
		}

		return pointedEntity;
	}

	public float rotsToFloat(final float[] rots, final int m) {
		if (m == 1) {
			return rots[0];
		}
		if (m == 2) {
			return rots[1] + 4.0f;
		}
		return -1.0f;
	}

	public void aim(final Entity en, final float offset) {
		if (en != null) {
			final float[] rots = getTargetRotations(en);
			if (rots != null) {
				final float yaw = rotsToFloat(rots, 1);
				final float pitch = rotsToFloat(rots, 2) + 4.0f + offset;
				mc.thePlayer.rotationYaw = yaw;
				mc.thePlayer.rotationPitch = pitch;
			}
		}
	}
	
	public void aimToOnlyPitch(final Entity en, final float offset,  final float e) {
		if (en != null) {
			final float[] rots = getTargetRotations(en);
			if (rots != null) {
				final float pitch = rotsToFloat(rots, 2) + e + offset;
				mc.thePlayer.rotationPitch = pitch;
			}
		}
	}

	public boolean canEntityBeSeen(Entity entityIn) {
		EntityPlayer p = mc.thePlayer;
		return mc.theWorld.rayTraceBlocks(new Vec3(p.posX, p.posY + (double) p.getEyeHeight(), p.posZ),
				new Vec3(entityIn.posX, entityIn.posY + (double) entityIn.getEyeHeight(), entityIn.posZ),
				false) == null;
	}

	public float[] getTargetRotations(final Entity en) {
		if (en == null) {
			return null;
		}
		final double diffX = en.posX - mc.thePlayer.posX;
		double diffY;
		if (en instanceof EntityLivingBase) {
			final EntityLivingBase x = (EntityLivingBase) en;
			diffY = x.posY + x.getEyeHeight() * 0.9 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
		} else {
			diffY = (en.getEntityBoundingBox().minY + en.getEntityBoundingBox().maxY) / 2.0
					- (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
		}
		final double diffZ = en.posZ - mc.thePlayer.posZ;
		final float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
		final float pitch = (float) (-(Math.atan2(diffY, MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ)) * 180.0
				/ Math.PI));
		return new float[] { mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw),
				mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch) };
	}

	public Vec3 getVectorForRotation(final float pitch, final float yaw) {
		final float f = MathHelper.cos(-yaw * 0.017453292f - 3.1415927f);
		final float f2 = MathHelper.sin(-yaw * 0.017453292f - 3.1415927f);
		final float f3 = -MathHelper.cos(-pitch * 0.017453292f);
		final float f4 = MathHelper.sin(-pitch * 0.017453292f);
		return new Vec3((double) (f2 * f3), (double) f4, (double) (f * f3));
	}

	public void aimAt(float pitch, float yaw, float fuckedYaw, float fuckedPitch, double speed) {
		float[] gcd = getPatchedRots(new float[] { yaw, pitch + ((int) fuckedPitch / 360) * 360 },
				new float[] { mc.thePlayer.prevRotationYaw, mc.thePlayer.prevRotationPitch });
		float cappedYaw = maxAngleChange(mc.thePlayer.prevRotationYaw, gcd[0], (float) speed);
		float cappedPitch = maxAngleChange(mc.thePlayer.prevRotationPitch, gcd[1], (float) speed);
		mc.thePlayer.rotationPitch = cappedPitch;
		mc.thePlayer.rotationYaw = cappedYaw;
	}

	public float[] getPatchedRots(final float[] currentRots, final float[] prevRots) {
		final float yawDif = currentRots[0] - prevRots[0];
		final float pitchDif = currentRots[1] - prevRots[1];
		final double gcd = mouseSens();

		currentRots[0] -= (float) (yawDif % gcd);
		currentRots[1] -= (float) (pitchDif % gcd);
		return currentRots;
	}

	public float maxAngleChange(final float prev, final float now, final float maxTurn) {
		float dif = MathHelper.wrapAngleTo180_float(now - prev);
		if (dif > maxTurn)
			dif = maxTurn;
		if (dif < -maxTurn)
			dif = -maxTurn;
		return prev + dif;
	}

	public double mouseSens() {
		final float sens = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
		final float pow = sens * sens * sens * 8.0F;
		return pow * 0.15D;
	}

	public boolean isATeamMate(Entity entity) {
		EntityPlayer teamMate = (EntityPlayer) entity;
		if (mc.thePlayer.isOnSameTeam((EntityLivingBase) entity) || mc.thePlayer.getDisplayName().getUnformattedText().startsWith(teamMate.getDisplayName().getUnformattedText().substring(0, 2)))
			return true;
		return false;
	}

	public double getDistanceToEntityBox(Entity entity1) {
		Vec3 eyes = entity1.getPositionEyes(1.0F);
		Vec3 pos = getNearestPointBB(eyes, entity1.getEntityBoundingBox());
		double xDist = Math.abs(pos.xCoord - eyes.xCoord);
		double yDist = Math.abs(pos.yCoord - eyes.yCoord);
		double zDist = Math.abs(pos.zCoord - eyes.zCoord);
		return Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2) + Math.pow(zDist, 2));
	}

	public Vec3 getNearestPointBB(Vec3 eye, AxisAlignedBB box) {
		double[] origin = { eye.xCoord, eye.yCoord, eye.zCoord };
		double[] destMins = { box.minX, box.minY, box.minZ };
		double[] destMaxs = { box.maxX, box.maxY, box.maxZ };

		for (int i = 0; i < 3; i++) {
			if (origin[i] > destMaxs[i]) {
				origin[i] = destMaxs[i];
			} else if (origin[i] < destMins[i]) {
				origin[i] = destMins[i];
			}
		}

		return new Vec3(origin[0], origin[1], origin[2]);
	}

	public int getPing(EntityPlayer e) {
		return mc.getNetHandler().getPlayerInfo(e.getUniqueID()) != null ? mc.getNetHandler().getPlayerInfo(e.getUniqueID()).getResponseTime() : 0;
	}
	
    public double nearestRotation(final AxisAlignedBB bb) {
        final Vec3 eyes = mc.thePlayer.getPositionEyes(1F);

        Vec3 vecRotation3d = null;

        for(double xSearch = 0D; xSearch <= 1D; xSearch += 0.05D) {
            for (double ySearch = 0D; ySearch < 1D; ySearch += 0.05D) {
                for (double zSearch = 0D; zSearch <= 1D; zSearch += 0.05D) {
                    final Vec3 vec3 = new Vec3(
                            bb.minX + (bb.maxX - bb.minX) * xSearch,
                            bb.minY + (bb.maxY - bb.minY) * ySearch,
                            bb.minZ + (bb.maxZ - bb.minZ) * zSearch
                    );
                    final double vecDist = eyes.squareDistanceTo(vec3);

                    if (vecRotation3d == null || eyes.squareDistanceTo(vecRotation3d) > vecDist) {
                        vecRotation3d = vec3;
                    }
                }
            }
        }
        return vecRotation3d.distanceTo(eyes);
    }
    
    public Vec3 closestPointToBox(Vec3 start, AxisAlignedBB box) {
        double x = coerceInRange(start.xCoord, box.minX, box.maxX);
        double y = coerceInRange(start.yCoord, box.minY, box.maxY);
        double z = coerceInRange(start.zCoord, box.minZ, box.maxZ);
        return new Vec3(x, y, z);
    }
    
    private double coerceInRange(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }
    
    public double getLookingTargetRange(EntityPlayerSP thePlayer, AxisAlignedBB bb) {
        return getLookingTargetRange(thePlayer, bb, 6.0);
    }
    
    public double getLookingTargetRange(EntityPlayerSP thePlayer, AxisAlignedBB bb, double range) {
        Vec3 eyes = thePlayer.getPositionEyes(1F);
        Vec3 direction = RotationUtil.instance.getCurrentRotation().toDirection();
        Vec3 adjustedDirection = multiply(direction, range);
        Vec3 target = adjustedDirection.add(eyes);
        MovingObjectPosition movingObj = bb.calculateIntercept(eyes, target);
        return movingObj != null ? movingObj.hitVec.distanceTo(eyes) : Double.MAX_VALUE;
    }
    
    public static Vec3 multiply(Vec3 vec, double value) {
        return new Vec3(vec.xCoord * value, vec.yCoord * value, vec.zCoord * value);
    }
}
