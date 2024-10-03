package cc.unknown.ui.clickgui.raven.impl.api;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import cc.unknown.Haru;
import cc.unknown.module.impl.visuals.ClickGui;
import cc.unknown.utils.client.ColorUtil;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Theme {
    private final Supplier<Color> backColorSupplier = () -> new Color(0, 0, 0, 100);
    private final Map<String, Supplier<Color>> colorMap = new HashMap<>();

    static {
        initializeColorMap();
    }

    private void initializeColorMap() {
        colorMap.put("Lilith", () -> ColorUtil.reverseGradientDraw(new Color(76, 56, 108), new Color(255, 51, 51), new Color(76, 56, 108), 5));
        colorMap.put("Rainbow", () -> Color.getHSBColor((float) (System.currentTimeMillis() % 15000) / 15000, 1.0F, 1.0F));
        colorMap.put("Pastel", () -> ColorUtil.reverseGradientDraw(new Color(255, 190, 190), new Color(255, 190, 255), 2));
        colorMap.put("Memories", () -> ColorUtil.reverseGradientDraw(new Color(255, 0, 255), new Color(255, 255, 0), new Color(255, 0, 158), 2));
        colorMap.put("Cantina", () -> ColorUtil.gradientDraw(new Color(255, 0, 0), new Color(0, 0, 255), 7));
    }

    public Color getMainColor() {
        ClickGui clickgui = (ClickGui) Haru.instance.getModuleManager().getModule(ClickGui.class);
        return Optional.ofNullable(colorMap.get(clickgui.clientTheme.getMode()))
                .map(Supplier::get)
                .orElseGet(() -> createHSBColor(clickgui));
    }

    private Color createHSBColor(ClickGui clickgui) {
        float hue = (clickgui.clickGuiColor.getInputToFloat() % 360) / 360.0f;
        float saturation = clickgui.saturation.getInputToFloat();
        return Color.getHSBColor(hue, saturation, 1.0F);
    }

    public Color getBackColor() {
        return backColorSupplier.get();
    }
}