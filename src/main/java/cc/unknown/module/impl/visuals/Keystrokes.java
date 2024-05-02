package cc.unknown.module.impl.visuals;

import java.awt.Color;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.render.RenderEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.ui.clickgui.raven.impl.api.Theme;
import cc.unknown.utils.client.RenderUtil;

@Register(name = "Keystrokes", category = Category.Visuals)
public class Keystrokes extends Module {
	
	public int lastA = 0;
	public int lastW = 0;
	public int lastS = 0;
	public int lastD = 0;
	public long deltaAnim = 0;

	@EventLink
	public void onDraw(RenderEvent e) {
	    if (e.is2D()) {
	        boolean A = mc.gameSettings.keyBindLeft.isKeyDown();
	        boolean W = mc.gameSettings.keyBindForward.isKeyDown();
	        boolean S = mc.gameSettings.keyBindBack.isKeyDown();
	        boolean D = mc.gameSettings.keyBindRight.isKeyDown();

	        int targetA = A ? 255 : 0;
	        int targetW = W ? 255 : 0;
	        int targetS = S ? 255 : 0;
	        int targetD = D ? 255 : 0;

	        float delta = (float) deltaAnim / 1000.0f;
	        float speed = 8.0f;
	        
	        lastA = (int) approach(lastA, targetA, speed * delta);
	        lastW = (int) approach(lastW, targetW, speed * delta);
	        lastS = (int) approach(lastS, targetS, speed * delta);
	        lastD = (int) approach(lastD, targetD, speed * delta);

	        drawKeyIndicator("A", lastA, 5.0F, 49.0F);
	        drawKeyIndicator("W", lastW, 27.0F, 27.0F);
	        drawKeyIndicator("S", lastS, 27.0F, 49.0F);
	        drawKeyIndicator("D", lastD, 49.0F, 49.0F);
	    }
	}

	private void drawKeyIndicator(String keyLabel, int alpha, float x1, float y1) {
	    float size = 20.0F;
	    float x2 = x1 + size;
	    float y2 = y1 + size;

	    RenderUtil.drawRect(x1, y1, x2, y2, new Color(0, 0, 0, 150).getRGB());
	    RenderUtil.drawRect(x1, y1, x2, y2, new Color(alpha, alpha, alpha, 150).getRGB());

	    mc.fontRendererObj.drawStringWithShadow(keyLabel, x1 + 8.0F, y1 + 5.0F, Theme.instance.getMainColor().getRGB());
	}
	
	private float approach(float current, float target, float maxChange) {
        if (current == target) {
            return current;
        } else {
            float difference = target - current;
            float sign = Math.signum(difference);
            float change = Math.min(Math.abs(difference), maxChange);
            return current + sign * change;
        }
    }
}
