package cc.unknown.ui.clickgui.raven.theme;

import java.awt.Color;

import cc.unknown.Haru;
import cc.unknown.module.impl.settings.Colors;
import cc.unknown.module.impl.visuals.ClickGuiModule;
import cc.unknown.utils.client.ColorUtil;
import cc.unknown.utils.interfaces.Loona;

public class Theme implements Loona {
    public static Color getMainColor() {
    	ClickGuiModule clickgui = (ClickGuiModule) Haru.instance.getModuleManager().getModule(ClickGuiModule.class);
    	Colors col = (Colors) Haru.instance.getModuleManager().getModule(Colors.class);
        switch (clickgui.clientTheme.getMode()) {
            case "RGB":
                return Color.getHSBColor((float)(System.currentTimeMillis() % (15000L / 3)) / (15000.0F / (float)3), 1.0F, 1.0F);
            case "Pastel":
            	return ColorUtil.reverseGradientDraw(new Color(255, 190, 190), new Color(255, 190, 255), 2);
            case "Memories":
            	return ColorUtil.reverseGradientDraw(new Color(255, 0, 255), new Color(255, 255, 0), new Color(255, 0, 158), 2);
            case "Static":
            	return Color.getHSBColor((col.getClickGuiColor().getInputToFloat() % 360) / 360.0f, col.getSaturation().getInputToFloat(), col.getBrightness().getInputToFloat());
        
        }
		return null;
    }
    
    public static Color getBackColor() {
        return new Color(0, 0, 0, 100);
    }
}