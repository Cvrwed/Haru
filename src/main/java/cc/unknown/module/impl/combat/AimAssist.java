package cc.unknown.module.impl.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Mouse;

import cc.unknown.Haru;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.LivingEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.misc.ClickUtil;
import cc.unknown.utils.player.CombatUtil;
import cc.unknown.utils.player.FriendUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

@Register(name = "AimAssist", category = Category.Combat)
public class AimAssist extends Module {

	private SliderValue horizontalAimSpeed = new SliderValue("Horizontal Aim Speed", 45, 5, 100, 1);
	private SliderValue horizontalAimFineTuning = new SliderValue("Horizontal Aim Fine-tuning", 15, 2, 97, 1);
	private BooleanValue horizontalRandomization = new BooleanValue("Horizontal Randomization", false);
	private SliderValue horizontalRandomizationAmount = new SliderValue("Horizontal Randomization", 1.2, 0.1, 5, 0.01);
	private SliderValue fieldOfView = new SliderValue("Field of View", 90.0, 15.0, 360.0, 1.0);
	public SliderValue enemyDetectionRange = new SliderValue("Enemy Detection Range", 4.5, 1.0, 10.0, 0.5);
	private BooleanValue verticalAlignmentCheck = new BooleanValue("Vertical Alignment Check", false);
	private BooleanValue verticalRandomization = new BooleanValue("Vertical Randomization", false);
	private SliderValue verticalRandomizationAmount = new SliderValue("Vertical Randomization", 1.2, 0.1, 5, 0.01);
	private SliderValue verticalAimSpeed = new SliderValue("Vertical Aim Speed", 10, 1, 15, 1);
	private SliderValue verticalAimFineTuning = new SliderValue("Vertical Aim Fine-tuning", 5, 1, 10, 1);
	private BooleanValue clickAim = new BooleanValue("Auto Aim on Click", true);
	private BooleanValue centerAim = new BooleanValue("Instant Aim Centering", false);
	private BooleanValue ignoreFriendlyEntities = new BooleanValue("Ignore Friendly Entities", false);
	private BooleanValue ignoreTeammates = new BooleanValue("Ignore Teammates", false);
	private BooleanValue aimAtInvisibleEnemies = new BooleanValue("Aim at Invisible Enemies", false);
	private BooleanValue lineOfSightCheck = new BooleanValue("Line of Sight Check", true);
	private BooleanValue disableAimWhileBreakingBlock = new BooleanValue("Disable Aim While Breaking Block", false);
	private BooleanValue weaponOnly = new BooleanValue("Weapon Only Aim", false);
	private Random random = new Random();

	public AimAssist() {
		this.registerSetting(horizontalAimSpeed, horizontalAimFineTuning, horizontalRandomization,
				horizontalRandomizationAmount, fieldOfView, enemyDetectionRange, verticalAlignmentCheck,
				verticalRandomization, verticalRandomizationAmount, verticalAimSpeed, verticalAimFineTuning, clickAim,
				centerAim, ignoreFriendlyEntities, ignoreTeammates, aimAtInvisibleEnemies, lineOfSightCheck,
				disableAimWhileBreakingBlock, weaponOnly);
	}

	@EventLink
	public void onLiving(LivingEvent e) {
		if (mc.thePlayer == null || mc.currentScreen != null || !mc.inGameHasFocus)
			return;

		if (disableAimWhileBreakingBlock.isToggled() && mc.objectMouseOver != null) {
			BlockPos p = mc.objectMouseOver.getBlockPos();
			if (p != null) {
				Block bl = mc.theWorld.getBlockState(p).getBlock();
				if (bl != Blocks.air && !(bl instanceof BlockLiquid) && bl instanceof Block) {
					return;
				}
			}
		}

		if (!weaponOnly.isToggled() || PlayerUtil.isHoldingWeapon()) {
			AutoClick clicker = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);
			if ((clickAim.isToggled() && ClickUtil.instance.isClicking())
					|| (Mouse.isButtonDown(0) && clicker != null && !clicker.isEnabled()) || !clickAim.isToggled()) {
				Entity enemy = getEnemy();
				if (enemy != null) {
					if (centerAim.isToggled()) {
						CombatUtil.instance.aim(enemy, 0.0f);
					}

					double fovEntity = PlayerUtil.fovFromEntity(enemy);

					double complimentHorizontal = fovEntity
							* (ThreadLocalRandom.current().nextDouble(horizontalAimFineTuning.getInput() - 1.47328,
									horizontalAimFineTuning.getInput() + 2.48293) / 100);
					float resultHorizontal = (float) (-(complimentHorizontal + fovEntity / (101.0D
							- (float) ThreadLocalRandom.current().nextDouble(horizontalAimSpeed.getInput() - 4.723847,
									horizontalAimSpeed.getInput()))));

					double complimentVertical = fovEntity
							* (ThreadLocalRandom.current().nextDouble(verticalAimFineTuning.getInput() - 1.47328,
									verticalAimFineTuning.getInput() + 2.48293) / 100);
					float resultVertical = (float) (-(complimentVertical
							+ fovEntity / (101.0D - (float) ThreadLocalRandom.current()
									.nextDouble(verticalAimSpeed.getInput() - 4.723847, verticalAimSpeed.getInput()))));

					if (fovEntity > 1.0D || fovEntity < -1.0D) {
						boolean yaw = random.nextBoolean();
						float yawChange = yaw
								? -RandomUtils.nextFloat(0F, horizontalRandomizationAmount.getInputToFloat())
								: RandomUtils.nextFloat(0F, horizontalRandomizationAmount.getInputToFloat());
						float yawAdjustment = (float) (horizontalRandomization.isToggled() ? yawChange
								: resultHorizontal);
						mc.thePlayer.rotationYaw += yawAdjustment;
					}

					if (verticalAlignmentCheck.isToggled()) {
						boolean pitch = random.nextBoolean();
						float pitchChange = pitch
								? -RandomUtils.nextFloat(0F, verticalRandomizationAmount.getInputToFloat())
								: RandomUtils.nextFloat(0F, verticalRandomizationAmount.getInputToFloat());
						float pitchAdjustment = (float) (verticalRandomization.isToggled() ? pitchChange
								: resultVertical);
						float newPitch = mc.thePlayer.rotationPitch + pitchAdjustment;
						mc.thePlayer.rotationPitch += pitchAdjustment;
						mc.thePlayer.rotationPitch = newPitch >= 90 ? newPitch - 180
								: newPitch <= -90 ? newPitch + 180 : newPitch;

					}
				}
			}
		}
	}

	public Entity getEnemy() {
		int fov = (int) fieldOfView.getInput();
		List<EntityPlayer> playerList = new ArrayList<>(mc.theWorld.playerEntities);

	    playerList.sort(new Comparator<EntityPlayer>() {
	        @Override
	        public int compare(EntityPlayer player1, EntityPlayer player2) {
	            if (mc.thePlayer.canEntityBeSeen(player1) && !mc.thePlayer.canEntityBeSeen(player2)) {
	                return -1;
	            } else if (!mc.thePlayer.canEntityBeSeen(player1) && mc.thePlayer.canEntityBeSeen(player2)) {
	                return 1;
	            } else {
	                double distance1 = mc.thePlayer.getDistanceToEntity(player1);
	                double distance2 = mc.thePlayer.getDistanceToEntity(player2);
	                int distanceComparison = Double.compare(distance1, distance2);

	                if (distanceComparison == 0) {
	                    int health1 = (int) player1.getHealth();
	                    int health2 = (int) player2.getHealth();
	                    return Integer.compare(health1, health2);
	                }
	                return distanceComparison;
	            }
	        }
	    });

		for (final EntityPlayer entityPlayer : mc.theWorld.playerEntities) {
			if (entityPlayer != mc.thePlayer && entityPlayer.deathTime == 0) {

				if (isFriend(entityPlayer) && ignoreFriendlyEntities.isToggled()) {
					continue;
				}

				if (!mc.thePlayer.canEntityBeSeen(entityPlayer) && lineOfSightCheck.isToggled()) {
					continue;
				}

				if (isTeamMate(entityPlayer) && ignoreTeammates.isToggled()) {
					continue;
				}

				if (entityPlayer == mc.thePlayer || entityPlayer.isDead || isNPCShop(entityPlayer)) {
					continue;
				}

				if (!aimAtInvisibleEnemies.isToggled() && entityPlayer.isInvisible()) {
					continue;
				}

				if (mc.thePlayer.getDistanceToEntity(entityPlayer) > enemyDetectionRange.getInput()) {
					continue;
				}

				if (!centerAim.isToggled() && fov != 360 && !isWithinFOV(entityPlayer, fov)) {
					continue;
				}

				return entityPlayer;
			}
		}

		return null;
	}

	private boolean isFriend(EntityPlayer player) {
		return ignoreFriendlyEntities.isToggled() && FriendUtil.instance.isAFriend(player);
	}

	private boolean isTeamMate(EntityPlayer player) {
		return ignoreTeammates.isToggled() && CombatUtil.instance.isATeamMate(player);
	}

	private boolean isNPCShop(EntityPlayer player) {
		return player.getName().matches("[\\[§]?[NPC] ?\\]?|§a?Shop|SHOP|UPGRADES");
	}

	private boolean isWithinFOV(EntityPlayer player, int fieldOfView) {
		return PlayerUtil.fov(player, (float) fieldOfView);
	}
}
