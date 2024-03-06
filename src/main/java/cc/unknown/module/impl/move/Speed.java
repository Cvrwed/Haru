package cc.unknown.module.impl.move;

import cc.unknown.event.impl.api.EventLink;
import cc.unknown.event.impl.player.StrafeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.utils.player.PlayerUtil;

public class Speed extends Module {

	private ModeValue mode = new ModeValue("Mode", "Legit Strafe", "Legit Strafe");

	public Speed() {
		super("Speed", ModuleCategory.Move);
		this.registerSetting(mode);
	}

	@EventLink
	public void onStrafe(StrafeEvent e) {
		if (mode.is("Legit Strafe") && !mc.thePlayer.onGround && (e.getStrafe() != 0 || e.getForward() != 0)) {
			e.setYaw(PlayerUtil.getStrafeYaw(e.getForward(), e.getStrafe()));
			e.setForward(1);
			e.setStrafe(0);
			mc.thePlayer.jump();
		}
	}

}
