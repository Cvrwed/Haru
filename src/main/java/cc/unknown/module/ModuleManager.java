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
	private final List<Module> modules = new ArrayList<>();
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
				new KeepSprint(),
				new Criticals(),
				new Reach(),
				new WTap(),
				new Velocity(),
				
				// exploit
				new ChatBypass(),
				new PingSpoof(),
				new FakeLag(),
				new BackTrack(),
				
				// other
				new Autoplay(),
				new AutoLeave(),
				new AutoTool(),
				new SelfDestruct(),
				new MidClick(),
				new Inventory(),
				
				// player
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
				new Ambience(),
				new Fullbright(),
				new FreeLook(),
				new ClickGuiModule(),
				new HUD(),
				new CpsDisplay(),
				new KeystrokesDisplay(),
				new TargetHUD(),
				new Trajectories(),
				new Nametags(),
				new ESP(),
				
				// settings
				new Colors(),
				new Targets(),
				new Tweaks()
				);
		
		initialized = true;
	}
	
	public void addModule(Module... s) {
		modules.addAll(Arrays.asList(s));
	}

    public Module getModule(String name) {
        return initialized ? modules.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst().orElse(null) : null;
    }

    public Module getModule(Class<? extends Module> clazz) {
        return initialized ? modules.stream().filter(module -> module.getClass().equals(clazz)).findFirst().orElse(null) : null;
    }

    public List<Module> getModule() {
        return modules;
    }

    public List<Module> getModule(Class<?>[] classes) {
        return initialized ? modules.stream().filter(module -> Arrays.stream(classes).anyMatch(clazz -> module.getClass().equals(clazz))).collect(Collectors.toList()) : Collections.emptyList();
    }

    public List<Module> getCategory(ModuleCategory category) {
        return initialized ? modules.stream().filter(module -> module.moduleCategory().equals(category)).collect(Collectors.toList()) : Collections.emptyList();
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

    public int getLongestActiveModule(FontRenderer fontRenderer) {
        return initialized ? modules.stream().filter(Module::isEnabled).mapToInt(module -> fontRenderer.getStringWidth(module.getName())).max().orElse(0) : 0;
    }

    public int getBoxHeight(FontRenderer fontRenderer, int margin) {
        return initialized ? modules.stream().filter(Module::isEnabled).mapToInt(module -> fontRenderer.FONT_HEIGHT + margin).sum() : 0;
    }

    public int numberOfModules() {
        return modules.size();
    }
}
