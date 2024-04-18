package cc.unknown.module.setting.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.google.gson.JsonObject;

import cc.unknown.module.setting.Setting;

public class DoubleSliderValue extends Setting {
	private final String name;
    private double valMax, valMin, max, min, interval;

    public DoubleSliderValue(String name, double valMin, double valMax, double min, double max, double intervals) {
        super(name);
        this.name = name;
        this.valMin = valMin;
        this.valMax = valMax;
        this.min = min;
        this.max = max;
        this.interval = intervals;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void resetToDefaults() {
        this.setValueMin(0.0);
        this.setValueMax(0.0);
    }

    @Override
    public JsonObject getConfigAsJson() {
        JsonObject data = new JsonObject();
        data.addProperty("type", getSettingType());
        data.addProperty("valueMin", getInputMin());
        data.addProperty("valueMax", getInputMax());
        return data;
    }

    @Override
    public String getSettingType() {
        return "doubleslider";
    }

    @Override
    public void applyConfigFromJson(JsonObject data) {
        if(!data.get("type").getAsString().equals(getSettingType()))
            return;

        setValueMax(data.get("valueMax").getAsDouble());
        setValueMin(data.get("valueMin").getAsDouble());
    }

    public double getInputMin() {
        return round(this.valMin, 2);
    }
    public double getInputMax() {
        return round(this.valMax, 2);
    }
    
    public long getInputMinToLong() {
        return (long) round(this.valMin, 2);
    }

    public long getInputMaxToLong() {
        return (long) round(this.valMax, 2);
    }
    
    public int getInputMinToInt() {
        return (int) round(this.valMin, 2);
    }

    public int getInputMaxToInt() {
        return (int) round(this.valMax, 2);
    }
    
    public float getInputMinToFloat() {
    	return (float) round(this.valMin, 2);
    }
    
    public float getInputMaxToFloat() {
    	return (float) round(this.valMax, 2);
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }

    public void setValueMin(double n) {
        n = correct(n, this.min, this.valMax);
        n = (double)Math.round(n * (1.0D / this.interval)) / (1.0D / this.interval);
        this.valMin = n;
    }

    public void setValueMax(double n) {
        n = correct(n, this.valMin, this.max);
        n = (double)Math.round(n * (1.0D / this.interval)) / (1.0D / this.interval);
        this.valMax = n;
    }

    public static double correct(double val, double min, double max) {
        val = Math.max(min, val);
        val = Math.min(max, val);
        return val;
    }

    public static double round(double val, int p) {
        if (p < 0) {
            return 0.0D;
        } else {
            BigDecimal bd = new BigDecimal(val);
            bd = bd.setScale(p, RoundingMode.HALF_UP);
            return bd.doubleValue();
        }
    }
}
