package cc.unknown.module.impl.player;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.utils.player.PlayerUtil;

public class Sprint extends Module {

	public BooleanValue omni = new BooleanValue("Omni", false);

	public Sprint() {
		super("Sprint", ModuleCategory.Player);
		this.registerSetting(omni);
	}

	@EventLink
	public void onOmni(TickEvent e) {
		if (omni.isToggled()) {
			if (PlayerUtil.inGame() && mc.inGameHasFocus && PlayerUtil.isMoving()) {
				mc.thePlayer.setSprinting(true);
			}
		}
	}
}
