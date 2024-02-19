package cc.unknown.module.impl.player;

import cc.unknown.event.impl.api.EventLink;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.utils.player.PlayerUtil;

public class Sprint extends Module {

	public BooleanValue omni = new BooleanValue("Omni", true);

	public Sprint() {
		super("Sprint", ModuleCategory.Player);
		this.registerSetting(omni);
	}

	@EventLink
	public void onOmni(TickEvent e) {
		if (omni.isToggled()) {
			if (PlayerUtil.inGame() && mc.inGameHasFocus) {
				if (PlayerUtil.isMoving() || (mc.thePlayer.movementInput.moveForward > 0) && (mc.thePlayer.movementInput.moveStrafe == 0)) {
					mc.thePlayer.setSprinting(true);
				}
			}
		}
	}
}
