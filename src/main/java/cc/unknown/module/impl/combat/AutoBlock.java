package cc.unknown.module.impl.combat;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.utils.helpers.MathHelper;
import cc.unknown.utils.player.CombatUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.settings.KeyBinding;

public class AutoBlock extends Module {

	private ModeValue mode = new ModeValue("Mode", "Basic", "Basic");
	private BooleanValue limitTarget = new BooleanValue("Limit target", false);
	private BooleanValue limitCps = new BooleanValue("Limit cps", false);
	private DoubleSliderValue blockCps = new DoubleSliderValue("Limit Cps", 16, 19, 1, 30, 1);
	private long blockDelay = 50L;
	private long lastBlock = 0L;

	public AutoBlock() {
		super("AutoBlock", ModuleCategory.Combat);
		this.registerSetting(mode, limitTarget, limitCps, blockCps);
	}

	@EventLink
	public void onRender(Render3DEvent e) {
		if (mode.is("Basic")) {
			if (!limitTarget.isToggled() || CombatUtil.instance.canTarget(mc.objectMouseOver.entityHit)) {
				if (mc.gameSettings.keyBindAttack.isKeyDown() && !mc.gameSettings.keyBindUseItem.isKeyDown() && PlayerUtil.isHoldingWeapon() && mc.objectMouseOver.entityHit != null) {
					if (limitCps.isToggled()) {
						if (System.currentTimeMillis() - lastBlock >= blockDelay) {
							KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());

							lastBlock = System.currentTimeMillis();
							blockDelay = MathHelper.randomClickDelay(blockCps.getInputMinToInt(), blockCps.getInputMaxToInt());
						}
					} else {
						KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
					}
				}
			}
		}
	}
}
