package cc.unknown.utils.misc;

import java.util.Arrays;
import java.util.List;

import cc.unknown.Haru;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.other.AutoLeave;
import cc.unknown.module.impl.other.Autoplay;
import cc.unknown.module.impl.other.Inventory;
import cc.unknown.module.impl.other.MidClick;
import cc.unknown.module.impl.other.Tweaks;
import cc.unknown.module.impl.player.Sprint;
import cc.unknown.module.impl.visuals.Ambience;
import cc.unknown.module.impl.visuals.ClickGuiModule;
import cc.unknown.module.impl.visuals.CpsDisplay;
import cc.unknown.module.impl.visuals.ESP;
import cc.unknown.module.impl.visuals.FreeLook;
import cc.unknown.module.impl.visuals.Fullbright;
import cc.unknown.module.impl.visuals.HUD;
import cc.unknown.module.impl.visuals.Keystrokes;
import cc.unknown.module.impl.visuals.Nametags;
import cc.unknown.module.impl.visuals.TargetHUD;
import cc.unknown.module.impl.visuals.Trajectories;

public class HiddenUtil {
	public static void setVisible(boolean visible) {
	    if (Haru.instance.getModuleManager() != null) {
	        List<Class<? extends Module>> modules = Arrays.asList(
	        		Tweaks.class,
	        		
	        		Ambience.class,
	        		ClickGuiModule.class,
	        		Fullbright.class,
	        		HUD.class,
	        		Nametags.class,
	        		CpsDisplay.class,
	        		ESP.class,
	        		FreeLook.class,
	        		Fullbright.class,
	        		Nametags.class,
	        		TargetHUD.class,
	        		Trajectories.class,
	        		
	        		Sprint.class,
	        		
	        		AutoLeave.class,
	        		Autoplay.class,
	        		Inventory.class,
	        		Keystrokes.class,
	        		MidClick.class

	        );

	        List<Module> x = Haru.instance.getModuleManager().getModule(modules.toArray(new Class<?>[0]));

	        for (Module m : x) {
	            m.setHidden(visible);
	        }
	    }
	}
}
