  package cc.unknown;

import cc.unknown.command.CommandManager;
import cc.unknown.config.ClientConfig;
import cc.unknown.config.ConfigManager;
import cc.unknown.event.impl.api.EventBus;
import cc.unknown.event.impl.other.StartGameEvent;
import cc.unknown.module.ModuleManager;
import cc.unknown.ui.clickgui.raven.ClickGui;
import cc.unknown.utils.font.FontUtil;

public enum Haru {
	instance;
	
	private CommandManager commandManager;
	private ConfigManager configManager;
	private ClientConfig clientConfig;
	private ModuleManager moduleManager;

	private ClickGui clickGui;
	private EventBus eventBus = new EventBus();

	public void startClient() {
	    eventBus.post(new StartGameEvent());
		FontUtil.bootstrap();
		commandManager = new CommandManager();
		moduleManager = new ModuleManager();
		clickGui = new ClickGui();
		configManager = new ConfigManager();
		clientConfig = new ClientConfig();
		clientConfig.applyConfig();
	}

	public void saveConfig() {
		clientConfig.saveConfig();
		configManager.save();
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

	public ClientConfig getClientConfig() {
		return clientConfig;
	}

	public ModuleManager getModuleManager() {
		return moduleManager;
	}

	public ClickGui getClickGui() {
		return clickGui;
	}

	public EventBus getEventBus() {
		return eventBus;
	}
}