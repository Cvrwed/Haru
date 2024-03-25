package cc.unknown.module.impl.other;

import java.io.File;

import cc.unknown.Haru;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.utils.Loona;

public class SelfDestruct extends Module {
	
    private final File logsDirectory = new File(Loona.mc.mcDataDir + File.separator + "logs" + File.separator);

	public SelfDestruct() {
		super("SelfDestruct", ModuleCategory.Other);
	}
	
	@Override
	public void onEnable() {
		Haru.instance.getModuleManager().getModule().forEach(m -> m.setToggled(false));
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
