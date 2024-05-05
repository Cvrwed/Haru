package cc.unknown.ui.clickgui.raven.impl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.lwjgl.opengl.GL11;

import cc.unknown.module.impl.Module;
import cc.unknown.module.setting.Setting;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.DescValue;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.ui.clickgui.raven.impl.api.Component;
import cc.unknown.ui.clickgui.raven.impl.api.Theme;
import net.minecraft.client.Minecraft;

public class ModuleComp extends Component {
	public Module mod;
	public CategoryComp category;
	public int o;
	private final ArrayList<Component> settings;
	public boolean open;

	public ModuleComp(Module mod, CategoryComp p, int o) {
		this.mod = mod;
		this.category = p;
		this.o = o;
		this.settings = new ArrayList<>();
		this.open = false;

		AtomicInteger y = new AtomicInteger(o + 12);

		mod.getSettings().forEach(setting -> {
			addComp(setting, y.getAndAdd(getOffset(setting)));
		});

		this.settings.add(new BindComp(this, y));
	}

	@Override
	public void setOffset(int n) {
		this.o = n;
		int y = this.o + 16;

		for (Component c : this.settings) {
			c.setOffset(y);
			if (c instanceof SliderComp || c instanceof DoubleSliderComp) {
				y += 16;
			} else if (c instanceof BooleanComp || c instanceof DescComp || c instanceof ModeComp
					|| c instanceof BindComp) {
				y += 12;
			}
		}
	}

	public static void e() {
		GL11.glDisable(2929);
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glDepthMask(true);
		GL11.glEnable(2848);
		GL11.glHint(3154, 4354);
		GL11.glHint(3155, 4354);
	}

	public static void f() {
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glEnable(2929);
		GL11.glDisable(2848);
		GL11.glHint(3154, 4352);
		GL11.glHint(3155, 4352);
		GL11.glEdgeFlag(true);
	}

	public static void g(int h) {
		float a = 0.0F;
		float r = 0.0F;
		float g = 0.0F;
		float b = 0.0F;

		GL11.glColor4f(r, g, b, a);
	}

	public static void v(float x, float y, float x1, float y1, int t, int b) {
		e();
		GL11.glShadeModel(7425);
		GL11.glBegin(7);
		g(t);
		GL11.glVertex2f(x, y1);
		GL11.glVertex2f(x1, y1);
		g(b);
		GL11.glVertex2f(x1, y);
		GL11.glVertex2f(x, y);
		GL11.glEnd();
		GL11.glShadeModel(7424);
		f();
	}

	@Override
	public void renderComponent() {
		v((float) this.category.getX(), (float) (this.category.getY() + this.o),
				(float) (this.category.getX() + this.category.getWidth()), (float) (this.category.getY() + 15 + this.o),
				this.mod.isEnabled() ? Theme.instance.getMainColor().getRGB() : -12829381,
				this.mod.isEnabled() ? Theme.instance.getMainColor().getRGB() : -12302777);
		GL11.glPushMatrix();
		int button_rgb;
		if (this.mod.isEnabled()) {
			button_rgb = Theme.instance.getMainColor().getRGB();
		} else if (this.mod.canBeEnabled()) {
			button_rgb = Color.lightGray.getRGB();
		} else {
			button_rgb = new Color(102, 102, 102).getRGB();
		}
		mc.fontRendererObj.drawStringWithShadow(this.mod.getRegister().name(),
				(float) (this.category.getX() + this.category.getWidth() / 2
						- Minecraft.getMinecraft().fontRendererObj.getStringWidth(this.mod.getRegister().name()) / 2),
				(float) (this.category.getY() + this.o + 4), button_rgb);
		GL11.glPopMatrix();
		if (this.open && !this.settings.isEmpty()) {
			this.settings.forEach(Component::renderComponent);
		}
	}

	@Override
	public int getHeight() {
		if (!this.open) {
			return 16;
		} else {
			int h = 16;

			for (Component c : this.settings) {
				if (c instanceof SliderComp || c instanceof DoubleSliderComp) {
					h += 16;
				} else if (c instanceof BooleanComp || c instanceof DescComp || c instanceof ModeComp
						|| c instanceof BindComp) {
					h += 12;
				}
			}
			return h;
		}
	}

	@Override
	public void updateComponent(int mousePosX, int mousePosY) {
		if (!this.settings.isEmpty()) {
			this.settings.forEach(comp -> comp.updateComponent(mousePosX, mousePosY));
		}
	}

	@Override
	public void mouseClicked(int x, int y, int b) {
		if (mod.canBeEnabled()) {
			if (isMouseOnButton(x, y)) {
				switch (b) {
				case 0:
					this.mod.toggle();
					break;
				case 1:
					this.open = !this.open;
					this.category.refresh();
					break;
				}
			}
		}

		this.settings.forEach(comp -> comp.mouseClicked(x, y, b));
	}

	@Override
	public void mouseReleased(int x, int y, int m) {
		this.settings.forEach(comp -> comp.mouseReleased(x, y, m));
	}

	@Override
	public void keyTyped(char t, int k) {
		this.settings.forEach(comp -> comp.keyTyped(t, k));
	}

	public boolean isMouseOnButton(int x, int y) {
		return x > this.category.getX() && x < this.category.getX() + this.category.getWidth()
				&& y > this.category.getY() + this.o && y < this.category.getY() + 16 + this.o;
	}

	private void addComp(Setting setting, int y) {
		if (setting instanceof SliderValue) {
			this.settings.add(new SliderComp((SliderValue) setting, this, y));
		} else if (setting instanceof BooleanValue) {
			this.settings.add(new BooleanComp(mod, (BooleanValue) setting, this, y));
		} else if (setting instanceof DescValue) {
			this.settings.add(new DescComp((DescValue) setting, this, y));
		} else if (setting instanceof DoubleSliderValue) {
			this.settings.add(new DoubleSliderComp((DoubleSliderValue) setting, this, y));
		} else if (setting instanceof ModeValue) {
			this.settings.add(new ModeComp((ModeValue) setting, this, y));
		}
	}

	private int getOffset(Setting setting) {
		return (setting instanceof SliderValue || setting instanceof DoubleSliderValue) ? 16 : 12;
	}
}
