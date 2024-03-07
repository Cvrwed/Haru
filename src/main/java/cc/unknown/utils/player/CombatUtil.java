package cc.unknown.utils.player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.base.Predicates;

import cc.unknown.Haru;
import cc.unknown.module.impl.other.AntiBot;
import cc.unknown.module.impl.settings.Targets;
import cc.unknown.utils.Loona;
import cc.unknown.utils.helpers.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public enum CombatUtil implements Loona {
	instance;
	
	public Entity raycastEntity(final double range, final IEntityFilter entityFilter) {
		return raycastEntity(range, Objects.requireNonNull(RotationUtil.getTargetRotation()).getYaw(),
				RotationUtil.getTargetRotation().getPitch(), entityFilter);
	}

	private static Entity raycastEntity(final double range, final float yaw, final float pitch,
			final IEntityFilter entityFilter) {
		final Entity renderViewEntity = mc.getRenderViewEntity();

		if (renderViewEntity != null && mc.theWorld != null) {
			double blockReachDistance = range;
			final Vec3 eyePosition = renderViewEntity.getPositionEyes(1F);

			final float yawCos = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
			final float yawSin = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
			final float pitchCos = -MathHelper.cos(-pitch * 0.017453292F);
			final float pitchSin = MathHelper.sin(-pitch * 0.017453292F);

			final Vec3 entityLook = new Vec3(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
			final Vec3 vector = eyePosition.addVector(entityLook.xCoord * blockReachDistance,
					entityLook.yCoord * blockReachDistance, entityLook.zCoord * blockReachDistance);
			final List<Entity> entityList = mc.theWorld.getEntitiesInAABBexcluding(renderViewEntity,
					renderViewEntity.getEntityBoundingBox()
							.addCoord(entityLook.xCoord * blockReachDistance, entityLook.yCoord * blockReachDistance,
									entityLook.zCoord * blockReachDistance)
							.expand(1D, 1D, 1D),
					Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));

			Entity pointedEntity = null;

			for (final Entity entity : entityList) {
				if (!entityFilter.canRaycast(entity))
					continue;

				final float collisionBorderSize = entity.getCollisionBorderSize();
				final AxisAlignedBB axisAlignedBB = entity.getEntityBoundingBox().expand(collisionBorderSize,
						collisionBorderSize, collisionBorderSize);
				final MovingObjectPosition movingObjectPosition = axisAlignedBB.calculateIntercept(eyePosition, vector);

				if (axisAlignedBB.isVecInside(eyePosition)) {
					if (blockReachDistance >= 0.0D) {
						pointedEntity = entity;
						blockReachDistance = 0.0D;
					}
				} else if (movingObjectPosition != null) {
					final double eyeDistance = eyePosition.distanceTo(movingObjectPosition.hitVec);

					if (eyeDistance < blockReachDistance || blockReachDistance == 0.0D) {
						if (entity == renderViewEntity.ridingEntity && !renderViewEntity.canRiderInteract()) {
							if (blockReachDistance == 0.0D)
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

		return null;
	}

	public boolean canTarget(final Entity entity) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (isValidTarget(player)) {
				return true;
			}
		}
		return false;
	}

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
		boolean canRaycast(final Entity entity);
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

	public float[] getRotationNeededForBlock(final BlockPos bp) {
		final double x = bp.getX() - mc.thePlayer.posX;
		final double y = bp.getY() + 0.5 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
		final double z = bp.getZ() - mc.thePlayer.posZ;
		final float yaw = (float) (Math.atan2(z, x) * 180.0 / 3.141592653589793) - 90.0f;
		final float pitch = (float) (-(Math.atan2(y, Math.sqrt(x * x + z * z)) * 180.0 / 3.141592653589793));
		return new float[] { yaw, pitch };
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

	public void rayCast(final Entity en) {
		getMouseOver(en, getTargetRotations(en)[1], getTargetRotations(en)[0], 6.0);
	}

	public void silentRotations(final float bodyRot, final float headRot) {
		if (headRot > bodyRot) {
			return;
		}
		mc.thePlayer.renderYawOffset = bodyRot;
		mc.thePlayer.rotationYawHead = headRot;
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
		final float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
		final float pitch = (float) (-(Math.atan2(diffY, MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ)) * 180.0
				/ 3.141592653589793));
		return new float[] { mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw),
				mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch) };
	}

	public MovingObjectPosition getMouseOver(final Entity entity, final float yaw, final float pitch,
			final double range) {
		if (entity != null && mc.theWorld != null) {
			Entity pointedEntity = null;
			mc.pointedEntity = null;
			MovingObjectPosition objectMouseOver = rayTrace(entity, range, 1.0f, yaw, pitch);
			double d1 = range;
			final Vec3 vec3 = entity.getPositionEyes(1.0f);
			if (objectMouseOver != null && objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
				d1 = objectMouseOver.hitVec.distanceTo(vec3);
			}
			final Vec3 vec4 = getVectorForRotation(pitch, yaw);
			final Vec3 vec5 = vec3.addVector(vec4.xCoord * range, vec4.yCoord * range, vec4.zCoord * range);
			Vec3 vec6 = null;
			final List<Entity> list = mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox()
					.addCoord(vec4.xCoord * range, vec4.yCoord * range, vec4.zCoord * range).expand(1.0, 1.0, 1.0),
					Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
			double d2 = d1;
			for (int i = 0; i < list.size(); ++i) {
				final Entity entity2 = (Entity) list.get(i);
				final float f2 = entity2.getCollisionBorderSize();
				final AxisAlignedBB axisalignedbb = entity2.getEntityBoundingBox().expand((double) f2, (double) f2,
						(double) f2);
				final MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec5);
				if (axisalignedbb.isVecInside(vec3)) {
					if (d2 >= 0.0) {
						pointedEntity = entity2;
						vec6 = ((movingobjectposition == null) ? vec3 : movingobjectposition.hitVec);
						d2 = 0.0;
					}
				} else if (movingobjectposition != null) {
					final double d3 = vec3.distanceTo(movingobjectposition.hitVec);
					if (d3 < d2 || d2 == 0.0) {
						final boolean flag2 = false;
						if (entity2 == entity.ridingEntity && !flag2) {
							if (d2 == 0.0) {
								pointedEntity = entity2;
								vec6 = movingobjectposition.hitVec;
							}
						} else {
							pointedEntity = entity2;
							vec6 = movingobjectposition.hitVec;
							d2 = d3;
						}
					}
				}
			}
			if (pointedEntity != null) {
			}
			if (pointedEntity != null && (d2 < d1 || objectMouseOver == null)) {
				objectMouseOver = new MovingObjectPosition(pointedEntity, vec6);
			}
			return objectMouseOver;
		}
		return null;
	}

	public MovingObjectPosition rayTrace(final Entity view, final double blockReachDistance,
			final float partialTick, final float yaw, final float pitch) {
		final Vec3 vec3 = view.getPositionEyes(1.0f);
		final Vec3 vec4 = getVectorForRotation(pitch, yaw);
		final Vec3 vec5 = vec3.addVector(vec4.xCoord * blockReachDistance, vec4.yCoord * blockReachDistance,
				vec4.zCoord * blockReachDistance);
		return view.worldObj.rayTraceBlocks(vec3, vec5, false, false, true);
	}

	static Vec3 getVectorForRotation(final float pitch, final float yaw) {
		final float f = MathHelper.cos(-yaw * 0.017453292f - 3.1415927f);
		final float f2 = MathHelper.sin(-yaw * 0.017453292f - 3.1415927f);
		final float f3 = -MathHelper.cos(-pitch * 0.017453292f);
		final float f4 = MathHelper.sin(-pitch * 0.017453292f);
		return new Vec3((double) (f2 * f3), (double) f4, (double) (f * f3));
	}

	@SuppressWarnings("unchecked")
	public boolean couldHit(Entity hitEntity, float partialTicks, float currentYaw, float currentPitch,
			float yawSpeed, float pitchSpeed) {
		new RotationUtil();
		Vec3 positionEyes = mc.thePlayer.getPositionEyes(partialTicks);
		float f11 = hitEntity.getCollisionBorderSize();
		double ex = MathHelper.clamp_double(positionEyes.xCoord, hitEntity.getEntityBoundingBox().minX - (double) f11,
				hitEntity.getEntityBoundingBox().maxX + (double) f11);
		double ey = MathHelper.clamp_double(positionEyes.yCoord, hitEntity.getEntityBoundingBox().minY - (double) f11,
				hitEntity.getEntityBoundingBox().maxY + (double) f11);
		double ez = MathHelper.clamp_double(positionEyes.zCoord, hitEntity.getEntityBoundingBox().minZ - (double) f11,
				hitEntity.getEntityBoundingBox().maxZ + (double) f11);
		double x = ex - mc.thePlayer.posX;
		double y = ey - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());
		double z = ez - mc.thePlayer.posZ;
		float calcYaw = (float) (MathHelper.func_181159_b(z, x) * 180.0 / Math.PI - 90.0);
		float calcPitch = (float) (-(MathHelper.func_181159_b(y, (double) MathHelper.sqrt_double(x * x + z * z)) * 180.0
				/ Math.PI));
		float yaw = RotationUtil.updateRotation(currentYaw, calcYaw, 180.0F);
		float pitch = RotationUtil.updateRotation(currentPitch, calcPitch, 180.0F);
		MovingObjectPosition objectMouseOver = null;
		Entity entity = mc.getRenderViewEntity();
		if (entity != null && mc.theWorld != null) {
			mc.mcProfiler.startSection("pick");
			mc.pointedEntity = null;
			double d0 = (double) mc.playerController.getBlockReachDistance();
			objectMouseOver = customRayTrace(d0, partialTicks, yaw, pitch);
			double d1 = d0;
			Vec3 vec3 = entity.getPositionEyes(partialTicks);
			boolean flag = false;
			if (mc.playerController.extendedReach()) {
				d0 = 6.0;
				d1 = 6.0;
			} else {
				if (d0 > 3.0) {
					flag = true;
				}
			}

			if (objectMouseOver != null) {
				d1 = objectMouseOver.hitVec.distanceTo(vec3);
			}

			Vec3 vec31 = getCustomLook(partialTicks, yaw, pitch);
			Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
			Entity pointedEntity = null;
			Vec3 vec33 = null;
			float f = 1.0F;
			List<Entity> list = mc.theWorld.getEntitiesInAABBexcluding(entity,
					entity.getEntityBoundingBox().addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0)
							.expand((double) f, (double) f, (double) f),
					Predicates.and(EntitySelectors.NOT_SPECTATING));
			double d2 = d1;
			for (int i = 0; i < list.size(); ++i) {
				Entity entity1 = (Entity) list.get(i);
				float f1 = entity1.getCollisionBorderSize();
				AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand((double) f1, (double) f1,
						(double) f1);
				MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
				if (axisalignedbb.isVecInside(vec3)) {
					if (d2 >= 0.0) {
						pointedEntity = entity1;
						vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
						d2 = 0.0;
					}
				} else if (movingobjectposition != null) {
					double d3 = vec3.distanceTo(movingobjectposition.hitVec);
					if (d3 < d2 || d2 == 0.0) {
						boolean flag2 = false;

						if (entity1 != entity.ridingEntity || flag2) {
							pointedEntity = entity1;
							vec33 = movingobjectposition.hitVec;
							d2 = d3;
						} else if (d2 == 0.0) {
							pointedEntity = entity1;
							vec33 = movingobjectposition.hitVec;
						}
					}
				}
			}

			if (pointedEntity != null && flag && vec3.distanceTo(vec33) > 3.0) {
				pointedEntity = null;
				objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec33, null,
						new BlockPos(vec33));
			}

			if (pointedEntity != null && (d2 < d1 || objectMouseOver == null)) {
				objectMouseOver = new MovingObjectPosition(pointedEntity, vec33);
				if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame) {
					;
				}
			}
		}

		return objectMouseOver != null && objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY
				&& objectMouseOver.entityHit.getEntityId() == hitEntity.getEntityId();
	}

	public Vec3 getCustomLook(float partialTicks, float yaw, float pitch) {
		if (partialTicks != 1.0F && partialTicks != 2.0F) {
			float f = mc.thePlayer.prevRotationPitch
					+ (mc.thePlayer.rotationPitch - mc.thePlayer.prevRotationPitch) * partialTicks;
			float f1 = mc.thePlayer.prevRotationYaw
					+ (mc.thePlayer.rotationYaw - mc.thePlayer.prevRotationYaw) * partialTicks;
			return getVectorForRotation(f, f1);
		} else {
			return getVectorForRotation(pitch, yaw);
		}
	}

	public MovingObjectPosition customRayTrace(double blockReachDistance, float partialTicks, float yaw,
			float pitch) {
		Vec3 vec3 = mc.thePlayer.getPositionEyes(partialTicks);
		Vec3 vec31 = getCustomLook(partialTicks, yaw, pitch);
		Vec3 vec32 = vec3.addVector(vec31.xCoord * blockReachDistance, vec31.yCoord * blockReachDistance,
				vec31.zCoord * blockReachDistance);
		return mc.thePlayer.worldObj.rayTraceBlocks(vec3, vec32, false, false, true);
	}

	@SuppressWarnings("unchecked")
	public MovingObjectPosition rayCast(float partialTicks, float[] rots) {
		MovingObjectPosition objectMouseOver = null;
		Entity entity = mc.getRenderViewEntity();
		if (entity != null && mc.theWorld != null) {
			mc.mcProfiler.startSection("pick");
			mc.pointedEntity = null;
			double d0 = (double) mc.playerController.getBlockReachDistance();
			objectMouseOver = customRayTrace(d0, partialTicks, rots[0], rots[1]);
			double d1 = d0;
			Vec3 vec3 = entity.getPositionEyes(partialTicks);
			boolean flag = false;
			if (mc.playerController.extendedReach()) {
				d0 = 6.0;
				d1 = 6.0;
			} else {
				if (d0 > 3.0) {
					flag = true;
				}

			}

			if (objectMouseOver != null) {
				d1 = objectMouseOver.hitVec.distanceTo(vec3);
			}

			Vec3 vec31 = getCustomLook(partialTicks, rots[0], rots[1]);
			Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
			Entity pointedEntity = null;
			Vec3 vec33 = null;
			float f = 1.0F;
			List<Entity> list = mc.theWorld.getEntitiesInAABBexcluding(entity,
					entity.getEntityBoundingBox().addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0)
							.expand((double) f, (double) f, (double) f),
					Predicates.and(EntitySelectors.NOT_SPECTATING));
			double d2 = d1;
			for (int i = 0; i < list.size(); ++i) {
				Entity entity1 = (Entity) list.get(i);
				float f1 = entity1.getCollisionBorderSize();
				AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand((double) f1, (double) f1,
						(double) f1);
				MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
				if (axisalignedbb.isVecInside(vec3)) {
					if (d2 >= 0.0) {
						pointedEntity = entity1;
						vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
						d2 = 0.0;
					}
				} else if (movingobjectposition != null) {
					double d3 = vec3.distanceTo(movingobjectposition.hitVec);
					if (d3 < d2 || d2 == 0.0) {
						boolean flag2 = false;
						if (entity1 != entity.ridingEntity || flag2) {
							pointedEntity = entity1;
							vec33 = movingobjectposition.hitVec;
							d2 = d3;
						} else if (d2 == 0.0) {
							pointedEntity = entity1;
							vec33 = movingobjectposition.hitVec;
						}
					}
				}
			}

			if (pointedEntity != null && flag && vec3.distanceTo(vec33) > 3.0) {
				pointedEntity = null;
				objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec33, null,
						new BlockPos(vec33));
			}

			if (pointedEntity != null && (d2 < d1 || objectMouseOver == null)) {
				objectMouseOver = new MovingObjectPosition(pointedEntity, vec33);
				if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame) {
					;
				}
			}
		}

		return objectMouseOver;
	}

	public void aimAt(float pitch, float yaw, float fuckedYaw, float fuckedPitch, double speed) {
		float[] gcd = getGCDRotations(new float[] { yaw, pitch + ((int) fuckedPitch / 360) * 360 },
				new float[] { mc.thePlayer.prevRotationYaw, mc.thePlayer.prevRotationPitch });
		float cappedYaw = maxAngleChange(mc.thePlayer.prevRotationYaw, gcd[0], (float) speed);
		float cappedPitch = maxAngleChange(mc.thePlayer.prevRotationPitch, gcd[1], (float) speed);
		mc.thePlayer.rotationPitch = cappedPitch;
		mc.thePlayer.rotationYaw = cappedYaw;
	}

	public float[] getGCDRotations(final float[] rotations, final float[] prevRots) {
		final float yawDif = rotations[0] - prevRots[0];
		final float pitchDif = rotations[1] - prevRots[1];
		final double gcd = getGCD();

		rotations[0] -= yawDif % gcd;
		rotations[1] -= pitchDif % gcd;
		return rotations;
	}

	public float maxAngleChange(final float prev, final float now, final float maxTurn) {
		float dif = MathHelper.wrapAngleTo180_float(now - prev);
		if (dif > maxTurn)
			dif = maxTurn;
		if (dif < -maxTurn)
			dif = -maxTurn;
		return prev + dif;
	}

	public double getGCD() {
		final float sens = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
		final float pow = sens * sens * sens * 8.0F;
		return pow * 0.15D;
	}
	
	public double isBestTarget(Entity entity) {
		if (entity instanceof EntityLivingBase) {
			double distance = mc.thePlayer.getDistanceToEntity(entity);
			double health = ((EntityLivingBase) entity).getHealth();
			double hurtTime = 10.0;
			if (entity instanceof EntityPlayer) {
				hurtTime = ((EntityPlayer) entity).hurtTime;
			}

			return distance * 2.0 + health + hurtTime * 4.0;
		} else {
			return 1000.0;
		}
	}

	public double isUnknownTarget(Entity entity) {
		Targets aim = (Targets) Haru.instance.getModuleManager().getModule(Targets.class);

		if (!(entity instanceof EntityLivingBase)
				|| (!couldHit(entity, 1.0F, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, 180.0F,
						180.0F) || aim.getDistance().getInput() != 3.0) && aim.getDistance().getInput() == 3.0) {
			return 1000.0;
		} else {
			double distance = mc.thePlayer.getDistanceToEntity(entity);
			double hurtTime = ((EntityLivingBase) entity).hurtTime * 6;
			return hurtTime + distance;
		}
	}
	
	public double getDistanceToEntity(EntityLivingBase entity) {
		Vec3 playerVec = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(),
				mc.thePlayer.posZ);
		double yDiff = mc.thePlayer.posY - entity.posY;
		double targetY = yDiff > 0 ? entity.posY + entity.getEyeHeight()
				: -yDiff < mc.thePlayer.getEyeHeight() ? mc.thePlayer.posY + mc.thePlayer.getEyeHeight() : entity.posY;
		Vec3 targetVec = new Vec3(entity.posX, targetY, entity.posZ);
		return playerVec.distanceTo(targetVec) - 0.3F;
	}

	public boolean isATeamMate(Entity entity) {
		EntityPlayer teamMate = (EntityPlayer) entity;
		if (mc.thePlayer.isOnSameTeam((EntityLivingBase) entity) || mc.thePlayer.getDisplayName().getUnformattedText()
				.startsWith(teamMate.getDisplayName().getUnformattedText().substring(0, 2)))
			return true;
		return false;
	}
	
	public EntityPlayer getTarget() {
		Targets aim = (Targets) Haru.instance.getModuleManager().getModule(Targets.class);

		ArrayList<EntityPlayer> entities = mc.theWorld.loadedEntityList.stream()
		        .filter(entity -> entity instanceof EntityPlayer && entity != mc.thePlayer)
		        .map(entity -> (EntityPlayer) entity)
		        .filter(this::isValidTarget)
		        .collect(Collectors.toCollection(ArrayList::new));

		switch (aim.getSortMode().getMode()) {
		case "Distance":
			entities.sort((entity1, entity2) -> (int) (entity1.getDistanceToEntity(mc.thePlayer) * 1000 - entity2.getDistanceToEntity(mc.thePlayer) * 1000));
			break;
		case "Fov":
			entities.sort(Comparator.comparingDouble(entity -> (RotationUtil.getDistanceAngles(mc.thePlayer.rotationPitch, RotationUtil.getRotations(entity)[0]))));
			break;
		case "Angle":
			entities.sort((entity1, entity2) -> {
				float[] rot1 = RotationUtil.getRotations(entity1);
				float[] rot2 = RotationUtil.getRotations(entity2);
				return (int) ((mc.thePlayer.rotationYaw - rot1[0]) - (mc.thePlayer.rotationYaw - rot2[0]));
			});
			break;
		case "Health":
			entities.sort((entity1, entity2) -> (int) (entity1.getHealth() - entity2.getHealth()));
			break;
		case "Armor":
			entities.sort(Comparator.comparingInt(entity -> (entity instanceof EntityPlayer ? ((EntityPlayer) entity).inventory.getTotalArmorValue() : (int) entity.getHealth())));
			break;
		case "Best":
			entities.sort((entity1, entity2) -> (int) (isBestTarget(entity1) - isBestTarget(entity2)));
			break;
		case "Unknown":
			entities.sort((entity1, entity2) -> (int) (isUnknownTarget(entity1) - isUnknownTarget(entity2)));
			break;
		}

		List<EntityPlayer> list = entities.subList(0, Math.min(entities.size(), aim.getMultiTarget().getInputToInt()));
		return list.isEmpty() ? null : list.get(0);
	}

	public boolean isValidTarget(EntityPlayer ep) {
		Targets aim = (Targets) Haru.instance.getModuleManager().getModule(Targets.class);

		if (ep == mc.thePlayer && ep.isDead) {
			return false;
		}

		if (!(mc.thePlayer.getDistanceToEntity(ep) < aim.getDistance().getInput())) {
			return false;
		}

		if (!aim.getFriends().isToggled() && FriendUtil.instance.isAFriend(ep)) {
			return false;
		}

		if (!aim.getTeams().isToggled() && isATeamMate(ep)) {
			return false;
		}

		if (!aim.getBots().isToggled() && AntiBot.bot(ep)) {
			return false;
		}

		if (!aim.getInvis().isToggled() && ep.isInvisible()) {
			return false;
		}

		if (!aim.getNaked().isToggled() && !PlayerUtil.isPlayerNaked(ep)) {
			return false;
		}

		if (!PlayerUtil.fov(ep, aim.getFov().getInputToFloat())) {
			return false;
		}

		return true;
	}
}
