package cc.unknown.module.impl.settings;

import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.SliderValue;

public class Colors extends Module {
	
	private SliderValue saturation = new SliderValue("Saturation [H/S/B]", 1.0, 0.0, 1.0, 0.1);
	private SliderValue brightness = new SliderValue("Brightness [H/S/B]", 1.0, 0.0, 1.0, 0.1);

	public Colors() {
        super("Custom Colors", ModuleCategory.Settings);
        this.registerSetting(saturation, brightness);
        onEnable();
    }

	@Override
    public boolean canBeEnabled() {
        return false;
    }

	public SliderValue getSaturation() {
		return saturation;
	}

	public SliderValue getBrightness() {
		return brightness;
	}

}
