package cc.unknown.module.impl.combat;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.HitSlowDownEvent;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.SliderValue;

@Register(name = "KeepSprint", category = Category.Combat)
public class KeepSprint extends Module {
	
    private final SliderValue deffensive = new SliderValue("Defensive Motion", 0.6, 0, 1, 0.05);
    private final SliderValue offensive = new SliderValue("Offensive Motion", 1, 0, 1, 0.05);
    private final SliderValue chance = new SliderValue("Chance", 100, 0, 100, 1);
    
	public KeepSprint() {
		this.registerSetting(deffensive, offensive, chance);
	}
	
	@EventLink
	public void onGui(ClickGuiEvent e) {
		this.setSuffix("- [" + deffensive.getInput() + ", " + offensive.getInput() + "]");	
	}
	
    @EventLink
    public void onHitSlowDown(HitSlowDownEvent e) {
		if (chance.getInput() != 100.0D) {
			if (Math.random() >= chance.getInput() / 100.0D) {
				return;
			}
		}
    	
        if (mc.thePlayer.hurtTime > 0) {
        	e.setSlowDown(deffensive.getInput());
            e.setSprint(false);
        } else {
            e.setSlowDown(offensive.getInput());
            e.setSprint(true);
        }
    }
}