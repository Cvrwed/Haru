package cc.unknown.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.impl.combat.*;
import cc.unknown.module.impl.exploit.*;
import cc.unknown.module.impl.other.*;
import cc.unknown.module.impl.player.*;
import cc.unknown.module.impl.settings.*;
import cc.unknown.module.impl.visuals.*;
import cc.unknown.utils.Loona;
import net.minecraft.client.gui.FontRenderer;

public class ModuleManager implements Loona {
	private static List<Module> modules = new ArrayList<>();
	private boolean initialized = false;

	public ModuleManager() {
		if (initialized) return;
		addModule(
				// combat
				new AutoClick(),
				new AimAssist(),
				new AutoRod(),
				new AutoRefill(),
				new AutoBlock(),
				new JumpReset(),
				new KillAura(),
				new KeepSprint(),
				new Criticals(),
				new Reach(),
				new WTap(),
				new Velocity(),
				
				// exploit
				new ACDetector(),
				new ChatBypass(),
				new PingSpoof(),
				new LagRange(),
				new Fakelag(),
				new Test(),
				
				// other
				new MusicPlayer(),
				new Autoplay(),
				new AutoLeave(),
				new AutoTool(),
				new AntiBot(),
				new MidClick(),
				new Inventory(),
				
				// player
				new RightClick(),
				new InvManager(),
				new Stealer(),
				new FastPlace(),
				new LegitScaffold(),
				new BridgeAssist(),
				new Sprint(),
				new Blink(),
				new NoSlow(),
				new NoFall(),
				
				// visuals
				new Animations(),
				new Ambience(),
				new Fullbright(),
				new FreeLook(),
				new ClickGuiModule(),
				new HUD(),
				new CPSMod(),
				new TargetHUD(),
				new HitColor(),
				new Trajectories(),
				new NoHurtCam(),
				new Nametags(),
				new ESP(),
				
				// settings
				new Colors(),
				new Targets(),
				new Fixes()
				);
		
		initialized = true;
	}
	
	public void addModule(Module... s) {
		modules.addAll(Arrays.asList(s));
	}

	public Module getModule(String name) {
		if (!initialized) return null;

		for (Module m : modules) {
			if (m.getName().equalsIgnoreCase(name))
				return m;
		}
		return null;
	}

	public Module getModule(Class<? extends Module> c) {
		if (!initialized) return null;

		for (Module m : modules) {
			if (m.getClass().equals(c))
				return m;
		}
		return null;
	}
	   
	public List<Module> getModule(Class<?>[] array) {
	    if (!initialized) {
	        return Collections.emptyList();
	    }
	    
	    return modules.stream().filter(m -> Arrays.stream(array).anyMatch(c -> m.getClass().equals(c))).collect(Collectors.toList());
	}

	public List<Module> getModule() {
		return modules;
	}

	public List<Module> getCategory(ModuleCategory categ) {
		ArrayList<Module> modulesOfCat = new ArrayList<>();

		for (Module m : modules) {
			if (m.moduleCategory().equals(categ)) {
				modulesOfCat.add(m);
			}
		}
		return modulesOfCat;
	}

    public void sort() {
        modules.sort((o1, o2) -> mc.fontRendererObj.getStringWidth(o2.getName()) - mc.fontRendererObj.getStringWidth(o1.getName()));
    }

    public void sortLongShort() {
        modules.sort(Comparator.comparingInt(o2 -> mc.fontRendererObj.getStringWidth(o2.getName())));
    }

    public void sortShortLong() {
        modules.sort((o1, o2) -> mc.fontRendererObj.getStringWidth(o2.getName()) - mc.fontRendererObj.getStringWidth(o1.getName()));
    }

    public int getLongestActiveModule(FontRenderer fr) {
        int length = 0;
        for (Module mod : modules)
			if (mod.isEnabled())
				if (fr.getStringWidth(mod.getName()) > length)
					length = fr.getStringWidth(mod.getName());
        return length;
    }

    public int getBoxHeight(FontRenderer fr, int margin) {
        int length = 0;
        for (Module mod : modules)
			if (mod.isEnabled())
				length += fr.FONT_HEIGHT + margin;
        return length;
    }

	public int numberOfModules() {
		return modules.size();
	}

}
