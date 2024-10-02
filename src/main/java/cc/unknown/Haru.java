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
import cc.unknown.ui.clickgui.raven.HaruGui;
import cc.unknown.utils.Loona;
import cc.unknown.utils.player.PlayerUtil;
import lombok.Getter;

@Getter
public enum Haru {
	instance;
	
	private CommandManager commandManager;
	private ConfigManager configManager;
	private HudConfig hudConfig;
	private ModuleManager moduleManager;
	private HaruGui haruGui;
	private EventBus eventBus = new EventBus();

	public void startClient() {
		eventBus.register(this);
	    eventBus.post(new GameEvent.StartEvent());
		commandManager = new CommandManager();
		moduleManager = new ModuleManager();
		haruGui = new HaruGui();
		configManager = new ConfigManager();
		hudConfig = new HudConfig();
		hudConfig.applyHud();
	}
	
	@EventLink
	public void onTickPost(TickEvent.Post event) {
		try {
			if (PlayerUtil.inGame()) {
				for (Module module : Haru.instance.getModuleManager().getModule()) {
					if (Loona.mc.currentScreen == null) {
						module.keybind();
					} else if (Loona.mc.currentScreen instanceof HaruGui) {
						Haru.instance.getEventBus().post(new ClickGuiEvent());
					}
				}
			}
		} catch (ConcurrentModificationException ignore) {
		}
	}
}