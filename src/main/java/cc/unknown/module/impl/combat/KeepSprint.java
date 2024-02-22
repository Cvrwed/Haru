package cc.unknown.module.impl.combat;

import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import net.minecraft.entity.Entity;

public class KeepSprint extends Module {
	private ModeValue mode = new ModeValue("Mode", "Dynamic", "Dynamic", "Normal");
	private SliderValue motionXZ = new SliderValue("Motion X/Z", 0, 0, 100, 1);
	private BooleanValue onlyInAir = new BooleanValue("Only in air", false);

	public KeepSprint() {
		super("KeepSprint", ModuleCategory.Combat);
		this.registerSetting(mode, motionXZ, onlyInAir);
	}
	
    public void sl(Entity en) {
        if (!mc.thePlayer.onGround && onlyInAir.isToggled()) {
            return;
        }
        
        if (mode.is("Dynamic")) {
            dynamicMode((100.0D - motionXZ.getInput()) / 100.0D);
        } else if (mode.is("Normal")) {
        	normalMode((100.0D - motionXZ.getInput()) / 100.0D);
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