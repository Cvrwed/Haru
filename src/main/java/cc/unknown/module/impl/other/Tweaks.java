package cc.unknown.module.impl.other;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.ModuleInfo;
import cc.unknown.module.setting.impl.BooleanValue;

@ModuleInfo(name = "Tweaks", category = Category.Other)
public class Tweaks extends Module {
	private BooleanValue noClickDelay = new BooleanValue("No Click Delay", true);
	private BooleanValue noJumpDelay = new BooleanValue("No Jump Delay", true);
	public BooleanValue noHurtCam = new BooleanValue("No Hurt Cam", true);
	public BooleanValue noScoreboard = new BooleanValue("No Scoreboard", false);

	public Tweaks() {
		this.registerSetting(noClickDelay, noJumpDelay, noHurtCam);
	}

	@EventLink
	public void onClick(TickEvent e) {
		if (noClickDelay.isToggled() && this.isEnabled()) {
			mc.leftClickCounter = 0;
		}
	}

	@EventLink
	public void onJump(TickEvent e) {
		if (noJumpDelay.isToggled() && this.isEnabled()) {
			mc.thePlayer.jumpTicks = 0;
		}
	}
}
