package cc.unknown.module.impl.combat;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.utils.helpers.MathHelper;
import cc.unknown.utils.player.CombatUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.settings.KeyBinding;

@Register(name = "AutoBlock", category = Category.Combat)
public class AutoBlock extends Module {

	private ModeValue mode = new ModeValue("Mode", "Basic", "Basic", "Lag");
	private BooleanValue limitTarget = new BooleanValue("Limit Target", false);
	private BooleanValue limitCps = new BooleanValue("Limit CPS", false);
	private DoubleSliderValue blockCps = new DoubleSliderValue("Block CPS Limit", 16, 19, 1, 30, 1);
	private long blockDelay = 50L;
	private long lastBlock = 0L;

	public AutoBlock() {
		this.registerSetting(mode, limitTarget, limitCps, blockCps);
	}
	
	@EventLink
	public void onGui(ClickGuiEvent e) {
	    this.setSuffix(mode.getMode());
	}

	@EventLink
	public void onRender(Render3DEvent e) {
		if (mode.is("Basic")) {
			if (!limitTarget.isToggled() || CombatUtil.instance.canTarget(mc.objectMouseOver.entityHit, true)) {
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
