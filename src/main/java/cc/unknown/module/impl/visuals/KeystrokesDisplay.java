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
	private int index = 0;
	private long fx = 0L;
	private SliderValue posX = new SliderValue("Position X", 500, 10, 5000, 10);
	private SliderValue posY = new SliderValue("Position Y", 0, 10, 5000, 10);
	private BooleanValue lowerCase = new BooleanValue("Lowercase", false);
	private Key[] keys = new Key[] { new Key("W", mc.gameSettings.keyBindForward, 21, 1, 18, 18),
			new Key("A", mc.gameSettings.keyBindLeft, 1, 21, 18, 18),
			new Key("S", mc.gameSettings.keyBindBack, 21, 21, 18, 18),
			new Key("D", mc.gameSettings.keyBindRight, 41, 21, 18, 18),
			new Key("LMB", mc.gameSettings.keyBindAttack, 1, 41, 28, 18),
			new Key("RMB", mc.gameSettings.keyBindUseItem, 31, 41, 28, 18),
			new Key("SPACE", mc.gameSettings.keyBindJump, 1, 61, 58, 12) };

	public KeystrokesDisplay() {
		super("Keystrokes", ModuleCategory.Visuals);
		this.registerSetting(posX, posY, lowerCase);
	}

	@EventLink
	public void onRender(final Render2DEvent e) {
		if (mc.currentScreen != null || mc.gameSettings.showDebugInfo) {
			return;
		}

		boolean blend = GL11.glIsEnabled(3042);
		GL11.glDisable(3042);
		for (Key key : keys) {
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

			mc.fontRendererObj.drawString(keyName, x + width / 2 - textWidth / 2, y + height / 2 - 4, key.isDown() ? Color.BLACK.getRGB() : rainbowEffect(index + (float)fx * 2000.0F, 1.0F).getRGB());
		}
		if (blend)
			GL11.glEnable(3042);
	}
	
	  private Color rainbowEffect(float f, float fade) {
		    float hue = ((float)System.nanoTime() + f) / 4.0E9F % 1.0F;
		    long color = Long.parseLong(Integer.toHexString(Integer.valueOf(Color.HSBtoRGB(hue, 1.0F, 1.0F)).intValue()), 16);
		    Color c = new Color((int)color);
		    return new Color(c.getRed() / 255.0F * fade, c.getGreen() / 255.0F * fade, c.getBlue() / 255.0F * fade, c.getAlpha() / 255.0F);
	  }
}
