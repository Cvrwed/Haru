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

    private volatile boolean initialized = false;

    private CommandManager commandManager;
    private ConfigManager configManager;
    private ClientConfig clientConfig;
    private ModuleManager moduleManager;

    private ClickGui clickGui;
    private EventBus eventBus = new EventBus();
    private final SilentManager silentManager = new SilentManager();

    public synchronized void startClient() {
        if (!initialized) {
        	initialized = true;
            FontUtil.bootstrap();
            moduleManager = new ModuleManager();
            commandManager = new CommandManager();
            eventBus.register(silentManager);
            clickGui = new ClickGui();
            configManager = new ConfigManager();
            clientConfig = new ClientConfig();
            clientConfig.applyConfig();
        }
    }

    public void stopClient() {
        eventBus.post(new ShutdownEvent());
        clientConfig.saveConfig();
    }

    public CommandManager getCommandManager() {
    	checkRun();
        return commandManager;
    }

    public ConfigManager getConfigManager() {
    	checkRun();
        return configManager;
    }

    public ClientConfig getClientConfig() {
    	checkRun();
        return clientConfig;
    }

    public ModuleManager getModuleManager() {
    	checkRun();
        return moduleManager;
    }

    public ClickGui getClickGui() {
    	checkRun();
        return clickGui;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public SilentManager getSilentManager() {
        return silentManager;
    }

    private void checkRun() {
        if (!initialized) {
            throw new IllegalStateException("Client not started yet");
        }
    }
}