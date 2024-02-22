package cc.unknown.module.impl.combat;

import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.input.Mouse;

import cc.unknown.Haru;
import cc.unknown.event.impl.api.EventLink;
import cc.unknown.event.impl.move.PostUpdateEvent;
import cc.unknown.event.impl.move.SilentEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.impl.settings.Targets;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.ClientUtil;
import cc.unknown.utils.player.CombatUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.entity.Entity;

public class AimAssist extends Module {
	private SliderValue speedYaw = new SliderValue("Speed Yaw", 50.0, 1.0, 100.0, 1.0);
	private SliderValue complimentYaw = new SliderValue("Compliment Yaw", 50.0, 1.0, 100.0, 1.0);
	private BooleanValue center = new BooleanValue("Center", false);
	private BooleanValue weaponOnly = new BooleanValue("Weapon Only", false);
	private BooleanValue rayCast = new BooleanValue("Ray Cast", false);
	private BooleanValue clickAim = new BooleanValue("Click Aim", true);
	private BooleanValue movementFix = new BooleanValue("Move Fix", false);

	public AimAssist() {
		super("AimAssist", ModuleCategory.Combat);
		this.registerSetting(speedYaw, complimentYaw, center, weaponOnly, rayCast, clickAim, movementFix);
	}

	@EventLink
	public void onSilent(SilentEvent e) {
		if (mc.currentScreen != null)
			return;
		Entity en = this.getEnemy();
		if (clickAim.isToggled() && !mc.gameSettings.keyBindAttack.isKeyDown())
			en = null;
		if (en == null)
			return;
		e.setDoMovementFix(movementFix.isToggled());
		e.setDoJumpFix(movementFix.isToggled());
		e.setYaw(mc.thePlayer.rotationYaw);
		e.setPitch(mc.thePlayer.rotationPitch);
	}

	@EventLink
	public void onPost(PostUpdateEvent e) {
		if (!(mc.currentScreen == null || !PlayerUtil.inGame())) {
			return;
		}

		if (!weaponOnly.isToggled() || PlayerUtil.isHoldingWeapon()) {
			AutoClick clicker = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);
			if ((clickAim.isToggled() && ClientUtil.isClicking())
					|| (Mouse.isButtonDown(0) && clicker != null && !clicker.isEnabled()) || !clickAim.isToggled()) {
				Entity en = this.getEnemy();
				if (en != null) {
					if (center.isToggled()) {
						CombatUtil.aim(en, 0.0f);
					} else {
						double n = PlayerUtil.fovFromEntity(en);
						if (n > 1.0D || n < -1.0D) {
							double compliment = n * (ThreadLocalRandom.current().nextDouble(complimentYaw.getInput() - 1.47328, complimentYaw.getInput() + 2.48293) / 100);
							float val = (float) (-(compliment + n / (101.0D - (float) ThreadLocalRandom.current().nextDouble(speedYaw.getInput() - 4.723847, speedYaw.getInput()))));
							mc.thePlayer.rotationYaw += val;
						}

						if (rayCast.isToggled()) {
							CombatUtil.rayCast(en);
						}
					}
				}
			}
		}
	}

	private Entity getEnemy() {
		return Targets.getTarget();
	}
}
