package cc.unknown.module.impl.visuals;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.impl.visuals.keystrokes.Key;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.ui.clickgui.raven.theme.Theme;
import net.minecraft.client.gui.Gui;

public class KeystrokesDisplay extends Module {
	private SliderValue posX = new SliderValue("Position X", 500, 10, 1920, 10);
	private SliderValue posY = new SliderValue("Position Y", 0, 10, 1080, 10);
	private BooleanValue lowerCase = new BooleanValue("Lowercase", false);

	public KeystrokesDisplay() {
		super("Keystrokes", ModuleCategory.Visuals);
		this.registerSetting(posX, posY, lowerCase);
	}

	@EventLink
	public void onRender(final Render2DEvent e) {
	    if (mc.currentScreen != null || mc.gameSettings.showDebugInfo) {
	        return;
	    }

	    GL11.glDisable(GL11.GL_BLEND);
	    
	    for (Key key : getKeys()) {
	        final int textWidth = mc.fontRendererObj.getStringWidth(key.getName());
	        final int x = posX.getInputToInt() + key.getX();
	        final int y = posY.getInputToInt() + key.getY();
	        final int width = key.getWidth();
	        final int height = key.getHeight();

	        int backgroundColor = new Color(0, 0, 0, 125).getRGB();
	        int textColor = key.isDown() ? Color.WHITE.getRGB() : Theme.getMainColor().getRGB();
	        
	        Gui.drawRect(x, y, x + width, y + height, backgroundColor);

	        String keyName = key.getName();
	        if (lowerCase.isToggled()) {
	        	keyName = keyName.toLowerCase();
	        }
	        mc.fontRendererObj.drawString(keyName, x + width / 2 - textWidth / 2, y + height / 2 - 4, textColor);
	    }

	    GL11.glEnable(GL11.GL_BLEND);
	}
	
	private Key[] getKeys() {
	    return new Key[] {
	        new Key("W", mc.gameSettings.keyBindForward, 21, 1, 18, 18),
	        new Key("A", mc.gameSettings.keyBindLeft, 1, 21, 18, 18),
	        new Key("S", mc.gameSettings.keyBindBack, 21, 21, 18, 18),
	        new Key("D", mc.gameSettings.keyBindRight, 41, 21, 18, 18),
	        new Key("LMB", mc.gameSettings.keyBindAttack, 1, 41, 28, 18),
	        new Key("RMB", mc.gameSettings.keyBindUseItem, 31, 41, 28, 18),
	        new Key("SPACE", mc.gameSettings.keyBindJump, 1, 61, 58, 12)
	    };
	}
}
