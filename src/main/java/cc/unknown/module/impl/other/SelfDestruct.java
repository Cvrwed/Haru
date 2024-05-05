package cc.unknown.module.impl.other;

import java.io.File;
import java.util.Arrays;

import cc.unknown.Haru;
import cc.unknown.command.CommandManager;
import cc.unknown.module.ModuleManager;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.impl.visuals.ClickGuiModule;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.utils.Loona;

@Register(name = "SelfDestruct", category = Category.Other)
public class SelfDestruct extends Module {

	private final File logsDirectory = new File(Loona.mc.mcDataDir + File.separator + "logs" + File.separator);

	private BooleanValue deleteLogs = new BooleanValue("Delete logs", true);
	private BooleanValue removePrefix = new BooleanValue("Remove Prefix", true);
	private BooleanValue removeClickgui = new BooleanValue("Remove ClickGui", true);
	private BooleanValue removeBind = new BooleanValue("Remove Binds", false);
	private BooleanValue hiddenModules = new BooleanValue("Hidden Modules", true);
	
	public SelfDestruct() {
		this.registerSetting(deleteLogs, removePrefix, removeClickgui, removeBind, hiddenModules);
	}
	
	@Override
	public void onEnable() {
	    if (deleteLogs.isToggled()) {
	        deleteLogs();
	    }
	    
	    CommandManager commandManager = Haru.instance.getCommandManager();
	    ModuleManager moduleManager = Haru.instance.getModuleManager();
	    ClickGuiModule clickGuiModule = (ClickGuiModule) moduleManager.getModule(ClickGuiModule.class);
	    
	    if (removePrefix.isToggled()) {
	        commandManager.setPrefix(" ");
	    }
	    
	    if (removeClickgui.isToggled() && clickGuiModule != null) {
	        clickGuiModule.setKey(0);
	    }
	    
	    if (removeBind.isToggled()) {
	        moduleManager.getModule().forEach(m -> m.setKey(0));
	    }
	    
	    if (hiddenModules.isToggled()) {
	        moduleManager.getModule().forEach(m -> m.setToggled(false));
	    }
	    
	    if (mc != null) {
	        mc.displayGuiScreen(null);
	    }
	}

    private void deleteLogs() {
        if (logsDirectory.exists()) {
            File[] files = logsDirectory.listFiles();
            if (files != null) {
                Arrays.stream(files).filter(file -> file.getName().endsWith("log.gz")).forEach(File::delete);
            }
        }
    }
}
