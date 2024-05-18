package cc.unknown;

import cc.unknown.command.CommandManager;
import cc.unknown.config.HudConfig;
import cc.unknown.config.ConfigManager;
import cc.unknown.event.impl.api.EventBus;
import cc.unknown.event.impl.other.GameEvent;
import cc.unknown.module.ModuleManager;
import cc.unknown.ui.clickgui.raven.HaruGui;
import cc.unknown.utils.player.RotationUtils;

public enum Haru {
	instance;
	
	public RotationUtils rotationUtils;
	private CommandManager commandManager;
	private ConfigManager configManager;
	private HudConfig hudConfig;
	private ModuleManager moduleManager;

	private HaruGui haruGui;
	private EventBus eventBus = new EventBus();

	public void startClient() {
	    eventBus.post(new GameEvent.StartEvent());
	    rotationUtils = new RotationUtils();
		commandManager = new CommandManager();
		moduleManager = new ModuleManager();
		haruGui = new HaruGui();
		configManager = new ConfigManager();
		hudConfig = new HudConfig();
		hudConfig.applyHud();
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

	public HudConfig getHudConfig() {
		return hudConfig;
	}

	public ModuleManager getModuleManager() {
		return moduleManager;
	}

	public HaruGui getHaruGui() {
		return haruGui;
	}

	public EventBus getEventBus() {
		return eventBus;
	}
}