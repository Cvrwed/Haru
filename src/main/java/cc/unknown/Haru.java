package cc.unknown;

import java.util.ConcurrentModificationException;

import cc.unknown.command.CommandManager;
import cc.unknown.config.ConfigManager;
import cc.unknown.config.HudConfig;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.api.EventBus;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.event.impl.other.GameEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.ModuleManager;
import cc.unknown.module.impl.Module;
import cc.unknown.ui.clickgui.raven.ClickGUI;
import cc.unknown.utils.Loona;
import cc.unknown.utils.player.PlayerUtil;
import cc.unknown.utils.reflect.ReflectUtil;
import lombok.Getter;
import net.minecraft.client.Minecraft;

@Getter
public enum Haru {
	instance;
	
	private CommandManager commandManager;
	private ConfigManager configManager;
	private HudConfig hudConfig;
	private ModuleManager moduleManager;
	private ClickGUI haruGui;
	private EventBus eventBus = new EventBus();
	public int settingCounter;

	public void startClient() {
		eventBus.register(this);
	    eventBus.post(new GameEvent.StartEvent());
		commandManager = new CommandManager();
		moduleManager = new ModuleManager();
		haruGui = new ClickGUI();
		configManager = new ConfigManager();
		hudConfig = new HudConfig();
		hudConfig.applyHud();
		
		getOptimization(Minecraft.getMinecraft());
	}
	
	@EventLink
	public void onTickPost(TickEvent.Post event) {
		try {
			if (PlayerUtil.inGame()) {
				for (Module module : getModuleManager().getModule()) {
					if (Loona.mc.currentScreen == null) {
						module.keybind();
					} else if (Loona.mc.currentScreen instanceof ClickGUI) {
						getEventBus().post(new ClickGuiEvent());
					}
				}
			}
		} catch (ConcurrentModificationException ignore) {
		}
	}
	
    public void getOptimization(Minecraft mc) {
        if (ReflectUtil.isOptifineLoaded()) {
            try {
            	ReflectUtil.setGameSetting(mc, "ofFastRender", !ReflectUtil.isShaders());
            	ReflectUtil.setGameSetting(mc, "ofChunkUpdatesDynamic", true);
            	ReflectUtil.setGameSetting(mc, "ofSmartAnimations", true);
            	ReflectUtil.setGameSetting(mc, "ofShowGlErrors", false);
            	ReflectUtil.setGameSetting(mc, "ofRenderRegions", true);
            	ReflectUtil.setGameSetting(mc, "ofSmoothFps", false);
                ReflectUtil.setGameSetting(mc, "ofFastMath", true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mc.gameSettings.useVbo = true;
    }
}