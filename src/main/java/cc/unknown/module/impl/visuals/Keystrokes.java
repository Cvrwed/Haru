package cc.unknown.module.impl.visuals;

import java.awt.Color;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.render.Render2DEvent;
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
	public void onDraw(Render2DEvent e) {
        boolean A = mc.gameSettings.keyBindLeft.pressed;
        boolean W = mc.gameSettings.keyBindForward.pressed;
        boolean S = mc.gameSettings.keyBindBack.pressed;
        boolean D = mc.gameSettings.keyBindRight.pressed;
        int alphaA = A ? 255 : 0;
        int alphaW = W ? 255 : 0;
        int alphaS = S ? 255 : 0;
        int alphaD = D ? 255 : 0;
        float diff;

        if (lastA != alphaA) {
            diff = alphaA - lastA;
            lastA = (int) (lastA + diff / 40);
        }

        if (lastW != alphaW) {
            diff = alphaW - lastW;
            lastW = (int) (lastW + diff / 40);
        }

        if (lastS != alphaS) {
            diff = alphaS - lastS;
            lastS = (int) (lastS + diff / 40);
        }

        if (lastD != alphaD) {
            diff = alphaD - lastD;
            lastD = (int) (lastD + diff / 40);
        }
        
        RenderUtil.drawRect(5.0F, 49.0F, 25.0F, 69.0F, (new Color(lastA, lastA, lastA, 150)).getRGB());
        mc.fontRendererObj.drawStringWithShadow("A", 13.0F, 56.0F, Theme.instance.getMainColor().getRGB());
        
        RenderUtil.drawRect(27.0F, 27.0F, 47.0F, 47.0F, (new Color(lastW, lastW, lastW, 150)).getRGB());
        mc.fontRendererObj.drawStringWithShadow("W", 35.0F, 34.0F, Theme.instance.getMainColor().getRGB());

        RenderUtil.drawRect(27.0F, 49.0F, 47.0F, 69.0F, (new Color(lastS, lastS, lastS, 150)).getRGB());
        mc.fontRendererObj.drawStringWithShadow("S", 34.0F, 56.0F, Theme.instance.getMainColor().getRGB());
        
        RenderUtil.drawRect(49.0F, 49.0F, 69.0F, 69.0F, (new Color(lastD, lastD, lastD, 150)).getRGB());
        mc.fontRendererObj.drawStringWithShadow("D", 58.0F, 56.0F, Theme.instance.getMainColor().getRGB());
	}
}
