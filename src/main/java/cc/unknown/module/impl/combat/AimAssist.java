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
	private SliderValue speedYaw = new SliderValue("Speed Yaw", 45, 5, 100, 1);
	private SliderValue complimentYaw = new SliderValue("Compliment Yaw", 15, 2, 97, 1);
	private BooleanValue clickAim = new BooleanValue("Click Aim", true);
	private BooleanValue center = new BooleanValue("Instant", false);
	private BooleanValue rayCast = new BooleanValue("Not behind blocks", false);
	private BooleanValue disableWhen = new BooleanValue("Disable while breaking blocks", false);
	private BooleanValue weaponOnly = new BooleanValue("Weapon Only", false);

	public AimAssist() {
		super("AimAssist", ModuleCategory.Combat);
		this.registerSetting(speedYaw, complimentYaw, clickAim, center, rayCast, disableWhen, weaponOnly);
	}

	@EventLink
	public void onUpdate(LivingUpdateEvent e) {
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
					
					double n = PlayerUtil.fovFromEntity(enemy);
					if (n > 1.0D || n < -1.0D) {
						double compliment = n * (ThreadLocalRandom.current().nextDouble(complimentYaw.getInput() - 1.47328, complimentYaw.getInput() + 2.48293) / 100);
						float val = (float) (-(compliment + n / (101.0D - (float) ThreadLocalRandom.current().nextDouble(speedYaw.getInput() - 4.723847, speedYaw.getInput()))));
						mc.thePlayer.rotationYaw += val;
					}

					if (rayCast.isToggled()) {
						CombatUtil.instance.canEntityBeSeen(enemy);
					}
				}
			}
		}
	}


	private Entity getEnemy() {
		return CombatUtil.instance.getTarget();
	}
}
