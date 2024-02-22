package cc.unknown.module.impl.visuals;

import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.SliderValue;

public class HitColor extends Module {
	
	public SliderValue color = new SliderValue("Color [H/S/B]", 0, 0, 350, 10);
	
	public HitColor() {
		super("HitColor", ModuleCategory.Visuals);
		this.registerSetting(color);
	}
}
