package cc.unknown.module.impl.move;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.utils.player.MoveUtil;
import cc.unknown.utils.player.PlayerUtil;

@Register(name = "Speed", category = Category.Move)
public class Speed extends Module {

	private ModeValue mode = new ModeValue("Mode", "Verus", "Verus");
	
	public Speed() {
		this.registerSetting(mode);
	}
	
	@EventLink
	public void onMotion(MotionEvent e) {
		if (e.isPre()) {
			if (PlayerUtil.isMoving())
				MoveUtil.strafe(0.32F);
				mc.gameSettings.keyBindJump.pressed = true;
		}
	}
}
