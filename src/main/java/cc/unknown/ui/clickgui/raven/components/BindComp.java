package cc.unknown.ui.clickgui.raven.components;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cc.unknown.module.impl.visuals.ClickGuiModule;
import cc.unknown.ui.clickgui.Component;
import cc.unknown.ui.clickgui.theme.Theme;
import net.minecraft.client.Minecraft;

public class BindComp implements Component {
	private boolean isBinding;
	private final ModuleComp p;
    private int o;
    private int x;
    private int y;

    public BindComp(ModuleComp b, int o) {
        this.p = b;
        this.x = b.category.getX() + b.category.getWidth();
        this.y = b.category.getY() + b.o;
        this.o = o;
    }

    @Override
    public void draw() {
        GL11.glPushMatrix();
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        this.dr(this.isBinding ? "Select a key..." : "Bind: " + Keyboard.getKeyName(this.p.mod.getKey()));
        GL11.glPopMatrix();
    }

    @Override
    public void update(int mousePosX, int mousePosY) {
        this.y = this.p.category.getY() + this.o;
        this.x = this.p.category.getX();
    }

    @Override
    public void mouseDown(int x, int y, int b) {
        if (this.i(x, y) && b == 0 && this.p.po) {
            this.isBinding = !this.isBinding;
        }

    }

    @Override
    public void mouseReleased(int x, int y, int m) {

    }

    @Override
    public void keyTyped(char t, int k) {
        if (this.isBinding) {
            this.p.mod.setKey((k == 11 && this.p.mod instanceof ClickGuiModule) ? 28 : k);
            this.isBinding = false;
        }
    }

    @Override
    public void setComponentStartAt(int n) {
        this.o = n;
    }

    public boolean i(int x, int y) {
        return x > this.x && x < this.x + this.p.category.getWidth() && y > this.y - 1 && y < this.y + 12;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    private void dr(String s) {
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(s, (float)((this.p.category.getX() + 4) * 2), (float)((this.p.category.getY() + this.o + 3) * 2), Theme.getMainColor().getRGB());
    }
}
