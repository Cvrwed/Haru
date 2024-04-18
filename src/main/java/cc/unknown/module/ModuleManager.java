package cc.unknown.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import cc.unknown.Haru;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.combat.AimAssist;
import cc.unknown.module.impl.combat.AutoBlock;
import cc.unknown.module.impl.combat.AutoClick;
import cc.unknown.module.impl.combat.AutoRefill;
import cc.unknown.module.impl.combat.AutoRod;
import cc.unknown.module.impl.combat.BlockHit;
import cc.unknown.module.impl.combat.Criticals;
import cc.unknown.module.impl.combat.JumpReset;
import cc.unknown.module.impl.combat.KeepSprint;
import cc.unknown.module.impl.combat.Reach;
import cc.unknown.module.impl.combat.Velocity;
import cc.unknown.module.impl.combat.WTap;
import cc.unknown.module.impl.exploit.ChatBypass;
import cc.unknown.module.impl.exploit.FakeLag;
import cc.unknown.module.impl.exploit.PingSpoof;
import cc.unknown.module.impl.other.AutoLeave;
import cc.unknown.module.impl.other.AutoTool;
import cc.unknown.module.impl.other.Autoplay;
import cc.unknown.module.impl.other.Inventory;
import cc.unknown.module.impl.other.MidClick;
import cc.unknown.module.impl.other.SelfDestruct;
import cc.unknown.module.impl.other.Tweaks;
import cc.unknown.module.impl.player.AntiFireball;
import cc.unknown.module.impl.player.Blink;
import cc.unknown.module.impl.player.BridgeAssist;
import cc.unknown.module.impl.player.FastPlace;
import cc.unknown.module.impl.player.InvManager;
import cc.unknown.module.impl.player.LegitScaffold;
import cc.unknown.module.impl.player.NoFall;
import cc.unknown.module.impl.player.NoSlow;
import cc.unknown.module.impl.player.Sprint;
import cc.unknown.module.impl.player.Stealer;
import cc.unknown.module.impl.visuals.Ambience;
import cc.unknown.module.impl.visuals.ClickGuiModule;
import cc.unknown.module.impl.visuals.CpsDisplay;
import cc.unknown.module.impl.visuals.ESP;
import cc.unknown.module.impl.visuals.FreeLook;
import cc.unknown.module.impl.visuals.Fullbright;
import cc.unknown.module.impl.visuals.HUD;
import cc.unknown.module.impl.visuals.Nametags;
import cc.unknown.module.impl.visuals.TargetHUD;
import cc.unknown.module.impl.visuals.Trajectories;
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
				new BlockHit(),
				new Reach(),
				new WTap(),
				new Velocity(),
				
				// exploit
				new ChatBypass(),
				new PingSpoof(),
				new FakeLag(),
				
				// other
				new Autoplay(),
				new AutoLeave(),
				new AutoTool(),
				new Tweaks(),
				new SelfDestruct(),
				new MidClick(),
				new Inventory(),
				
				// player
				new InvManager(),
				new AntiFireball(),
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
				new TargetHUD(),
				new Trajectories(),
				new Nametags(),
				new ESP()
				);
		
		initialized = true;
	}
	
	public void addModule(Module... s) {
		modules.addAll(Arrays.asList(s));
	}

    public Module getModule(String name) {
        return initialized ? modules.stream().filter(module -> module.getRegister().name().equalsIgnoreCase(name)).findFirst().orElse(null) : null;
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

    public List<Module> getCategory(Category category) {
        return initialized ? modules.stream().filter(module -> module.getRegister().category().equals(category)).collect(Collectors.toList()) : Collections.emptyList();
    }

    public void sort() {
    	HUD hud = (HUD) Haru.instance.getModuleManager().getModule(HUD.class); 
    	modules.sort((o1, o2) -> mc.fontRendererObj.getStringWidth(o2.getRegister().name() + (hud.suffix.isToggled() ? " " + o2.getSuffix() : "")) - mc.fontRendererObj.getStringWidth(o1.getRegister().name() + (hud.suffix.isToggled() ? " " + o1.getSuffix() : "")));
    }

    public int getLongestActiveModule(FontRenderer fontRenderer) {
        return initialized ? modules.stream().filter(Module::isEnabled).mapToInt(module -> fontRenderer.getStringWidth(module.getRegister().name())).max().orElse(0) : 0;
    }

    public int getBoxHeight(FontRenderer fontRenderer, int margin) {
        return initialized ? modules.stream().filter(Module::isEnabled).mapToInt(module -> fontRenderer.FONT_HEIGHT + margin).sum() : 0;
    }

    public int numberOfModules() {
        return modules.size();
    }
}
