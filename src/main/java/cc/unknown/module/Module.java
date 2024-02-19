package cc.unknown.module;

import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import com.google.gson.JsonObject;

import cc.unknown.Haru;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.Setting;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.utils.interfaces.Loona;

public class Module implements Loona {
	public ArrayList<Setting> settings;
	private final String moduleName;
	private final ModuleCategory moduleCategory;
	protected boolean enabled = false;
	protected boolean hidden = true;
	protected int key = 0;
	protected boolean defaultEnabled = enabled;
	protected int defualtKey = key;
	private boolean isToggled = false;
	protected boolean oldState;

	public Module(String name, ModuleCategory ModuleCategory) {
		this.moduleName = name;
		this.moduleCategory = ModuleCategory;
		this.settings = new ArrayList<>();
	}

	protected <E extends Module> E withKeycode(int keyCode, Class<E> type) {
	    this.key = keyCode;
	    this.defualtKey = keyCode;
	    return type.cast(this);
	}
	
	protected <E extends Module> E withEnabled(boolean isEnabled, Class<E> type) {
	    this.enabled = isEnabled;
	    this.defaultEnabled = isEnabled;
	    try {
	        setToggled(isEnabled);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return type.cast(this);
	}

	public JsonObject getConfigAsJson() {
		JsonObject settings = new JsonObject();

		for (Setting setting : this.settings) {
			JsonObject settingData = setting.getConfigAsJson();
			settings.add(setting.settingName, settingData);
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

	public boolean canBeEnabled() {
		return true;
	}

	public void setVisibleInHud(boolean vis) {
		this.hidden = vis;
	}

	public boolean showInHud() {
		return hidden;
	}

	public void enable() {
		this.oldState = this.enabled;
		this.enabled = true;
		this.onEnable();
		Haru.instance.getEventBus().register(this);
	}

	public void disable() {
		this.oldState = this.enabled;
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

	public String getName() {
		return this.moduleName;
	}

	public ArrayList<Setting> getSettings() {
		return this.settings;
	}

	public Setting getSettingByName(String name) {
		for (Setting setting : this.settings) {
			if (setting.getName().equalsIgnoreCase(name))
				return setting;
		}
		return null;
	}

	public void registerSetting(Setting... s) {
		this.settings.addAll(Arrays.asList(s));
	}

	public ModuleCategory moduleCategory() {
		return this.moduleCategory;
	}

	public boolean isEnabled() {
		return this.enabled;
	}
	
	public void onEnable() {
	}

	public void onDisable() {
	}

	public void update() {
	}

	public void postApplyConfig() {
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
		this.key = defualtKey;
		this.setToggled(defaultEnabled);

		for (Setting setting : this.settings) {
			setting.resetToDefaults();
		}
	}

	public void onGuiClose() {
	}

	public String getBindAsString() {
		return key == 0 ? "None" : Keyboard.getKeyName(key);
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}
}
