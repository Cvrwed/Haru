package cc.unknown;

import cc.unknown.command.CommandManager;
import cc.unknown.config.ClientConfig;
import cc.unknown.config.ConfigManager;
import cc.unknown.event.EventBus;
import cc.unknown.event.impl.other.ShutdownEvent;
import cc.unknown.module.ModuleManager;
import cc.unknown.ui.clickgui.raven.ClickGui;
import cc.unknown.utils.font.FontUtil;

public enum Haru {
	instance;

	private CommandManager commandManager;
	private ConfigManager configManager;
	private ClientConfig clientConfig;
	private ModuleManager moduleManager;
	private int realPosX;
	private int realPosY;
	private int realPosZ;

	private ClickGui clickGui;
	private EventBus eventBus = new EventBus();

	public void startClient() {
		FontUtil.bootstrap();
		moduleManager = new ModuleManager();
		commandManager = new CommandManager();
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

	public int getRealPosX() {
		return realPosX;
	}

	public void setRealPosX(int realPosX) {
		this.realPosX = realPosX;
	}

	public int getRealPosY() {
		return realPosY;
	}

	public void setRealPosY(int realPosY) {
		this.realPosY = realPosY;
	}

	public int getRealPosZ() {
		return realPosZ;
	}

	public void setRealPosZ(int realPosZ) {
		this.realPosZ = realPosZ;
	}
}