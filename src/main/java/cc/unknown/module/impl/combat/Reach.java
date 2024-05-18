package cc.unknown.module.impl.combat;

import java.util.List;

import org.lwjgl.input.Mouse;

import cc.unknown.Haru;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.event.impl.other.MouseEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.helpers.MathHelper;
import cc.unknown.utils.misc.ClickUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

@Register(name = "Reach", category = Category.Combat)
public class Reach extends Module {
	private DoubleSliderValue rangeCombat = new DoubleSliderValue("Range", 3, 3, 2.9, 6, 0.01);
	private SliderValue chance = new SliderValue("Chance", 100, 0, 100, 1);
	private BooleanValue weapon_only = new BooleanValue("Only Weapon", false);
	private BooleanValue moving_only = new BooleanValue("Only Move", false);
	private BooleanValue sprint_only = new BooleanValue("Only Sprint", false);
	private BooleanValue speed_only = new BooleanValue("Only Speed Potion", false);
	private BooleanValue hit_through_blocks = new BooleanValue("Hit through blocks", false);

	public Reach() {
		this.registerSetting(rangeCombat, chance, weapon_only, moving_only, sprint_only, speed_only, hit_through_blocks);
	}
	
	@EventLink
	public void onGui(ClickGuiEvent e) {
		this.setSuffix("- [" + rangeCombat.getInputMin() + ", " + rangeCombat.getInputMax() + "]");
	}
		
	@EventLink
	public void onMouse(MouseEvent e) {
		AutoClick clicker = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);
		if (PlayerUtil.inGame() && e.getButton() == 0 && (!clicker.isEnabled() || !Mouse.isButtonDown(0)) || ClickUtil.instance.isClicking()) {
			callReach();
		}
	}

	private boolean callReach() {
		if (!PlayerUtil.inGame()) {
			return false;
		} else if (moving_only.isToggled() && (double) mc.thePlayer.moveForward == 0.0D
				&& (double) mc.thePlayer.moveStrafing == 0.0D) {
			return false;
		} else if (weapon_only.isToggled() && !PlayerUtil.isHoldingWeapon()) {
			return false;
		} else if (sprint_only.isToggled() && !mc.thePlayer.isSprinting()) {
			return false;
		} else if (speed_only.isToggled() && !mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
			return false;
		} else if (!(chance.getInput() == 100 || Math.random() <= chance.getInput() / 100)) {
			return false;
		} else {
			if (!hit_through_blocks.isToggled() && mc.objectMouseOver != null) {
				BlockPos p = mc.objectMouseOver.getBlockPos();
				if (p != null && mc.theWorld.getBlockState(p).getBlock() != Blocks.air) {
					return false;
				}
			}

			double reach = ClickUtil.instance.ranModuleVal(rangeCombat, MathHelper.rand());
			Object[] object = findEntitiesWithinReach(reach);
			if (object == null) {
				return false;
			} else {
				Entity en = (Entity) object[0];
				mc.objectMouseOver = new MovingObjectPosition(en, (Vec3) object[1]);
				mc.pointedEntity = en;
				return true;
			}
		}
	}

	private Object[] findEntitiesWithinReach(double reach) {
		Reach reich = (Reach) Haru.instance.getModuleManager().getModule(Reach.class);

		if (!reich.isEnabled()) {
			reach = mc.playerController.extendedReach() ? 6.0D : 3.0D;
		}

		Entity renderView = mc.getRenderViewEntity();
		Entity target = null;
		if (renderView == null) {
			return null;
		} else {
			mc.mcProfiler.startSection("pick");
			Vec3 eyePosition = renderView.getPositionEyes(1.0F);
			Vec3 playerLook = renderView.getLook(1.0F);
			Vec3 reachTarget = eyePosition.addVector(playerLook.xCoord * reach, playerLook.yCoord * reach,
					playerLook.zCoord * reach);
			Vec3 targetHitVec = null;
			List<Entity> targetsWithinReach = mc.theWorld.getEntitiesWithinAABBExcludingEntity(renderView,
					renderView.getEntityBoundingBox()
							.addCoord(playerLook.xCoord * reach, playerLook.yCoord * reach, playerLook.zCoord * reach)
							.expand(1.0D, 1.0D, 1.0D));
			double adjustedReach = reach;

			for (Entity entity : targetsWithinReach) {
				if (entity.canBeCollidedWith()) {
					float ex = (float) ((double) entity.getCollisionBorderSize());
					AxisAlignedBB entityBoundingBox = entity.getEntityBoundingBox().expand(ex, ex, ex);
					MovingObjectPosition targetPosition = entityBoundingBox.calculateIntercept(eyePosition,
							reachTarget);
					if (entityBoundingBox.isVecInside(eyePosition)) {
						if (0.0D < adjustedReach || adjustedReach == 0.0D) {
							target = entity;
							targetHitVec = targetPosition == null ? eyePosition : targetPosition.hitVec;
							adjustedReach = 0.0D;
						}
					} else if (targetPosition != null) {
						double distanceToVec = eyePosition.distanceTo(targetPosition.hitVec);
						if (distanceToVec < adjustedReach || adjustedReach == 0.0D) {
							if (entity == renderView.ridingEntity) {
								if (adjustedReach == 0.0D) {
									target = entity;
									targetHitVec = targetPosition.hitVec;
								}
							} else {
								target = entity;
								targetHitVec = targetPosition.hitVec;
								adjustedReach = distanceToVec;
							}
						}
					}
				}
			}

			if (adjustedReach < reach && !(target instanceof EntityLivingBase)
					&& !(target instanceof EntityItemFrame)) {
				target = null;
			}

			mc.mcProfiler.endSection();
			if (target != null && targetHitVec != null) {
				return new Object[] { target, targetHitVec };
			} else {
				return null;
			}
		}
	}
}
