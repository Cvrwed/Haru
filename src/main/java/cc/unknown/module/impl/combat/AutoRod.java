package cc.unknown.module.impl.combat;

import java.util.List;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.PostUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.AdvancedTimer;
import cc.unknown.utils.player.CombatUtil;
import cc.unknown.utils.player.CombatUtil.IEntityFilter;
import cc.unknown.utils.player.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class AutoRod extends Module {

	private AdvancedTimer pushTimer = new AdvancedTimer(1);
	private AdvancedTimer rodPullTimer = new AdvancedTimer(1);
	private boolean rodInUse = false;
	private int switchBack = 1;

	private BooleanValue facingEnemy = new BooleanValue("Check enemy", true);
	private final SliderValue enemyDistance = new SliderValue("Distance enemy", 8, 1, 10, 1);
	private final SliderValue pushDelay = new SliderValue("Push delay", 100, 50, 1000, 50);
	private final SliderValue pullbackDelay = new SliderValue("Pullback delay", 500, 50, 1000, 50);

	public AutoRod() {
		super("AutoRod", ModuleCategory.Combat);
		this.registerSetting(facingEnemy, enemyDistance, pushDelay, pullbackDelay);
	}

	@EventLink
	public void onUpdate(PostUpdateEvent event) {
		boolean usingRod = (mc.thePlayer.isUsingItem() && mc.thePlayer.getHeldItem().getItem() == Items.fishing_rod)
				|| rodInUse;
		if (usingRod) {
			if (rodPullTimer.hasTimeElapsed(pullbackDelay.getInputToLong(), true)) {
				if (switchBack != 1 && mc.thePlayer.inventory.currentItem != switchBack) {
					mc.thePlayer.inventory.currentItem = switchBack;
					mc.playerController.updateController();
				} else {
					mc.thePlayer.stopUsingItem();
				}
				switchBack = 1;
				rodInUse = false;
			}
		} else {
			boolean rod = false;
			if (facingEnemy.isToggled()) {
				Entity facingEntity = mc.objectMouseOver != null ? mc.objectMouseOver.entityHit : null;

				if (facingEntity == null) {
					facingEntity = raycastEntity(enemyDistance.getInput(), RotationUtil.getTargetRotation().getYaw(),
							RotationUtil.getTargetRotation().getPitch(),
							entity -> CombatUtil.instance.canTarget(entity));
				}

				if (CombatUtil.instance.canTarget(facingEntity)) {
					rod = true;
				}
			} else {
				rod = true;
			}

			if (rod && pushTimer.hasTimeElapsed(pushDelay.getInputToLong(), true)) {
				if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() != Items.fishing_rod) {
					int rodSlot = findRod();
					switchBack = mc.thePlayer.inventory.currentItem;
					mc.thePlayer.inventory.currentItem = rodSlot;
					mc.playerController.updateController();
				}
				rod();
			}
		}

	}

	private void rod() {
		int rod = this.findRod();
		int currentSlot = mc.thePlayer.inventory.currentItem;
		if (mc.thePlayer.inventory.getStackInSlot(currentSlot) == null) {
			return;
		}
		mc.thePlayer.inventory.currentItem = rod;
		mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld,
				mc.thePlayer.inventoryContainer.getSlot(rod).getStack());
		rodInUse = true;
	}

	private int findRod() {
		for (int i = 0; i <= 8; i++) {
			ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
			if (stack != null && stack.getItem() == Items.fishing_rod) {
				return i;
			}
		}
		return 1;
	}

	public Entity raycastEntity(double range, float yaw, float pitch, IEntityFilter entityFilter) {
		Entity renderViewEntity = mc.getRenderViewEntity();

		if (renderViewEntity == null || mc.theWorld == null)
			return null;

		double blockReachDistance = range;
		Vec3 eyePosition = renderViewEntity.getPositionEyes(1F);
		Vec3 entityLook = CombatUtil.instance.getVectorForRotation(yaw, pitch);
		final Vec3 vector = eyePosition.addVector(entityLook.xCoord * blockReachDistance,
				entityLook.yCoord * blockReachDistance, entityLook.zCoord * blockReachDistance);

		List<Entity> entityList = mc.theWorld.getEntities(Entity.class,
				e -> e != null && (e instanceof EntityLivingBase || e instanceof EntityLargeFireball)
						&& (!(e instanceof EntityPlayer) || !((EntityPlayer) e).isSpectator()) && e.canBeCollidedWith()
						&& e != renderViewEntity);

		Entity pointedEntity = null;

		for (final Entity entity : entityList) {
			if (!entityFilter.canRaycast(entity))
				continue;

			final AxisAlignedBB axisAlignedBB = entity.getEntityBoundingBox();
			final MovingObjectPosition movingObjectPosition = axisAlignedBB.calculateIntercept(eyePosition, vector);

			if (axisAlignedBB.isVecInside(eyePosition)) {
				if (blockReachDistance >= 0.0) {
					pointedEntity = entity;
					blockReachDistance = 0.0;
				}
			} else if (movingObjectPosition != null) {
				double eyeDistance = eyePosition.distanceTo(movingObjectPosition.hitVec);

				if (eyeDistance < blockReachDistance || blockReachDistance == 0.0) {
					if (entity == renderViewEntity.ridingEntity && !renderViewEntity.canRiderInteract()) {
						if (blockReachDistance == 0.0)
							pointedEntity = entity;
					} else {
						pointedEntity = entity;
						blockReachDistance = eyeDistance;
					}
				}
			}

			return pointedEntity;
		}

		return null;
	}
}
