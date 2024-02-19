package cc.unknown.utils;

import java.util.Arrays;
import java.util.List;

import cc.unknown.Haru;
import cc.unknown.module.Module;
import cc.unknown.module.impl.exploit.ACDetector;
import cc.unknown.module.impl.exploit.ChatBypass;
import cc.unknown.module.impl.other.AntiBot;
import cc.unknown.module.impl.other.AutoLeave;
import cc.unknown.module.impl.other.Autoplay;
import cc.unknown.module.impl.other.MidClick;
import cc.unknown.module.impl.other.MusicPlayer;
import cc.unknown.module.impl.player.FastPlace;
import cc.unknown.module.impl.settings.Fixes;
import cc.unknown.module.impl.settings.Targets;
import cc.unknown.module.impl.visuals.Ambience;
import cc.unknown.module.impl.visuals.Animations;
import cc.unknown.module.impl.visuals.ESP;
import cc.unknown.module.impl.visuals.FreeLook;
import cc.unknown.module.impl.visuals.Fullbright;
import cc.unknown.module.impl.visuals.HitColor;
import cc.unknown.module.impl.visuals.Nametags;
import cc.unknown.module.impl.visuals.NoHurtCam;

public class VisibilityModules {
	public static void setVisible(boolean visible) {
	    if (Haru.instance.getModuleManager() != null) {
	        List<Class<? extends Module>> modules = Arrays.asList(
	            Ambience.class, NoHurtCam.class, AutoLeave.class, Fixes.class,
	            Fullbright.class, Animations.class, MusicPlayer.class, MidClick.class,
	            Targets.class, Nametags.class, FastPlace.class, ChatBypass.class,
	            ESP.class, ACDetector.class, AntiBot.class, Autoplay.class,
	            HitColor.class, FreeLook.class
	        );

	        List<Module> x = Haru.instance.getModuleManager().getModule(modules.toArray(new Class<?>[0]));

	        for (Module m : x) {
	            m.setVisibleInHud(visible);
	        }
	    }
	}
}
