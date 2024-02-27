package cc.unknown.module.impl.player;

import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;

public class NoSlow extends Module {
	public ModeValue mode = new ModeValue("Mode", "Grim", "Grim", "C16", "Vanilla");
	public SliderValue vForward = new SliderValue("Vanilla forward", 1.0, 0.2, 1.0, 0.1);
	public SliderValue vStrafe = new SliderValue("Vanilla strafe", 1.0, 0.2, 1.0, 0.1);

	public NoSlow() {
		super("NoSlow", ModuleCategory.Player);
		this.registerSetting(mode, vForward, vStrafe);
	}
}
