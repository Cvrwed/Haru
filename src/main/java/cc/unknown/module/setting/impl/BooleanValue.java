package cc.unknown.module.setting.impl;

import com.google.gson.JsonObject;

import cc.unknown.module.setting.Setting;

public class BooleanValue extends Setting {
	private final String name;
	private boolean isEnabled;

	public BooleanValue(String name, boolean isEnabled) {
		super(name);
		this.name = name;
		this.isEnabled = isEnabled;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void resetToDefaults() {
		this.isEnabled = false;
	}

	@Override
	public JsonObject getConfigAsJson() {
		JsonObject data = new JsonObject();
		data.addProperty("type", getSettingType());
		data.addProperty("value", isToggled());
		return data;
	}

	@Override
	public String getSettingType() {
		return "bool";
	}

	@Override
	public void applyConfigFromJson(JsonObject data) {
		if (!data.get("type").getAsString().equals(getSettingType()))
			return;

		setEnabled(data.get("value").getAsBoolean());
	}

	public boolean isToggled() {
		return this.isEnabled;
	}

	public void toggle() {
		this.isEnabled = !this.isEnabled;
	}

	public void enable() {
		this.isEnabled = true;
	}

	public void disable() {
		this.isEnabled = false;
	}

	public void setEnabled(boolean b) {
		this.isEnabled = b;
	}
}
