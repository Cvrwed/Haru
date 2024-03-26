package cc.unknown.ui.clickgui.raven.impl;

import org.lwjgl.opengl.GL11;

import cc.unknown.module.setting.impl.DescValue;
import cc.unknown.ui.clickgui.raven.impl.api.Component;
import cc.unknown.ui.clickgui.raven.impl.api.Theme;
import cc.unknown.utils.Loona;

public class DescComp implements Component, Loona {
	private final DescValue desc;
	private final ModuleComp p;
	private int o;

	public DescComp(DescValue desc, ModuleComp b, int o) {
		this.desc = desc;
		this.p = b;
		this.o = o;
	}

	@Override
	public void draw() {
		GL11.glPushMatrix();
		GL11.glScaled(0.5D, 0.5D, 0.5D);
		mc.fontRendererObj.drawStringWithShadow(this.desc.getDesc(), (float)((this.p.category.getX() + 4) * 2), (float)((this.p.category.getY() + this.o + 4) * 2), Theme.getMainColor().getRGB());
		GL11.glPopMatrix();
	}

	@Override
	public void update(int mousePosX, int mousePosY) {
		
	}

	@Override
	public void mouseDown(int x, int y, int b) {

	}

	@Override
	public void mouseReleased(int x, int y, int m) {

	}

	@Override
	public void keyTyped(char t, int k) {

	}

	@Override
	public void setComponentStartAt(int n) {
		this.o = n;
	}

	@Override
	public int getHeight() {
		return 0;
	}
	
}
