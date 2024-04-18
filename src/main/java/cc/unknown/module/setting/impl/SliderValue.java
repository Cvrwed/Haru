package cc.unknown.module.setting.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.google.gson.JsonObject;

import cc.unknown.module.setting.Setting;

public class SliderValue extends Setting {
	private final String name;
	private double value, max, min, interval;

	public SliderValue(String name, double value, double min, double max, double intervals) {
		super(name);
		this.name = name;
		this.value = value;
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
		this.value = 0.0;
	}

	@Override
	public JsonObject getConfigAsJson() {
		JsonObject data = new JsonObject();
		data.addProperty("type", getSettingType());
		data.addProperty("value", getInput());
		return data;
	}

	@Override
	public String getSettingType() {
		return "slider";
	}

	@Override
	public void applyConfigFromJson(JsonObject data) {
		if (!data.get("type").getAsString().equals(getSettingType()))
			return;

		setValue(data.get("value").getAsDouble());
	}

	public double getInput() {
		return r(this.value, 2);
	}

	public float getInputToFloat() {
		return (float) getInput();
	}

	public int getInputToInt() {
		return (int) getInput();
	}

	public long getInputToLong() {
		return (long) getInput();
	}

	public double getMin() {
		return this.min;
	}

	public double getMax() {
		return this.max;
	}

	public void setValue(double n) {
		n = check(n, this.min, this.max);
		n = (double) Math.round(n * (1.0D / this.interval)) / (1.0D / this.interval);
		this.value = n;
	}

	public static double check(double v, double i, double a) {
		v = Math.max(i, v);
		v = Math.min(a, v);
		return v;
	}

	public static double r(double v, int p) {
		if (p < 0) {
			return 0.0D;
		} else {
			BigDecimal bd = new BigDecimal(v);
			bd = bd.setScale(p, RoundingMode.HALF_UP);
			return bd.doubleValue();
		}
	}
}
