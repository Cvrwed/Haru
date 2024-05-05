package cc.unknown.ui.clickgui.raven.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.lwjgl.opengl.GL11;

import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.ui.clickgui.raven.impl.api.Component;
import cc.unknown.ui.clickgui.raven.impl.api.Theme;
import net.minecraft.client.gui.Gui;

public class SliderComp extends Component {
	private final SliderValue v;
	private final ModuleComp p;
    private int offset;
    private int x;
    private int y;
    private boolean dragging = false;
    private double renderWidth;

	public SliderComp(SliderValue v, ModuleComp b, int offset) {
		this.v = v;
		this.p = b;
		this.x = b.category.getX() + b.category.getWidth();
		this.y = b.category.getY() + b.o;
		this.offset = offset;
	}

	@Override
	public void renderComponent() {
		Gui.drawRect(p.category.getX() + 4, p.category.getY() + offset + 11,
				p.category.getX() + 4 + p.category.getWidth() - 8, p.category.getY() + offset + 15,
				-12302777);
		int l = p.category.getX() + 4;
		int r = p.category.getX() + 4 + (int) renderWidth;
		if (r - l > 84) {
			r = l + 84;
		}

		Gui.drawRect(l, p.category.getY() + offset + 11, r, p.category.getY() + offset + 15,
				Theme.instance.getMainColor().getRGB());
		GL11.glPushMatrix();
		GL11.glScaled(0.5D, 0.5D, 0.5D);
		mc.fontRendererObj.drawStringWithShadow(v.getName() + ": " + v.getInput(),
				(float) ((int) ((float) (p.category.getX() + 4) * 2.0F)),
				(float) ((int) ((float) (p.category.getY() + offset + 3) * 2.0F)), -1);
		GL11.glPopMatrix();
	}

	@Override
	public void setOffset(int n) {
		this.offset = n;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public void updateComponent(int mousePosX, int mousePosY) {
		y = p.category.getY() + offset;
		x = p.category.getX();
		double d = Math.min(p.category.getWidth() - 8, Math.max(0, mousePosX - x));
		renderWidth = (double) (p.category.getWidth() - 8) * (v.getInput() - v.getMin())
				/ (v.getMax() - v.getMin());
		if (dragging) {
			if (d == 0.0D) {
				v.setValue(v.getMin());
			} else {
				double n = r(d / (double) (p.category.getWidth() - 8) * (v.getMax() - v.getMin())
						+ v.getMin(), 2);
				v.setValue(n);
			}
		}
	}

	private static double r(double v, int p) {
		if (p < 0) {
			return 0.0D;
		} else {
			BigDecimal bd = new BigDecimal(v);
			bd = bd.setScale(p, RoundingMode.HALF_UP);
			return bd.doubleValue();
		}
	}

	@Override
	public void mouseClicked(int x, int y, int b) {
		if (u(x, y) && b == 0 && p.open) {
			dragging = true;
		}

		if (i(x, y) && b == 0 && p.open) {
			dragging = true;
		}
	}

	@Override
	public void mouseReleased(int x, int y, int m) {
		dragging = false;
	}

	@Override
	public void keyTyped(char t, int k) {

	}

	public boolean u(int x, int y) {
		return x > this.x && x < this.x + this.p.category.getWidth() / 2 + 1 && y > this.y && y < this.y + 16;
	}

	public boolean i(int x, int y) {
		return x > this.x + this.p.category.getWidth() / 2 && x < this.x + this.p.category.getWidth() && y > this.y
				&& y < this.y + 16;
	}
}
