package cc.unknown.ui.clickgui.raven.impl.api;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import cc.unknown.Haru;
import cc.unknown.module.impl.visuals.ClickGuiModule;
import cc.unknown.utils.client.ColorUtil;

public enum Theme {
	instance;
    private Supplier<Color> backColorSupplier = () -> new Color(0, 0, 0, 100);
    private final Map<String, Supplier<Color>> colorMap = new HashMap<>();

    {
        colorMap.put("Lilith", () -> ColorUtil.reverseGradientDraw(new Color(76, 56, 108), new Color(255, 51, 51), new Color(76, 56, 108), 5));
        colorMap.put("Rainbow", () -> Color.getHSBColor((float) (System.currentTimeMillis() % (15000L / 3)) / (15000.0F / 3), 1.0F, 1.0F));
        colorMap.put("Pastel", () -> ColorUtil.reverseGradientDraw(new Color(255, 190, 190), new Color(255, 190, 255), 2));
        colorMap.put("Memories", () -> ColorUtil.reverseGradientDraw(new Color(255, 0, 255), new Color(255, 255, 0), new Color(255, 0, 158), 2));
        colorMap.put("Cantina", () -> ColorUtil.gradientDraw(new Color(255, 0, 0), new Color(0, 0, 255), 7));
    }

    public Color getMainColor() {
        ClickGuiModule clickgui = (ClickGuiModule) Haru.instance.getModuleManager().getModule(ClickGuiModule.class);
        return colorMap.getOrDefault(clickgui.clientTheme.getMode(), () -> Color.getHSBColor((clickgui.clickGuiColor.getInputToFloat() % 360) / 360.0f, clickgui.saturation.getInputToFloat(), clickgui.brightness.getInputToFloat())).get();
    }
    
    public Color getBackColor() {
        return backColorSupplier.get();
    }
}