package cc.unknown.ui.clickgui.raven.impl;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cc.unknown.module.setting.Setting;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.ui.clickgui.raven.impl.api.Component;

public class ModeComp extends Component {
	private final int c = (new Color(30, 144, 255)).getRGB();
	private final ModeValue mode;
	private final ModuleComp module;
	protected Setting setting;
	protected int x;
	protected int y;
	private int o;

	public ModeComp(ModeValue desc, ModuleComp b, int o) {
		this.mode = desc;
		this.module = b;
		this.x = b.category.getX() + b.category.getWidth();
		this.y = b.category.getY() + b.o;
		this.o = o;
	}

	@Override
	public void renderComponent() {
		GL11.glPushMatrix();
		GL11.glScaled(0.5D, 0.5D, 0.5D);
		int bruhWidth = (int) (mc.fontRendererObj.getStringWidth(this.mode.getName() + ": ")
				* 0.5);
		mc.fontRendererObj.drawString(this.mode.getName() + ": ",
				(float) ((this.module.category.getX() + 4) * 2),
				(float) ((this.module.category.getY() + this.o + 4) * 2), 0xffffffff, true);
		mc.fontRendererObj.drawString(String.valueOf(this.mode.getMode()),
				(float) ((this.module.category.getX() + 4 + bruhWidth) * 2),
				(float) ((this.module.category.getY() + this.o + 4) * 2), this.c, true);
		GL11.glPopMatrix();
	}

	@Override
	public void updateComponent(int mousePosX, int mousePosY) {
		this.y = this.module.category.getY() + this.o;
		this.x = this.module.category.getX();
	}

	@Override
	public void setOffset(int n) {
		this.o = n;
	}

	@Override
	public int getHeight() {
		return 11;
	}

	@Override
	public void mouseClicked(int x, int y, int b) {
		if (this.i(x, y) && b == 0 && this.module.open) {
			this.mode.increment();
		} else if (this.i(x, y) && b == 1 && this.module.open) {
			this.mode.decrement();
		}
	}

	@Override
	public void mouseReleased(int x, int y, int m) {

	}

	@Override
	public void keyTyped(char t, int k) {

	}

	private boolean i(int x, int y) {
		return x > this.x && x < this.x + this.module.category.getWidth() && y > this.y && y < this.y + 11;
	}
}