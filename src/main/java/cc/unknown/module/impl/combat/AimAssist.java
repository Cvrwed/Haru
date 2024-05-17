package cc.unknown.module.impl.combat;

import static org.apache.commons.lang3.RandomUtils.nextFloat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.input.Mouse;

import cc.unknown.Haru;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.LivingEvent;
import cc.unknown.event.impl.player.JumpEvent;
import cc.unknown.event.impl.player.StrafeEvent;
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
	private BooleanValue moveFix = new BooleanValue("Movemenet Fix", false);
	private BooleanValue ignoreFriendlyEntities = new BooleanValue("Ignore Friendly Entities", false);
	private BooleanValue ignoreTeammates = new BooleanValue("Ignore Teammates", false);
	private BooleanValue aimAtInvisibleEnemies = new BooleanValue("Aim at Invisible Enemies", false);
	private BooleanValue lineOfSightCheck = new BooleanValue("Line of Sight Check", true);
	private BooleanValue disableAimWhileBreakingBlock = new BooleanValue("Disable Aim While Breaking Block", false);
	private BooleanValue weaponOnly = new BooleanValue("Weapon Only Aim", false);
	private Random random = new Random();
	private EntityPlayer target; // i fix.... i think

	public AimAssist() {
		this.registerSetting(horizontalAimSpeed, horizontalAimFineTuning, horizontalRandomization,
				horizontalRandomizationAmount, fieldOfView, enemyDetectionRange, verticalAlignmentCheck,
				verticalRandomization, verticalRandomizationAmount, verticalAimSpeed, verticalAimFineTuning, clickAim,
				centerAim, moveFix, ignoreFriendlyEntities, ignoreTeammates, aimAtInvisibleEnemies, lineOfSightCheck,
				disableAimWhileBreakingBlock, weaponOnly);
	}

	@EventLink
	public void onLiving(LivingEvent e) {
	    if (mc.thePlayer == null || mc.currentScreen != null || !mc.inGameHasFocus) {
	        return;
	    }

	    if (disableAimWhileBreakingBlock.isToggled() && mc.objectMouseOver != null) {
	        BlockPos blockPos = mc.objectMouseOver.getBlockPos();
	        if (blockPos != null) {
	            Block block = mc.theWorld.getBlockState(blockPos).getBlock();
	            if (block != Blocks.air && !(block instanceof BlockLiquid)) {
	                return;
	            }
	        }
	    }

		if (!weaponOnly.isToggled() || PlayerUtil.isHoldingWeapon()) {
			AutoClick clicker = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);
			if ((clickAim.isToggled() && ClickUtil.instance.isClicking()) || (Mouse.isButtonDown(0) && clicker != null && !clicker.isEnabled()) || !clickAim.isToggled()) {
	            target = getEnemy();
	            if (target != null) {
	                if (centerAim.isToggled()) {
	                    CombatUtil.instance.aim(target, 0.0f);
	                }

	                double fovEntity = PlayerUtil.fovFromEntity(target);
	                double pitchEntity = PlayerUtil.PitchFromEntity(target, 0);

	                double horizontalRandomOffset = ThreadLocalRandom.current().nextDouble(horizontalAimFineTuning.getInput() - 1.47328, horizontalAimFineTuning.getInput() + 2.48293) / 100;
	                float resultHorizontal = (float) (-(fovEntity * horizontalRandomOffset + fovEntity / (101.0D - (float) ThreadLocalRandom.current().nextDouble(horizontalAimSpeed.getInput() - 4.723847, horizontalAimSpeed.getInput()))));

	                double verticalRandomOffset = ThreadLocalRandom.current().nextDouble(verticalAimFineTuning.getInput() - 1.47328, verticalAimFineTuning.getInput() + 2.48293) / 100;
	                float resultVertical = (float) (-(pitchEntity * verticalRandomOffset + pitchEntity / (101.0D - (float) ThreadLocalRandom.current().nextDouble(verticalAimSpeed.getInput() - 4.723847, verticalAimSpeed.getInput()))));

	                if (fovEntity > 1.0D || fovEntity < -1.0D) {
	                    float yawChange = random.nextBoolean() ? -nextFloat(0F, horizontalRandomizationAmount.getInputToFloat()) : nextFloat(0F, horizontalRandomizationAmount.getInputToFloat());
	                    float yawAdjustment = (float) (horizontalRandomization.isToggled() ? yawChange : resultHorizontal);
	                    mc.thePlayer.rotationYaw += yawAdjustment;

	                    if (verticalAlignmentCheck.isToggled()) {
	                        float pitchChange = random.nextBoolean() ? -nextFloat(0F, verticalRandomizationAmount.getInputToFloat()) : nextFloat(0F, verticalRandomizationAmount.getInputToFloat());
	                        float pitchAdjustment = (float) (verticalRandomization.isToggled() ? pitchChange : resultVertical);
	                        float newPitch = mc.thePlayer.rotationPitch + pitchAdjustment;
							mc.thePlayer.rotationPitch += pitchAdjustment;
							mc.thePlayer.rotationPitch = newPitch >= 90f ? newPitch - 360f : newPitch <= -90f ? newPitch + 360f : newPitch;
	                    }
	                }
	            }
	        }
	    }
	}

	@EventLink
	public void onJump(JumpEvent e) {
		if (target != null && moveFix.isToggled()) {
			e.setYaw(mc.thePlayer.rotationYaw);
		}
	}

	@EventLink
	public void onStrafe(StrafeEvent e) {
		if (target != null && moveFix.isToggled()) {
			e.setYaw(mc.thePlayer.rotationYaw);
		}
	}

	public EntityPlayer getEnemy() {
	    ArrayList<EntityPlayer> entities = new ArrayList<>();
	    for (Entity entity : mc.theWorld.loadedEntityList) {
	        if (entity instanceof EntityPlayer && entity != mc.thePlayer) {
	            EntityPlayer player = (EntityPlayer) entity;
	            if (isValidTarget(player)) {
	                entities.add(player);
	            }
	        }
	    }

		if (entities != null && entities.size() > 0) {
	        entities.sort(Comparator.comparingDouble(EntityPlayer::getHealth).reversed());
			return entities.get(0);
		}

	    return null;
	}

	private boolean isValidTarget(EntityPlayer player) {
		int fov = (int) fieldOfView.getInput();
		if (player == mc.thePlayer && player.isDead && !isNPCShop(player)) {
			return false;
		}

		if (mc.thePlayer.getDistanceToEntity(player) > enemyDetectionRange.getInput()) {
			return false;
		}

		if (ignoreFriendlyEntities.isToggled() && isFriend(player)) {
			return false;
		}

		if (!mc.thePlayer.canEntityBeSeen(player) && lineOfSightCheck.isToggled()) {
			return false;
		}

		if (ignoreTeammates.isToggled() && !isTeamMate(player)) {
			return false;
		}

		if (!aimAtInvisibleEnemies.isToggled() && player.isInvisible()) {
			return false;
		}

		if (!centerAim.isToggled() && fov != 360 && !isWithinFOV(player, fov)) {
			return false;
		}

		return true;
	}

	private boolean isFriend(EntityPlayer player) {
		return FriendUtil.instance.isAFriend(player);
	}

	private boolean isTeamMate(EntityPlayer player) {
		return CombatUtil.instance.isTeam(player);
	}

	private boolean isNPCShop(EntityPlayer player) {
		return player.getName().matches("[\\[§]?[NPC] ?\\]?|§a?Shop|SHOP|UPGRADES");
	}

	private boolean isWithinFOV(EntityPlayer player, int fieldOfView) {
		return PlayerUtil.fov(player, (float) fieldOfView);
	}
}
