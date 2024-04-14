package cc.unknown.module.setting.impl;

import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonObject;

import cc.unknown.module.setting.Setting;

public class ModeValue extends Setting {
    private int index;
    private List<String> list;

    public ModeValue(String name, String t, String... list) {
        super(name);
        this.list = Arrays.asList(list);
        setMode(t);
    }
	
	public String getMode() {
		if (this.index >= this.list.size() || this.index < 0)
			this.index = 0; 
		return this.list.get(this.index);
	}
	  
	public void setMode(String mode) {
		this.index = this.list.indexOf(mode);
	}
	  
	public boolean is(String mode) {
		if (this.index >= this.list.size() || this.index < 0)
			this.index = 0; 
		return ((String)this.list.get(this.index)).equals(mode);
	}
	
	@Override
	public JsonObject getConfigAsJson() {
		JsonObject data = new JsonObject();
	    data.addProperty("type", getSettingType());
	    data.addProperty("value", getMode());
	    return data;
	}
	
	@Override
	public String getSettingType() {
		return "mode";
	}
	  
	public void increment() {
		if (this.index < this.list.size() - 1) {
			this.index++;
		} else {
			this.index = 0;
		} 
	}
	  
	public void decrement() {
		if (this.index > 0) {
			this.index--;
		} else {
			this.index = this.list.size() - 1;
		} 
	}
	  
	public int getIndex() {
		return this.index;
	}
	  
	public List<String> getList() {
		return this.list;
	}
	  
	@Override
	public void resetToDefaults() {
		this.setMode("");
	}
	  
	@Override
	public void applyConfigFromJson(JsonObject data) {
		if (!data.get("type").getAsString().equals(getSettingType()))
			return; 
		String value = data.get("value").getAsString();
		setMode(value);
	}
}