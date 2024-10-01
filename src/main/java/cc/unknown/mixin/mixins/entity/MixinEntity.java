package cc.unknown.mixin.mixins.entity;

import java.util.List;
import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import cc.unknown.Haru;
import cc.unknown.event.impl.move.MoveEvent;
import cc.unknown.event.impl.player.StrafeEvent;
import cc.unknown.utils.Loona;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;

@Mixin(Entity.class)
public abstract class MixinEntity implements Loona {

	@Shadow
	public float rotationYaw;
	@Shadow
	public float rotationPitch;
	@Shadow
	public double motionX;
	@Shadow
	public double motionZ;
	@Shadow
	public double motionY;
	@Shadow
	public double posX;
	@Shadow
	public double posY;
	@Shadow
	public double posZ;
	@Shadow
	public float fallDistance;
	@Shadow
	public boolean noClip;
	@Shadow
	public World worldObj;
	@Shadow
	protected boolean isInWeb;

	@Shadow
	public abstract void setSprinting(boolean sprinting);

	@Shadow
	public abstract boolean isSneaking();

	@Shadow
	public float stepHeight;
	@Shadow
	public boolean isCollidedHorizontally;
	@Shadow
	public boolean isCollidedVertically;
	@Shadow
	public boolean isCollided;

	@Shadow
	protected abstract void updateFallState(double p_updateFallState_1_, boolean p_updateFallState_3_,
			Block p_updateFallState_4_, BlockPos p_updateFallState_5_);

	@Shadow
	protected abstract boolean canTriggerWalking();

	@Shadow
	public abstract float getDistanceToEntity(Entity entityIn);

	@Shadow
	public abstract void setEntityBoundingBox(AxisAlignedBB p_setEntityBoundingBox_1_);

	@Shadow
	public abstract AxisAlignedBB getEntityBoundingBox();

	@Shadow
	protected abstract void resetPositionToBB();

	@Shadow
	public Entity ridingEntity;
	@Shadow
	public float distanceWalkedModified;
	@Shadow
	public float distanceWalkedOnStepModified;
	@Shadow
	private int nextStepDistance;

	@Shadow
	public abstract boolean isInWater();

	@Shadow
	public abstract void playSound(String p_playSound_1_, float p_playSound_2_, float p_playSound_3_);

	@Shadow
	protected abstract String getSwimSound();

	@Shadow
	protected Random rand;

	@Shadow
	protected abstract void playStepSound(BlockPos p_playStepSound_1_, Block p_playStepSound_2_);

	@Shadow
	protected abstract void doBlockCollisions();

	@Shadow
	public abstract void addEntityCrashInfo(CrashReportCategory p_addEntityCrashInfo_1_);

	@Shadow
	public abstract boolean isWet();

	@Shadow
	protected abstract void dealFireDamage(int p_dealFireDamage_1_);

	@Shadow
	private int fire;

	@Shadow
	public abstract void setFire(int p_setFire_1_);

	@Shadow
	public abstract boolean isSprinting();

	@Shadow
	public boolean isAirBorne;
	@Shadow
	public int fireResistance;
	@Shadow
	public boolean onGround;

    @Overwrite
    public void moveFlying(float p_moveFlying_1_, float p_moveFlying_2_, float p_moveFlying_3_) {
        float yaw = this.rotationYaw;
        if((Object) this == Minecraft.getMinecraft().thePlayer) {
            StrafeEvent e = new StrafeEvent(p_moveFlying_1_, p_moveFlying_2_, p_moveFlying_3_, this.rotationYaw);
            Haru.instance.getEventBus().post(e);
            if (e.isCancelled()) {
                return;
            }
            p_moveFlying_1_ = e.getStrafe();
            p_moveFlying_2_ = e.getForward();
            p_moveFlying_3_ = e.getFriction();
            yaw = e.getYaw();
        }

        float f = p_moveFlying_1_ * p_moveFlying_1_ + p_moveFlying_2_ * p_moveFlying_2_;
        if (f >= 1.0E-4F) {
            f = MathHelper.sqrt_float(f);
            if (f < 1.0F) {
                f = 1.0F;
            }

            f = p_moveFlying_3_ / f;
            p_moveFlying_1_ *= f;
            p_moveFlying_2_ *= f;
            float f1 = MathHelper.sin(yaw * 3.1415927F / 180.0F);
            float f2 = MathHelper.cos(yaw * 3.1415927F / 180.0F);
            this.motionX += (double)(p_moveFlying_1_ * f2 - p_moveFlying_2_ * f1);
            this.motionZ += (double)(p_moveFlying_2_ * f2 + p_moveFlying_1_ * f1);
        }
    }

	@Overwrite
	public void moveEntity(double x, double y, double z) {
		MoveEvent e = new MoveEvent(x, y, z);
		if ((Object) this instanceof EntityPlayerSP) {
			Haru.instance.getEventBus().post(e);
			x = e.getMotionX();
			y = e.getMotionY();
			z = e.getMotionZ();
			if (e.isCancelled()) {
				return;
			}
		}

		if (this.noClip) {
			this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, y, z));
			this.resetPositionToBB();
		} else {
			this.worldObj.theProfiler.startSection("move");
			double d0 = this.posX;
			double d1 = this.posY;
			double d2 = this.posZ;

			if (this.isInWeb) {
				this.isInWeb = false;
				x *= 0.25D;
				y *= 0.05000000074505806D;
				z *= 0.25D;
				this.motionX = 0.0D;
				this.motionY = 0.0D;
				this.motionZ = 0.0D;
			}

			double d3 = x;
			double d4 = y;
			double d5 = z;
			boolean sneak = this.isSneaking();
			
			if ((Object) this instanceof EntityPlayerSP && e.isDisableSneak()) {
				sneak = false;
			}

			boolean flag = (this.onGround && sneak || e.isSaveWalk()) && (Object) this instanceof EntityPlayer;

			if (flag) {
				double d6;

				for (d6 = 0.05; x != 0 && worldObj
						.getCollidingBoundingBoxes((Entity) (Object) this, getEntityBoundingBox().offset(x, -1, 0))
						.isEmpty(); d3 = x) {
					if (x < d6 && x >= -d6) {
						x = 0;
					} else if (x > 0) {
						x -= d6;
					} else {
						x += d6;
					}
				}

				for (; z != 0 && worldObj
						.getCollidingBoundingBoxes((Entity) (Object) this, getEntityBoundingBox().offset(0, -1, z))
						.isEmpty(); d5 = z) {
					if (z < d6 && z >= -d6) {
						z = 0;
					} else if (z > 0) {
						z -= d6;
					} else {
						z += d6;
					}
				}

				for (; x != 0 && z != 0 && worldObj
						.getCollidingBoundingBoxes((Entity) (Object) this, getEntityBoundingBox().offset(x, -1, z))
						.isEmpty(); d5 = z) {
					if (x < d6 && x >= -d6) {
						x = 0;
					} else if (x > 0) {
						x -= d6;
					} else {
						x += d6;
					}

					d3 = x;

					if (z < d6 && z >= -d6) {
						z = 0;
					} else if (z > 0) {
						z -= d6;
					} else {
						z += d6;
					}
				}
			}

			List<AxisAlignedBB> list1 = worldObj.getCollidingBoundingBoxes((Entity) (Object) this,
					getEntityBoundingBox().addCoord(x, y, z));
			AxisAlignedBB axisalignedbb = getEntityBoundingBox();

			for (AxisAlignedBB axisalignedbb1 : list1) {
				y = axisalignedbb1.calculateYOffset(this.getEntityBoundingBox(), y);
			}

			this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, y, 0.0D));
			boolean flag1 = this.onGround || d4 != y && d4 < 0.0D;

			for (AxisAlignedBB axisalignedbb2 : list1) {
				x = axisalignedbb2.calculateXOffset(this.getEntityBoundingBox(), x);
			}

			this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, 0.0D, 0.0D));

			for (AxisAlignedBB axisalignedbb13 : list1) {
				z = axisalignedbb13.calculateZOffset(this.getEntityBoundingBox(), z);
			}

			this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, 0.0D, z));

			if (this.stepHeight > 0.0F && flag1 && (d3 != x || d5 != z)) {
				double d11 = x;
				double d7 = y;
				double d8 = z;
				AxisAlignedBB axisalignedbb3 = this.getEntityBoundingBox();
				this.setEntityBoundingBox(axisalignedbb);
				y = (double) this.stepHeight;
				List<AxisAlignedBB> list = worldObj.getCollidingBoundingBoxes((Entity) (Object) this,
						getEntityBoundingBox().addCoord(d3, y, d5));
				AxisAlignedBB axisalignedbb4 = this.getEntityBoundingBox();
				AxisAlignedBB axisalignedbb5 = axisalignedbb4.addCoord(d3, 0.0D, d5);
				double d9 = y;

				for (AxisAlignedBB axisalignedbb6 : list) {
					d9 = axisalignedbb6.calculateYOffset(axisalignedbb5, d9);
				}

				axisalignedbb4 = axisalignedbb4.offset(0.0D, d9, 0.0D);
				double d15 = d3;

				for (AxisAlignedBB axisalignedbb7 : list) {
					d15 = axisalignedbb7.calculateXOffset(axisalignedbb4, d15);
				}

				axisalignedbb4 = axisalignedbb4.offset(d15, 0.0D, 0.0D);
				double d16 = d5;

				for (AxisAlignedBB axisalignedbb8 : list) {
					d16 = axisalignedbb8.calculateZOffset(axisalignedbb4, d16);
				}

				axisalignedbb4 = axisalignedbb4.offset(0.0D, 0.0D, d16);
				AxisAlignedBB axisalignedbb14 = this.getEntityBoundingBox();
				double d17 = y;

				for (AxisAlignedBB axisalignedbb9 : list) {
					d17 = axisalignedbb9.calculateYOffset(axisalignedbb14, d17);
				}

				axisalignedbb14 = axisalignedbb14.offset(0.0D, d17, 0.0D);
				double d18 = d3;

				for (AxisAlignedBB axisalignedbb10 : list) {
					d18 = axisalignedbb10.calculateXOffset(axisalignedbb14, d18);
				}

				axisalignedbb14 = axisalignedbb14.offset(d18, 0.0D, 0.0D);
				double d19 = d5;

				for (AxisAlignedBB axisalignedbb11 : list) {
					d19 = axisalignedbb11.calculateZOffset(axisalignedbb14, d19);
				}

				axisalignedbb14 = axisalignedbb14.offset(0.0D, 0.0D, d19);
				double d20 = d15 * d15 + d16 * d16;
				double d10 = d18 * d18 + d19 * d19;

				if (d20 > d10) {
					x = d15;
					z = d16;
					y = -d9;
					this.setEntityBoundingBox(axisalignedbb4);
				} else {
					x = d18;
					z = d19;
					y = -d17;
					this.setEntityBoundingBox(axisalignedbb14);
				}

				for (AxisAlignedBB axisalignedbb12 : list) {
					y = axisalignedbb12.calculateYOffset(this.getEntityBoundingBox(), y);
				}

				this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, y, 0.0D));

				if (d11 * d11 + d8 * d8 >= x * x + z * z) {
					x = d11;
					y = d7;
					z = d8;
					this.setEntityBoundingBox(axisalignedbb3);
				}
			}

			this.worldObj.theProfiler.endSection();
			this.worldObj.theProfiler.startSection("rest");
			this.resetPositionToBB();
			this.isCollidedHorizontally = d3 != x || d5 != z;
			this.isCollidedVertically = d4 != y;
			this.onGround = this.isCollidedVertically && d4 < 0.0D;
			this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;
			int i = MathHelper.floor_double(this.posX);
			int j = MathHelper.floor_double(this.posY - 0.20000000298023224D);
			int k = MathHelper.floor_double(this.posZ);
			BlockPos blockpos = new BlockPos(i, j, k);
			Block block1 = this.worldObj.getBlockState(blockpos).getBlock();

			if (block1.getMaterial() == Material.air) {
				Block block = this.worldObj.getBlockState(blockpos.down()).getBlock();

				if (block instanceof BlockFence || block instanceof BlockWall || block instanceof BlockFenceGate) {
					block1 = block;
					blockpos = blockpos.down();
				}
			}

			this.updateFallState(y, this.onGround, block1, blockpos);

			if (d3 != x) {
				this.motionX = 0.0D;
			}

			if (d5 != z) {
				this.motionZ = 0.0D;
			}

			if (d4 != y) {
				block1.onLanded(worldObj, (Entity) (Object) this);
			}

			if (this.canTriggerWalking() && !flag && this.ridingEntity == null) {
				double d12 = this.posX - d0;
				double d13 = this.posY - d1;
				double d14 = this.posZ - d2;

				if (block1 != Blocks.ladder) {
					d13 = 0.0D;
				}

				if (block1 != null && this.onGround) {
					block1.onEntityCollidedWithBlock(worldObj, blockpos, (Entity) (Object) this);
				}

				this.distanceWalkedModified = (float) ((double) this.distanceWalkedModified
						+ (double) MathHelper.sqrt_double(d12 * d12 + d14 * d14) * 0.6D);
				this.distanceWalkedOnStepModified = (float) ((double) this.distanceWalkedOnStepModified
						+ (double) MathHelper.sqrt_double(d12 * d12 + d13 * d13 + d14 * d14) * 0.6D);

				if (this.distanceWalkedOnStepModified > (float) this.nextStepDistance
						&& block1.getMaterial() != Material.air) {
					this.nextStepDistance = (int) this.distanceWalkedOnStepModified + 1;

					if (this.isInWater()) {
						float f = MathHelper.sqrt_double(this.motionX * this.motionX * 0.20000000298023224D
								+ this.motionY * this.motionY + this.motionZ * this.motionZ * 0.20000000298023224D)
								* 0.35F;

						if (f > 1.0F) {
							f = 1.0F;
						}

						this.playSound(this.getSwimSound(), f,
								1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
					}

					this.playStepSound(blockpos, block1);
				}
			}

			try {
				this.doBlockCollisions();
			} catch (Throwable throwable) {
				CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
				CrashReportCategory crashreportcategory = crashreport
						.makeCategory("Entity being checked for collision");
				this.addEntityCrashInfo(crashreportcategory);
				throw new ReportedException(crashreport);
			}

			boolean flag2 = this.isWet();

			if (this.worldObj.isFlammableWithin(this.getEntityBoundingBox().contract(0.001D, 0.001D, 0.001D))) {
				this.dealFireDamage(1);

				if (!flag2) {
					++this.fire;

					if (this.fire == 0) {
						this.setFire(8);
					}
				}
			} else if (this.fire <= 0) {
				this.fire = -this.fireResistance;
			}

			if (flag2 && this.fire > 0) {
				this.playSound("random.fizz", 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
				this.fire = -this.fireResistance;
			}

			this.worldObj.theProfiler.endSection();
		}
	}
}
