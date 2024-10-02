package cc.unknown.module.impl.combat;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.netty.PreVelocityEvent;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.ModuleInfo;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;

@ModuleInfo(name = "Velocity", category = Category.Combat)
public class Velocity extends Module {

	public ModeValue mode = new ModeValue("Mode", "Simple", "Simple");
	public SliderValue horizontal = new SliderValue("Horizontal", 90, -100, 100, 1);
	public SliderValue vertical = new SliderValue("Vertical", 100, -100, 100, 1);
	public SliderValue chance = new SliderValue("Chance", 100, 0, 100, 1);
	private BooleanValue onlyCombat = new BooleanValue("Only During Combat", false);
	private BooleanValue onlyGround = new BooleanValue("Only While on Ground", false);

	public Velocity() {
		this.registerSetting(mode, horizontal, vertical, chance, onlyCombat, onlyGround);
	}

	@EventLink
	public void onGui(ClickGuiEvent e) {
		this.setSuffix("- [" + mode.getMode() + "]");
	}

	@EventLink
	public void onKnockBack(PreVelocityEvent e) {
		if (chance.getInput() != 100.0D) {
			if (Math.random() >= chance.getInput() / 100.0D) {
				return;
			}
		}
		
		if (mode.is("Simple")) {
			e.setX(e.getX() * horizontal.getInput() / 100.0);
			e.setY(e.getY() * vertical.getInput() / 100.0);
			e.setZ(e.getZ() * horizontal.getInput() / 100.0);
		}
	}
}
