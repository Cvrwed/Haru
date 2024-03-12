package cc.unknown.module.impl.combat;

import java.util.List;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.UpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.AdvancedTimer;
import cc.unknown.utils.player.CombatUtil;
import cc.unknown.utils.player.CombatUtil.IEntityFilter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class AutoRod extends Module {

	private AdvancedTimer pushTimer = new AdvancedTimer(1);
	private AdvancedTimer rodPullTimer = new AdvancedTimer(1);
	private boolean rodInUse = false;
	private int switchBack;

	private BooleanValue facingEnemy = new BooleanValue("Check enemy", true);
	private SliderValue enemyDistance = new SliderValue("Distance enemy", 8, 1, 10, 1);
	private SliderValue pushDelay = new SliderValue("Push delay", 100, 50, 1000, 50);
	private SliderValue pullbackDelay = new SliderValue("Pullback delay", 500, 50, 1000, 50);

	public AutoRod() {
		super("AutoRod", ModuleCategory.Combat);
		this.registerSetting(facingEnemy, enemyDistance, pushDelay, pullbackDelay);
	}
	
	@Override
	public void onDisable() {
	    switchBack = mc.thePlayer.inventory.currentItem;
	}

	@EventLink
	public void onUpdate(UpdateEvent e) {
	    if ((mc.thePlayer.isUsingItem() && mc.thePlayer.getHeldItem().getItem() == Items.fishing_rod) || rodInUse) {
	        if (rodPullTimer.hasTimeElapsed(pullbackDelay.getInputToLong(), true)) {
	            if (switchBack != 0 && mc.thePlayer.inventory.currentItem != switchBack) {
	                mc.thePlayer.inventory.currentItem = switchBack;
	                mc.playerController.updateController();
	            } else {
	                mc.thePlayer.stopUsingItem();
	            }
	            switchBack = 0;
	            rodInUse = false;
	        }
	    } else {
	        boolean shouldUseRod = facingEnemy.isToggled() ? isFacingEnemy() : true;
	        if (shouldUseRod && pushTimer.hasTimeElapsed(pushDelay.getInputToLong(), true)) {
	            int rodSlot = findRod();
	            if (rodSlot != -1) {
	                mc.thePlayer.inventory.currentItem = rodSlot;
	                mc.playerController.updateController();
	                rod();
	            }
	        }
	    }
	}

	private boolean isFacingEnemy() {
	    Entity facingEntity = mc.objectMouseOver != null ? mc.objectMouseOver.entityHit : null;

	    if (facingEntity != null && CombatUtil.instance.canTarget(facingEntity)) {
	        return true;
	    }
	    
	    return false;
	}

	private void rod() {
	    int rodSlot = findRod();
	    if (rodSlot != -1) {
	        mc.thePlayer.inventory.currentItem = rodSlot;
	        mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getStackInSlot(rodSlot));
	        rodInUse = true;
	        rodSlot = mc.thePlayer.inventory.currentItem;
	    }
	}

	private int findRod() {
	    for (int slot = 1; slot <= 9; slot++) {
	        ItemStack itemInSlot = mc.thePlayer.inventory.getStackInSlot(slot);
	        if (itemInSlot != null && itemInSlot.getItem() instanceof ItemFishingRod) {
	            return slot;
	        }
	    }
	    return -1;
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
