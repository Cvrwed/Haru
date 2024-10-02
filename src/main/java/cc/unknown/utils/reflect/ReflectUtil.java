package cc.unknown.utils.reflect;

import java.io.File;
import java.lang.reflect.Field;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;

@UtilityClass
public class ReflectUtil {	
    public boolean isOptifineLoaded() {
        File modsFolder = new File(Minecraft.getMinecraft().mcDataDir, "run/mods");

        if (!modsFolder.exists() || !modsFolder.isDirectory()) {
            modsFolder = new File(Minecraft.getMinecraft().mcDataDir, "mods");
        }

        if (modsFolder.exists() && modsFolder.isDirectory()) {
            File[] modFiles = modsFolder.listFiles((dir, name) -> name.toLowerCase().contains("optifine") && name.endsWith(".jar"));

            if (modFiles != null && modFiles.length > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isShaders() {
        try {
            Class<?> configClass = Class.forName("Config");
            return (boolean) configClass.getMethod("isShaders").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setGameSetting(Minecraft mc, String fieldName, boolean value) {
        try {
            Field field = mc.gameSettings.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(mc.gameSettings, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
