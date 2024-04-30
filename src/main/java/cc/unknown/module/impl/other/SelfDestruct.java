package cc.unknown.module.impl.other;

import java.io.File;

import cc.unknown.Haru;
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
	
	public SelfDestruct() {
		this.registerSetting(deleteLogs, removePrefix, removeClickgui);
	}
	
	@Override
	public void onEnable() {
		if (deleteLogs.isToggled()) {
			deleteLogs();
		}
		
		if (removePrefix.isToggled()) {
			Haru.instance.getCommandManager().setPrefix(" ");
		}
		
		if (removeClickgui.isToggled()) {
			Haru.instance.getModuleManager().getModule(ClickGuiModule.class).setKey(0);
		}
		
		Haru.instance.getModuleManager().getModule().forEach(m -> m.setToggled(false));
		mc.displayGuiScreen(null);

	}

	private void deleteLogs() {
		if (logsDirectory.exists()) {
			File[] files = logsDirectory.listFiles();
			if (files == null)
				return;

			for (File file : files) {
				if (file.getName().endsWith("log.gz")) {
					file.delete();
				}
			}
		}
	}
}
