package cc.unknown.module.setting.impl;

import com.google.gson.JsonObject;

import cc.unknown.module.setting.Setting;

public class DescValue extends Setting {
	private String desc;

	public DescValue(String t) {
		super(t);
		this.desc = t;
	}
	
	public String getDesc() {
		return this.desc;
	}

	public void setDesc(String t) {
		this.desc = t;
	}

	@Override
	public void resetToDefaults() {
		this.desc = "";
	}

	@Override
	public JsonObject getConfigAsJson() {
		JsonObject data = new JsonObject();
		data.addProperty("type", getSettingType());
		data.addProperty("value", getDesc());
		return data;
	}

	@Override
	public String getSettingType() {
		return "desc";
	}

	@Override
	public void applyConfigFromJson(JsonObject data) {
		if (!data.get("type").getAsString().equals(getSettingType()))
			return;

		setDesc(data.get("value").getAsString());
	}
}
