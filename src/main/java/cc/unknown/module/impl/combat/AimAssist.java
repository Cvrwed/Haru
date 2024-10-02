package cc.unknown.module.impl.combat;

import static org.apache.commons.lang3.RandomUtils.nextFloat;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.PreMotionEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.misc.ClickUtil;
import cc.unknown.utils.player.CombatUtil;
import cc.unknown.utils.player.FriendUtil;
import cc.unknown.utils.player.PlayerUtil;
import cc.unknown.utils.vector.CustomVec3;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

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
	private BooleanValue mouseOverEntity = new BooleanValue("Mouse Over Entity", false);
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
	public void onPreMotion(PreMotionEvent e) {
		if (noAim()) {
			return;
		}

		final EntityPlayer target = getEnemy();
		if (target == null)
			return;

		
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
	
	private EntityPlayer getEnemy() {
		int fov = (int) fieldOfView.getInput();
		final List<EntityPlayer> players = mc.theWorld.playerEntities;
		final CustomVec3 playerPos = new CustomVec3(mc.thePlayer);

		EntityPlayer target = null;
		double targetFov = Double.MAX_VALUE;
		for (final EntityPlayer entityPlayer : players) {
			if (entityPlayer != mc.thePlayer && entityPlayer.deathTime == 0) {
				double dist = playerPos.distanceTo(entityPlayer);
				if (isFriend(entityPlayer) && ignoreFriendlyEntities.isToggled()) continue;
				if (ignoreTeammates.isToggled() && isTeamMate(entityPlayer)) continue;
				if (dist > enemyDetectionRange.getInput()) continue;
				if (fov != 360 && !PlayerUtil.fov(entityPlayer, fov)) continue;
				if (lineOfSightCheck.isToggled() && !mc.thePlayer.canEntityBeSeen(entityPlayer)) continue;
				double curFov = Math.abs(PlayerUtil.getFov(entityPlayer.posX, entityPlayer.posZ));
				if (curFov < targetFov) {
					target = entityPlayer;
					targetFov = curFov;
				}
			}
		}
		return target;
	}

	private boolean noAim() {
		if (mc.currentScreen != null || !mc.inGameHasFocus) return true;
		if (weaponOnly.isToggled() && !PlayerUtil.isHoldingWeapon()) return true;
		if (clickAim.isToggled() && !ClickUtil.instance.isClicking()) return true;
		if (mouseOverEntity.isToggled() && mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) return true;
		return disableAimWhileBreakingBlock.isToggled() && mc.playerController.isHittingBlock;
	}

	private boolean isFriend(EntityPlayer player) {
		return FriendUtil.instance.isAFriend(player);
	}

	private boolean isTeamMate(EntityPlayer player) {
		return CombatUtil.instance.isTeam(player);
	}
}
