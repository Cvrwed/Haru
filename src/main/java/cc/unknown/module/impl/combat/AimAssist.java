package cc.unknown.module.impl.combat;

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
import cc.unknown.module.setting.impl.DoubleSliderValue;
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
	private SliderValue speedYaw = new SliderValue("Horizontal Aim Speed", 45, 5, 100, 1);
	private SliderValue complimentYaw = new SliderValue("Horizontal Aim Fine-tuning", 15, 2, 97, 1);
	private SliderValue fov = new SliderValue("Field of View [FOV]", 90.0, 15.0, 360.0, 1.0);
	public SliderValue distance = new SliderValue("Enemy Detection Range", 4.5, 1.0, 10.0, 0.5);
	private BooleanValue verticalCheck = new BooleanValue("Vertical Alignment Check", false);
	private DoubleSliderValue pitchRand = new DoubleSliderValue("Vertical Randomization", 0.1, 1.2, 0.1, 4, 0.1);
	private BooleanValue clickAim = new BooleanValue("Auto Aim on Click", true);
	private BooleanValue center = new BooleanValue("Instant Aim Centering", false);
	private BooleanValue jitter = new BooleanValue("Jitter", false);
	private SliderValue jitterRand = new SliderValue("Jitter Randomization", 1, 0, 5, 0.1);
	private BooleanValue ignoreFriends = new BooleanValue("Ignore Friendly Entities", false);
	private BooleanValue ignoreTeams = new BooleanValue("Ignore Teammates", false);
	private BooleanValue aimInvis = new BooleanValue("Aim at Invisible Enemies", false);
	private BooleanValue rayCast = new BooleanValue("Line of Sight Check", true);
	private BooleanValue disableWhen = new BooleanValue("Disable Aim While Breaking Blocks", false);
	private BooleanValue weaponOnly = new BooleanValue("Weapon Only Aim", false);

	private Random random = new Random();

	public AimAssist() {
		this.registerSetting(speedYaw, complimentYaw, fov, distance, verticalCheck, pitchRand, clickAim, center, jitter,
				jitterRand, ignoreFriends, ignoreTeams, aimInvis, rayCast, disableWhen, weaponOnly);
	}

	@EventLink
	public void onLiving(LivingEvent e) {
		if (mc.thePlayer == null || mc.currentScreen != null || !mc.inGameHasFocus)
			return;

		if (disableWhen.isToggled() && mc.objectMouseOver != null) {
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
			if ((clickAim.isToggled() && ClickUtil.instance.isClicking()) || (Mouse.isButtonDown(0) && clicker != null && !clicker.isEnabled()) || !clickAim.isToggled()) {
				Entity enemy = getEnemy();
				if (enemy != null) {
					if (center.isToggled()) {
						CombatUtil.instance.aim(enemy, 0.0f);
					}

					double fovEntity = PlayerUtil.fovFromEntity(enemy);
					double compliment = fovEntity * (ThreadLocalRandom.current()
							.nextDouble(complimentYaw.getInput() - 1.47328, complimentYaw.getInput() + 2.48293) / 100);
					float result = (float) (-(compliment + fovEntity / (101.0D - (float) ThreadLocalRandom.current()
							.nextDouble(speedYaw.getInput() - 4.723847, speedYaw.getInput()))));
					double pitchRandMin = pitchRand.getInputMin();
					double pitchRandMax = pitchRand.getInputMax();
					double pitchRandValue = pitchRandMin + (Math.random() * (pitchRandMax - pitchRandMin));
					double pitchVariation = ThreadLocalRandom.current().nextDouble(-pitchRandValue, pitchRandValue);

					if (fovEntity > 1.0D || fovEntity < -1.0D) {
						mc.thePlayer.rotationYaw += result;
					}

					if (verticalCheck.isToggled()) {
						double pitchLimit = 90.0;
						double currentPitch = mc.thePlayer.rotationPitch;
						double newPitch = currentPitch + pitchVariation;
						if (newPitch > pitchLimit) {
							pitchVariation = pitchLimit - currentPitch;
						} else if (newPitch < -pitchLimit) {
							pitchVariation = -pitchLimit - currentPitch;
						}
						mc.thePlayer.rotationPitch += pitchVariation;

					}

					if (jitter.isToggled()) {
						boolean yaw = random.nextBoolean();
						boolean pitch = random.nextBoolean();
						boolean yawNegative = random.nextBoolean();
						boolean pitchNegative = random.nextBoolean();

						if (yaw) {
							float yawChange = yawNegative ? -RandomUtils.nextFloat(0F, jitterRand.getInputToFloat())
									: RandomUtils.nextFloat(0F, jitterRand.getInputToFloat());
							mc.thePlayer.rotationYaw += yawChange;
						}

						if (pitch) {
							float pitchChange = pitchNegative ? -RandomUtils.nextFloat(0F, jitterRand.getInputToFloat())
									: RandomUtils.nextFloat(0F, jitterRand.getInputToFloat());
							mc.thePlayer.rotationPitch += pitchChange;
							if (mc.thePlayer.rotationPitch > 90) {
								mc.thePlayer.rotationPitch = 90F;
							} else if (mc.thePlayer.rotationPitch < -90) {
								mc.thePlayer.rotationPitch = -90F;
							}
						}
					}
				}
			}
		}
	}

	public Entity getEnemy() {
		int fieldOfView = (int) fov.getInput();
		for (final EntityPlayer entityPlayer : mc.theWorld.playerEntities) {
			if (entityPlayer != mc.thePlayer && entityPlayer.deathTime == 0) {

				if (isFriend(entityPlayer) && ignoreFriends.isToggled()) {
					continue;
				}

				if (mc.thePlayer.canEntityBeSeen(entityPlayer) && !rayCast.isToggled()) {
					continue;
				}

				if (isTeamMate(entityPlayer) && ignoreTeams.isToggled()) {
					continue;
				}

				if (entityPlayer == mc.thePlayer || entityPlayer.isDead || isNPCShop(entityPlayer)) {
					continue;
				}

				if (!aimInvis.isToggled() && entityPlayer.isInvisible()) {
					continue;
				}

				if ((double) mc.thePlayer.getDistanceToEntity(entityPlayer) > distance.getInput()) {
					continue;
				}

				if (!center.isToggled() && fieldOfView != 360 && !isWithinFOV(entityPlayer, fieldOfView)) {
					continue;
				}

				return entityPlayer;
			}
		}
		return null;
	}

	private boolean isFriend(EntityPlayer player) {
		return ignoreFriends.isToggled() && FriendUtil.instance.isAFriend(player);
	}

	private boolean isTeamMate(EntityPlayer player) {
		return ignoreTeams.isToggled() && CombatUtil.instance.isATeamMate(player);
	}

	private boolean isNPCShop(EntityPlayer player) {
		return player.getName().matches("[\\[§]?[NPC] ?\\]?|§a?Shop|SHOP|UPGRADES");
	}

	private boolean isWithinFOV(EntityPlayer player, int fieldOfView) {
		return PlayerUtil.fov(player, (float) fieldOfView);
	}
}
