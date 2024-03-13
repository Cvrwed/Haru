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
import net.minecraft.client.gui.Gui;

public class KeystrokesDisplay extends Module {

	private SliderValue posX = new SliderValue("Position X", 100, 1, 700, 1);
	private SliderValue posY = new SliderValue("Position Y", 0, 1, 270, 1);
	private SliderValue keyColor = new SliderValue("Keys Color [H/S/B]", 0, 0, 350, 10);
	private BooleanValue lowerCase = new BooleanValue("Lowercase", false);
	private Key[] keys = new Key[] {new Key("W", mc.gameSettings.keyBindForward, 21, 1, 18, 18), new Key("A", mc.gameSettings.keyBindLeft, 1, 21, 18, 18), new Key("S", mc.gameSettings.keyBindBack, 21, 21, 18, 18), new Key("D", mc.gameSettings.keyBindRight, 41, 21, 18, 18), new Key("LMB", mc.gameSettings.keyBindAttack, 1, 41, 28, 18), new Key("RMB", mc.gameSettings.keyBindUseItem, 31, 41, 28, 18), new Key("SPACE", mc.gameSettings.keyBindJump, 1, 61, 58, 12) };

	public KeystrokesDisplay() {
		super("Keystrokes", ModuleCategory.Visuals);
		this.registerSetting(posX, posY, keyColor, lowerCase);
	}

	@EventLink
	public void onRender(final Render2DEvent e) {
	    if (mc.currentScreen != null || mc.gameSettings.showDebugInfo) {
	        return;
	    }

	    GL11.glDisable(3042);
	    for (Key key : keys) {
	        drawKey(key);
	    }

	    if (GL11.glIsEnabled(3042)) {
	        GL11.glEnable(3042);
	    }
	}

	private void drawKey(Key key) {
	    final int textWidth = mc.fontRendererObj.getStringWidth(key.getName());
	    int x = posX.getInputToInt() + key.getX();
	    int y = posY.getInputToInt() + key.getY();
	    int width = key.getWidth();
	    int height = key.getHeight();

	    Gui.drawRect(x, y, x + width, y + height, key.isDown() ? Integer.MAX_VALUE : 2130706432);

	    String keyName = key.getName();
	    if (lowerCase.isToggled()) {
	        keyName = keyName.toLowerCase();
	    }

	    mc.fontRendererObj.drawString(keyName, x + width / 2 - textWidth / 2, y + height / 2 - 4, key.isDown() ? Color.BLACK.getRGB() : Color.getHSBColor((keyColor.getInputToFloat() % 360) / 360.0f, 1.0f, 1.0f).getRGB());
	}
}
