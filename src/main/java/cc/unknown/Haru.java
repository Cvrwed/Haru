package cc.unknown;

import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cc.unknown.command.CommandManager;
import cc.unknown.config.ConfigManager;
import cc.unknown.config.HudConfig;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.api.EventBus;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.event.impl.other.GameEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.ModuleManager;
import cc.unknown.ui.clickgui.raven.ClickGUI;
import cc.unknown.utils.Loona;
import cc.unknown.utils.font.FontUtil;
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
	

	public void startClient() {
		eventBus.register(this);
	    eventBus.post(new GameEvent.StartEvent());
	    FontUtil.bootstrap();
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
            Minecraft mc = Loona.mc;
            moduleManager.getModule().forEach(module -> {
                if (mc.currentScreen == null) {
                    module.keybind();
                } else if (mc.currentScreen instanceof ClickGUI) {
                    eventBus.post(new ClickGuiEvent());
                }
            });
        } catch (ConcurrentModificationException e) {
        }
    }
	
    private void getOptimization(Minecraft mc) {
        if (ReflectUtil.isOptifineLoaded()) {
            try {
                Map<String, Boolean> settings = Stream.of(new Object[][]{
                        {"ofFastRender", !ReflectUtil.isShaders()},
                        {"ofChunkUpdatesDynamic", true},
                        {"ofSmartAnimations", true},
                        {"ofShowGlErrors", false},
                        {"ofRenderRegions", true},
                        {"ofSmoothFps", false},
                        {"ofFastMath", true}
                }).collect(Collectors.toMap(data -> (String) data[0], data -> (Boolean) data[1]));

                settings.forEach((key, value) -> ReflectUtil.setGameSetting(mc, key, value));
            } catch (Exception e) {
            }
        }
        mc.gameSettings.useVbo = true;
    }
}