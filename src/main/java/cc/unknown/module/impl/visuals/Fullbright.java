package cc.unknown.module.impl.visuals;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.UpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class Fullbright extends Module {
	
	private ModeValue mode = new ModeValue("Mode", "Gamma", "Gamma", "Night Vision");
	public BooleanValue confusion = new BooleanValue("Remove confusion effect", true);
	public SliderValue fire = new SliderValue("Fire Alpha", 0.0, 0.0, 1, 0.1);
	private float prevGamma = 0f;

    public Fullbright() {
        super("Fullbright", ModuleCategory.Visuals);
        this.registerSetting(mode, confusion, fire);
    }
    
    @Override
    public void onEnable() {
        prevGamma = mc.gameSettings.gammaSetting;
    }

    @Override
    public void onDisable() {
        if (prevGamma == 0f) return;
        mc.gameSettings.gammaSetting = prevGamma;
        prevGamma = 0f;
        if (mc.thePlayer != null)
        	mc.thePlayer.removePotionEffectClient(Potion.nightVision.id);
    }
    
    @EventLink
    public void onUpdate(UpdateEvent e) {
    	if(mode.is("Gamma")) {
			if (mc.gameSettings.gammaSetting <= 100f) mc.gameSettings.gammaSetting++;
    	} else if (mode.is("Night Vision")) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.nightVision.id, 1337, 1));
    	} else if (prevGamma != 0f) {
            mc.gameSettings.gammaSetting = prevGamma;
            prevGamma = 0f;
        }
    }
}
