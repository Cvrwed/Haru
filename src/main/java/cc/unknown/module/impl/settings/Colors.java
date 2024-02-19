package cc.unknown.module.impl.settings;

import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.SliderValue;

public class Colors extends Module {
	
	public static SliderValue colors = new SliderValue("ArrayList Color [H/S/B]", 0, 0, 350, 10);
	public static SliderValue colors2 = new SliderValue("ClickGui Color [H/S/B]", 0, 0, 350, 10);

    public Colors() {
        super("Custom Colors", ModuleCategory.Settings);
        this.registerSetting(colors, colors2);
        onEnable();
    }

    @Override
    public boolean canBeEnabled() {
        return false;
    }
}
