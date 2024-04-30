package cc.unknown.module.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.google.gson.JsonObject;

import cc.unknown.Haru;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.Setting;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.utils.Loona;

public class Module implements Loona {
	private List<Setting> settings = new ArrayList<>();
	private String suffix = "";
	private boolean isToggled = false;
	private boolean enabled = false;
	private boolean hidden = true;
	private int key = 0;
	private Register register;

	public Module() {
		if (this.getClass().isAnnotationPresent(Register.class)) {
			this.register = this.getClass().getAnnotation(Register.class);
			this.key = getRegister().key();
			this.enabled = getRegister().enable();
		} else {
			throw new RuntimeException("@Register not found" + this.getClass().getSimpleName());
		}
	}

	public JsonObject getConfigAsJson() {
		JsonObject settings = new JsonObject();

		for (Setting setting : this.settings) {
			JsonObject settingData = setting.getConfigAsJson();
			settings.add(setting.getName(), settingData);
		}

		JsonObject data = new JsonObject();
		data.addProperty("enabled", enabled);
		data.addProperty("keycode", key);
		data.add("settings", settings);

		return data;
	}

	public void applyConfigFromJson(JsonObject data) {
		try {
			this.key = data.get("keycode").getAsInt();
			setToggled(data.get("enabled").getAsBoolean());
			JsonObject settingsData = data.get("settings").getAsJsonObject();
			for (Setting setting : getSettings()) {
				if (settingsData.has(setting.getName())) {
					setting.applyConfigFromJson(settingsData.get(setting.getName()).getAsJsonObject());
				}
			}
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
        for (final Setting setting : settings) {
            final String comparingName = setting.getName().replaceAll(" ", "");

            if (comparingName.equalsIgnoreCase(name)) {
                return setting;
            }
        }

        return null;
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

	public List<Setting> getSettings() {
		return this.settings;
	}

	public void registerSetting(Setting... s) {
		this.settings.addAll(Arrays.asList(s));
	}

	public boolean isEnabled() {
		return this.enabled;
	}
	
	public void onEnable() {
	}
	
	public void onDisable() {
	}

	public void guiButtonToggled(BooleanValue b) {
	}

	public void toggle() {
		if (this.enabled) {
			this.disable();
		} else {
			this.enable();
		}
	}

	public void resetToDefaults() {
		this.key = 0;
		this.setToggled(enabled);

		for (Setting setting : this.settings) {
			setting.resetToDefaults();
		}
	}

	public String getBindAsString() {
		return key == 0 ? "None" : Keyboard.getKeyName(key);
	}
	
	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}
	
	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public Register getRegister() {
		return register;
	}
}
