package cc.unknown.module.impl.combat;

import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import net.minecraft.entity.Entity;

public class KeepSprint extends Module {
	private ModeValue mode = new ModeValue("Mode", "Dynamic", "Dynamic", "Normal");
	private SliderValue motionXZ = new SliderValue("Motion X/Z", 0, 0, 100, 1);

	public KeepSprint() {
		super("KeepSprint", ModuleCategory.Combat);
		this.registerSetting(mode, motionXZ);
	}
	
    public void sl(Entity en) {
        double m = (100.0D - (double) motionXZ.getInput()) / 100.0D;

        if (mode.is("Dynamic")) {
            dynamicMode(m);
        } else if (mode.is("Normal")) {
        	normalMode(m);
        }
    }

    private void dynamicMode(double m) {
        if (mc.thePlayer.hurtTime > 0) {
            mc.thePlayer.motionX *= 0.6D;
            mc.thePlayer.motionZ *= 0.6D;
            mc.thePlayer.setSprinting(false);
        } else {
            mc.thePlayer.motionX *= m;
            mc.thePlayer.motionZ *= m;
        }
    }

    private void normalMode(double m) {
        mc.thePlayer.motionX *= m;
        mc.thePlayer.motionZ *= m;
    }
}