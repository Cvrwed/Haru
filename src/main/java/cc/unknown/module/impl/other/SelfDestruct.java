package cc.unknown.module.impl.other;

import java.io.File;

import cc.unknown.Haru;
import cc.unknown.command.CommandManager;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.utils.Loona;

@Register(name = "SelfDestruct", category = Category.Other)
public class SelfDestruct extends Module {
	
    private final File logsDirectory = new File(Loona.mc.mcDataDir + File.separator + "logs" + File.separator);
	
	@Override
	public void onEnable() {
		Haru.instance.getModuleManager().getModule().forEach(m -> m.setToggled(false));
		Haru.instance.getEventBus().unregister(new CommandManager());
		mc.displayGuiScreen(null);
		this.deleteLogs();
	}

    private void deleteLogs() {
        if (logsDirectory.exists()) {
            File[] files = logsDirectory.listFiles();
            if (files == null) return;

            for (File file : files) {
                if (file.getName().endsWith("log.gz")) {
                    file.delete();
                }
            }
        }
    }
}
