package cc.unknown.module.impl.combat;

import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.input.Mouse;

import cc.unknown.Haru;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.LivingUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.misc.ClickUtil;
import cc.unknown.utils.player.CombatUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class AimAssist extends Module {
	private SliderValue speedYaw = new SliderValue("Horizontal Speed", 2, 0, 100, 1);
	private SliderValue complimentYaw = new SliderValue("Horizontal Complement", 15, 2, 97, 1);
	private BooleanValue verticalCheck = new BooleanValue("Vertical Check", false);
	private SliderValue speedPitch = new SliderValue("Vertical Speed", 2, 0, 100, 1);
	private SliderValue complementPitch = new SliderValue("Vertical Complement", 15, 1, 97, 1);
	private SliderValue pitchOffset = new SliderValue("Vertical Randomization", 3.0, 0.0, 4.0, 0.1);
	private BooleanValue clickAim = new BooleanValue("Click Aim", true);
	private BooleanValue instantCenter = new BooleanValue("Instant Center", false);
	private BooleanValue rayCast = new BooleanValue("Ray Cast (No Blocks)", false);
	private BooleanValue breakingBlocks = new BooleanValue("Disable While Breaking Blocks", false);
	private BooleanValue weaponOnly = new BooleanValue("Weapon Only", false);
	private float[] facing;

	public AimAssist() {
		super("AimAssist", ModuleCategory.Combat);
		this.registerSetting(speedYaw, complimentYaw, verticalCheck, speedPitch, complementPitch, pitchOffset, clickAim, instantCenter, rayCast, breakingBlocks,
				weaponOnly);
	}

	@EventLink
	public void onUpdate(LivingUpdateEvent e) {
		if (mc.thePlayer == null || mc.currentScreen != null || !mc.inGameHasFocus)
			return;

		if (breakingBlocks.isToggled() && mc.objectMouseOver != null) {
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
					if (instantCenter.isToggled()) {
						CombatUtil.instance.aim(enemy, 0.0f);
					}

					double n = PlayerUtil.fovFromEntity(enemy);
					if (n > 1.0D || n < -1.0D) {
						this.facing = CombatUtil.instance.getTargetRotations(enemy);
						float f = this.facing[0];
						float f2 = this.facing[1];
						double yawCompl = (float) (n * (ThreadLocalRandom.current().nextDouble(speedYaw.getInput() - 1.47328, speedYaw.getInput() + 2.48293) / 100));
						float finalYawVal = (float) (-(yawCompl + n / (101.0D - (float) ThreadLocalRandom.current().nextDouble(complimentYaw.getInput() - 4.723847, complimentYaw.getInput()))));
						
						if (mc.thePlayer.rotationYaw < f || mc.thePlayer.rotationYaw > f) {
							mc.thePlayer.rotationYaw += finalYawVal;
						}
						
						if (verticalCheck.isToggled()) {
							
							double pitchCompl = (n * (ThreadLocalRandom.current().nextDouble(speedPitch.getInput() - 1.47328, speedPitch.getInput() + 2.48293) / 100));
							float finalPitchVal = (float) (-(pitchCompl + n / (101.0D - (float) ThreadLocalRandom.current().nextDouble(complementPitch.getInput() - 4.723847, complementPitch.getInput()))));
							
							if (mc.thePlayer.rotationPitch < f2) {
								mc.thePlayer.rotationPitch += Math.random() * finalPitchVal - pitchOffset.getInput();
								mc.thePlayer.rotationPitch = Math.max(mc.thePlayer.rotationPitch, -90);
								mc.thePlayer.rotationPitch = Math.min(mc.thePlayer.rotationPitch, 90);
							}
							
							if (mc.thePlayer.rotationPitch > f2) {
								mc.thePlayer.rotationPitch -= Math.random() * finalPitchVal - pitchOffset.getInput();
								mc.thePlayer.rotationPitch = Math.max(mc.thePlayer.rotationPitch, -90);
								mc.thePlayer.rotationPitch = Math.min(mc.thePlayer.rotationPitch, 90);
							}
						}

						if (rayCast.isToggled()) {
							CombatUtil.instance.canEntityBeSeen(enemy);
						}
					}
				}
			}
		}
	}

	private Entity getEnemy() {
		return CombatUtil.instance.getTarget();
	}
}
