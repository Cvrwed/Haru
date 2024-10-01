package cc.unknown.module.setting;

import com.google.gson.JsonObject;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class Setting {
	
	public String name;

    public abstract void resetToDefaults();
    
	public abstract JsonObject getConfigAsJson();

	public abstract String getSettingType();

	public abstract void applyConfigFromJson(JsonObject data);
	
}
