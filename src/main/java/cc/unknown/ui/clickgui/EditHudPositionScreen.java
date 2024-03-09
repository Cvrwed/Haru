package cc.unknown.ui.clickgui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

import cc.unknown.Haru;
import cc.unknown.utils.client.FuckUtil;
import cc.unknown.utils.client.FuckUtil.PositionMode;
import cc.unknown.utils.helpers.MathHelper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class EditHudPositionScreen extends GuiScreen {
    final String hudTextExample = "This is an-Example-HUD";
    GuiButton resetPosButton;
    boolean mouseDown = false;
    int textBoxStartX = 0;
    int textBoxStartY = 0;
    ScaledResolution sr;
    int textBoxEndX = 0;
    int textBoxEndY = 0;
    int marginX = 5;
    int marginY = 70;
    int lastMousePosX = 0;
    int lastMousePosY = 0;
    int sessionMousePosX = 0;
    int sessionMousePosY = 0;
    
	public static AtomicInteger arrayListX = new AtomicInteger(5);
	public static AtomicInteger arrayListY = new AtomicInteger(70);

	public static final String ArrayListX = "HUDX:";
	public static final String ArrayListY = "HUDY:";

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(this.resetPosButton = new GuiButton(1, this.width - 90, 5, 85, 20, "Reset position"));
        this.marginX = arrayListX.get();
        this.marginY = arrayListY.get();
        sr = new ScaledResolution(mc);
        FuckUtil.instance.setPositionMode(FuckUtil.instance.getPostitionMode(marginX, marginY, sr.getScaledWidth(), sr.getScaledHeight()));
    }

    @Override
    public void drawScreen(int mX, int mY, float pt) {
        drawRect(0, 0, this.width, this.height, -1308622848);
        drawRect(0, this.height /2, this.width, this.height /2 + 1, 0x9936393f);
        drawRect(this.width /2, 0, this.width /2 + 1, this.height, 0x9936393f);
        AtomicInteger textBoxStartX = new AtomicInteger(marginX);
        AtomicInteger textBoxStartY = new AtomicInteger(marginY);
        int textBoxEndX = textBoxStartX.get() + 50;
        int textBoxEndY = textBoxStartY.get() + 32;
        this.drawArrayList(this.mc.fontRendererObj, this.hudTextExample);
        this.textBoxStartX = textBoxStartX.get();
        this.textBoxStartY = textBoxStartY.get();
        this.textBoxEndX = textBoxEndX;
        this.textBoxEndY = textBoxEndY;
        arrayListX.set(textBoxStartX.get());
        arrayListY.set(textBoxStartY.get());

        try {
			this.handleInput();
		} catch (IOException e) {
			e.printStackTrace();
		}

        super.drawScreen(mX, mY, pt);
    }
    
    private void drawArrayList(FontRenderer fr, String t) {
        int x = this.textBoxStartX;
        int gap = this.textBoxEndX - this.textBoxStartX;
        int y = this.textBoxStartY;
        double marginY = fr.FONT_HEIGHT + 2;
        String[] var4 = t.split("-");
        ArrayList<String> var5 = MathHelper.toArrayList(var4);
        if ((FuckUtil.instance.getPositionMode() == PositionMode.UPLEFT) || (FuckUtil.instance.getPositionMode() == PositionMode.UPRIGHT))
			var5.sort((o1, o2) -> mc.fontRendererObj.getStringWidth(o2) - mc.fontRendererObj.getStringWidth(o1));
		else if ((FuckUtil.instance.getPositionMode() == PositionMode.DOWNLEFT) || (FuckUtil.instance.getPositionMode() == PositionMode.DOWNRIGHT))
			var5.sort(Comparator.comparingInt(o2 -> mc.fontRendererObj.getStringWidth(o2)));

        if ((FuckUtil.instance.getPositionMode() == PositionMode.DOWNRIGHT) || (FuckUtil.instance.getPositionMode() == PositionMode.UPRIGHT))
			for (String s : var5) {
                fr.drawString(s, (float) x + (gap - fr.getStringWidth(s)), (float) y, Color.white.getRGB(), true);
                y += marginY;
            }
		else
			for (String s : var5) {
                fr.drawString(s, (float) x, (float) y, Color.white.getRGB(), true);
                y += marginY;
            }
    }

    @Override
    public void mouseClickMove(int mousePosX, int mousePosY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mousePosX, mousePosY, clickedMouseButton, timeSinceLastClick);
        if (clickedMouseButton == 0) {
            if (this.mouseDown) {
                this.marginX = this.lastMousePosX + (mousePosX - this.sessionMousePosX);
                this.marginY = this.lastMousePosY + (mousePosY - this.sessionMousePosY);
                sr = new ScaledResolution(mc);
                FuckUtil.instance.setPositionMode(FuckUtil.instance.getPostitionMode(marginX, marginY,sr.getScaledWidth(), sr.getScaledHeight()));

            } else if (mousePosX > this.textBoxStartX && mousePosX < this.textBoxEndX && mousePosY > this.textBoxStartY && mousePosY < this.textBoxEndY) {
                this.mouseDown = true;
                this.sessionMousePosX = mousePosX;
                this.sessionMousePosY = mousePosY;
                this.lastMousePosX = this.marginX;
                this.lastMousePosY = this.marginY;
            }

        }
    }

    @Override
    public void mouseReleased(int mX, int mY, int state) {
        super.mouseReleased(mX, mY, state);
        if (state == 0) {
            this.mouseDown = false;
        }
    }

    @Override
    public void actionPerformed(GuiButton b) {
        if (b == this.resetPosButton) {
            int newX = 5;
            int newY = 70;
            
            this.marginX = newX;
            this.marginY = newY;
            
            arrayListX.set(newX);
            arrayListY.set(newY);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    @Override
    public void onGuiClosed() {
        if (Haru.instance.getClientConfig() != null && Haru.instance.getConfigManager() != null) {
        	Haru.instance.getConfigManager().save();
        	Haru.instance.getClientConfig().saveConfig();
        }
    }
}
