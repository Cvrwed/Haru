package cc.unknown.module.impl.move;

import cc.unknown.event.impl.api.EventLink;
import cc.unknown.event.impl.move.UpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.utils.player.MoveUtil;
import cc.unknown.utils.player.PlayerUtil;

public class NoWeb extends Module {

	private ModeValue mode = new ModeValue("Mode", "Intave", "Intave", "Vanilla");

	public NoWeb() {
		super("NoWeb", ModuleCategory.Move);
		this.registerSetting(mode);
	}

	@EventLink
	public void onUpdate(UpdateEvent e) {
		if (mode.is("Intave")) {
			if (!mc.thePlayer.isInWeb || mc.thePlayer == null) {
				return;
			}

			if (PlayerUtil.isMoving() && mc.thePlayer.moveStrafing == 0.0f) {
				if (mc.thePlayer.onGround) {
					if (mc.thePlayer.ticksExisted % 3 == 0) {
						MoveUtil.strafe(0.734f);
					} else {
						mc.thePlayer.jump();
						MoveUtil.strafe(0.346f);
					}
				}
			}
		}
		
		if (mode.is("Vanilla")) {
			mc.thePlayer.isInWeb = false;
		}
	}
}
