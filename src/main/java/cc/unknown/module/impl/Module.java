package cc.unknown.module.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.google.gson.JsonObject;

import cc.unknown.Haru;
import cc.unknown.module.impl.api.ModuleInfo;
import cc.unknown.module.setting.Setting;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.utils.Loona;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Module implements Loona {
    private List<Setting> settings = new ArrayList<>();
    private String suffix = "";
    private boolean isToggled = false;
    private boolean enabled = false;
    private boolean hidden = true;
    private int key = 0;
    private ModuleInfo moduleInfo;

    public Module() {
        if (this.getClass().isAnnotationPresent(ModuleInfo.class)) {
            this.moduleInfo = this.getClass().getAnnotation(ModuleInfo.class);
            this.key = moduleInfo.key();
            this.enabled = moduleInfo.enable();
        } else {
            throw new RuntimeException("@ModuleInfo not found: " + this.getClass().getSimpleName());
        }
    }

    public JsonObject getConfigAsJson() {
        JsonObject settingsJson = new JsonObject();
        settings.forEach(setting -> settingsJson.add(setting.getName(), setting.getConfigAsJson()));

        JsonObject data = new JsonObject();
        data.addProperty("enabled", enabled);
        data.addProperty("keycode", key);
        data.add("settings", settingsJson);

        return data;
    }

    public void applyConfigFromJson(JsonObject data) {
        try {
            this.key = data.get("keycode").getAsInt();
            setToggled(data.get("enabled").getAsBoolean());

            JsonObject settingsData = data.get("settings").getAsJsonObject();
            settings.forEach(setting -> {
                if (settingsData.has(setting.getName())) {
                    setting.applyConfigFromJson(settingsData.get(setting.getName()).getAsJsonObject());
                }
            });
        } catch (NullPointerException ignored) {
        }
    }

    public void keybind() {
        if (this.key != 0 && this.canBeEnabled()) {
            if (!this.isToggled && Keyboard.isKeyDown(this.key)) {
                this.toggle();
                this.isToggled = true;
            } else if (!Keyboard.isKeyDown(this.key)) {
                this.isToggled = false;
            }
        }
    }

    public Setting getSettingAlternative(final String name) {
        return settings.stream()
                .filter(setting -> setting.getName().replaceAll(" ", "").equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public boolean canBeEnabled() {
        return true;
    }

    public void enable() {
        this.enabled = true;
        this.onEnable();
        Haru.instance.getEventBus().register(this);
    }

    public void disable() {
        this.enabled = false;
        this.onDisable();
        Haru.instance.getEventBus().unregister(this);
    }

    public void setToggled(boolean enabled) {
        if (enabled) {
            enable();
        } else {
            disable();
        }
    }

    public void registerSetting(Setting... s) {
        this.settings.addAll(Arrays.asList(s));
    }

    public void resetToDefaults() {
        this.key = 0;
        this.setToggled(enabled);
        settings.forEach(Setting::resetToDefaults);
    }

    public String getBindAsString() {
        return key == 0 ? "None" : Keyboard.getKeyName(key);
    }

    public void guiButtonToggled(BooleanValue b) {}

    public void toggle() {
        if (this.enabled) {
            this.disable();
        } else {
            this.enable();
        }
    }
    
    public void onEnable() {}

    public void onDisable() {}
}
