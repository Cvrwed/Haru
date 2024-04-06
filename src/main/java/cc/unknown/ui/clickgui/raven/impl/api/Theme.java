package cc.unknown.ui.clickgui.raven.impl.api;

import java.awt.Color;

import cc.unknown.Haru;
import cc.unknown.module.impl.visuals.ClickGuiModule;
import cc.unknown.utils.Loona;
import cc.unknown.utils.client.ColorUtil;

public class Theme implements Loona {
    public static Color getMainColor() {
    	ClickGuiModule clickgui = (ClickGuiModule) Haru.instance.getModuleManager().getModule(ClickGuiModule.class);
        switch (clickgui.clientTheme.getMode()) {
            case "Lilith":
            	return ColorUtil.reverseGradientDraw(new Color(76, 56, 108), new Color(255, 51, 51), new Color(76, 56, 108), 5);
            case "Rainbow":
                return Color.getHSBColor((float)(System.currentTimeMillis() % (15000L / 3)) / (15000.0F / (float)3), 1.0F, 1.0F);
            case "Pastel":
            	return ColorUtil.reverseGradientDraw(new Color(255, 190, 190), new Color(255, 190, 255), 2);
            case "Memories":
            	return ColorUtil.reverseGradientDraw(new Color(255, 0, 255), new Color(255, 255, 0), new Color(255, 0, 158), 2);
            case "Static":
            	return Color.getHSBColor((clickgui.clickGuiColor.getInputToFloat() % 360) / 360.0f, clickgui.saturation.getInputToFloat(), clickgui.brightness.getInputToFloat());
        
        }
		return null;
    }
    
    public static Color getBackColor() {
        return new Color(0, 0, 0, 100);
    }
}