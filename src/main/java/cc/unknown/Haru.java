package cc.unknown;

import cc.unknown.command.CommandManager;
import cc.unknown.config.ClientConfig;
import cc.unknown.config.ConfigManager;
import cc.unknown.event.EventBus;
import cc.unknown.event.impl.other.ShutdownEvent;
import cc.unknown.module.ModuleManager;
import cc.unknown.ui.clickgui.raven.ClickGui;
import cc.unknown.utils.font.FontUtil;
import cc.unknown.utils.player.SilentManager;

public enum Haru {
	instance;
	
	private CommandManager commandManager;
	private ConfigManager configManager;
	private ClientConfig clientConfig;
	private ModuleManager moduleManager;

	private ClickGui clickGui;
	private EventBus eventBus = new EventBus();
	private final SilentManager silentManager = new SilentManager();
	
    public void startClient() {
		FontUtil.bootstrap();
		
		moduleManager = new ModuleManager();
     	commandManager = new CommandManager();
     	
     	eventBus.register(silentManager);
     	
     	clickGui = new ClickGui();

     	configManager = new ConfigManager();
     	clientConfig = new ClientConfig();
     	
     	clientConfig.applyConfig();
    }
	
	public void stopClient() {
		eventBus.post(new ShutdownEvent());
		clientConfig.saveConfig();
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

	public SilentManager getSilentManager() {
		return silentManager;
	}
}