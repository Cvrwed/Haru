package cc.unknown.ui.clickgui.raven.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.lwjgl.opengl.GL11;

import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.ui.clickgui.raven.impl.api.Component;
import cc.unknown.ui.clickgui.raven.impl.api.Theme;
import cc.unknown.utils.helpers.MathHelper;
import net.minecraft.client.gui.Gui;

public class DoubleSliderComp extends Component {
	private final DoubleSliderValue doubleSlider;
	private final ModuleComp module;
	private double barWidth;
	private double blankWidth;
	private int sliderStartX;
	private int sliderStartY;
	private int moduleStartY;
	private boolean mouseDown;
	private Helping mode = Helping.NONE;
	private final int boxMargin = 4;

	public DoubleSliderComp(DoubleSliderValue doubleSlider, ModuleComp module, int moduleStartY) {
		this.doubleSlider = doubleSlider;
		this.module = module;
		this.sliderStartX = this.module.category.getX() + boxMargin;
		this.sliderStartY = moduleStartY + module.category.getY();
		this.moduleStartY = moduleStartY;
	}

	@Override
	public void renderComponent() {
		int boxHeight = 4;
		int textSize = 11;
		Gui.drawRect(this.module.category.getX() + boxMargin,
				this.module.category.getY() + this.moduleStartY + textSize,
				this.module.category.getX() - boxMargin + this.module.category.getWidth(),
				this.module.category.getY() + this.moduleStartY + textSize + boxHeight, -12302777);
		int startToDrawFrom = this.module.category.getX() + boxMargin + (int) this.blankWidth;
		int finishDrawingAt = startToDrawFrom + (int) this.barWidth;
		int middleThing = (int) MathHelper.round(this.barWidth / 2, 0) + this.module.category.getX()
				+ (int) this.blankWidth + boxMargin - 1;

		Gui.drawRect(startToDrawFrom, this.module.category.getY() + this.moduleStartY + textSize, finishDrawingAt,
				this.module.category.getY() + this.moduleStartY + textSize + boxHeight, Theme.instance.getMainColor().getRGB());
		Gui.drawRect(middleThing, this.module.category.getY() + this.moduleStartY + textSize - 1,
				middleThing + (middleThing % 2 == 0 ? 2 : 1),
				this.module.category.getY() + this.moduleStartY + textSize + boxHeight + 1, 0xff1D1D1F);

		GL11.glPushMatrix();
		GL11.glScaled(0.5D, 0.5D, 0.5D);
		mc.fontRendererObj.drawStringWithShadow(
				this.doubleSlider.getName() + ": " + this.doubleSlider.getInputMin() + ", "
						+ this.doubleSlider.getInputMax(),
				(float) ((int) ((float) (this.module.category.getX() + 4) * 2.0F)),
				(float) ((int) ((float) (this.module.category.getY() + this.moduleStartY + 3) * 2.0F)), -1);
		GL11.glPopMatrix();
	}

	@Override
	public void setOffset(int posY) {
		this.moduleStartY = posY;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public void updateComponent(int mousePosX, int mousePosY) {
		this.sliderStartY = this.module.category.getY() + this.moduleStartY;
		this.sliderStartX = this.module.category.getX() + boxMargin;

		double mousePressedAt = Math.min(this.module.category.getWidth() - boxMargin * 2,
				Math.max(0, mousePosX - this.sliderStartX));
		this.blankWidth = (double) (this.module.category.getWidth() - boxMargin * 2)
				* (this.doubleSlider.getInputMin() - this.doubleSlider.getMin())
				/ (this.doubleSlider.getMax() - this.doubleSlider.getMin());
		this.barWidth = (double) (this.module.category.getWidth() - boxMargin * 2)
				* (this.doubleSlider.getInputMax() - this.doubleSlider.getInputMin())
				/ (this.doubleSlider.getMax() - this.doubleSlider.getMin());

		if (this.mouseDown) {
			if (mousePressedAt > blankWidth + barWidth / 2 || mode == Helping.MAX) {
				if (this.mode == Helping.NONE)
					this.mode = Helping.MAX;
				if (this.mode == Helping.MAX) {
					if (mousePressedAt <= blankWidth) {
						this.doubleSlider.setValueMax(this.doubleSlider.getInputMin());
					} else {
						double n = r(mousePressedAt / (double) (this.module.category.getWidth() - boxMargin * 2)
								* (this.doubleSlider.getMax() - this.doubleSlider.getMin())
								+ this.doubleSlider.getMin(), 2);
						this.doubleSlider.setValueMax(n);
					}
				}
			}

			if (mousePressedAt < blankWidth + barWidth / 2 || mode == Helping.MIN) {
				if (this.mode == Helping.NONE)
					this.mode = Helping.MIN;
				if (this.mode == Helping.MIN) {
					if (mousePressedAt == 0.0D) {
						this.doubleSlider.setValueMin(this.doubleSlider.getMin());
					} else if (mousePressedAt >= barWidth + blankWidth) {
						this.doubleSlider.setValueMin(this.doubleSlider.getMax());
					} else {
						double n = r(mousePressedAt / (double) (this.module.category.getWidth() - boxMargin * 2)
								* (this.doubleSlider.getMax() - this.doubleSlider.getMin())
								+ this.doubleSlider.getMin(), 2);
						this.doubleSlider.setValueMin(n);
					}
				}
			}
		} else {
			if (mode != Helping.NONE)
				mode = Helping.NONE;
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
		if (this.u(x, y) && b == 0 && this.module.open) {
			this.mouseDown = true;
		}

		if (this.i(x, y) && b == 0 && this.module.open) {
			this.mouseDown = true;
		}

	}

	@Override
	public void mouseReleased(int x, int y, int m) {
		this.mouseDown = false;
	}

	@Override
	public void keyTyped(char t, int k) {

	}

	public boolean u(int x, int y) {
		return x > this.sliderStartX && x < this.sliderStartX + this.module.category.getWidth() / 2 + 1
				&& y > this.sliderStartY && y < this.sliderStartY + 16;
	}

	public boolean i(int x, int y) {
		return x > this.sliderStartX + this.module.category.getWidth() / 2
				&& x < this.sliderStartX + this.module.category.getWidth() && y > this.sliderStartY
				&& y < this.sliderStartY + 16;
	}

	public enum Helping {
		MIN, MAX, NONE
	}
}
