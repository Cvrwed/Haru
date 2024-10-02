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
import cc.unknown.utils.client.RenderUtil;
import cc.unknown.utils.font.FontUtil;

public class ModuleComp extends Component {
	public Module mod;
	public CategoryComp category;
	public int offSet;
	private final ArrayList<Component> settings;
	public boolean open;

	public ModuleComp(Module mod, CategoryComp p, int offSet) {
		this.mod = mod;
		this.category = p;
		this.offSet = offSet;
		this.settings = new ArrayList<>();
		this.open = false;

		AtomicInteger y = new AtomicInteger(offSet + 12);

		this.mod.getSettings().forEach(setting -> {
			addComp(setting, y.getAndAdd(getOffset(setting)));
		});

		this.settings.add(new BindComp(this, y));
	}

	@Override
	public void setOffset(int value) {
		offSet = value;
		int y = offSet + 16;

		for (Component c : settings) {
			c.setOffset(y);
			if (c instanceof SliderComp || c instanceof DoubleSliderComp) {
				y += 16;
			} else if (c instanceof BooleanComp || c instanceof DescComp || c instanceof ModeComp
					|| c instanceof BindComp) {
				y += 12;
			}
		}
	}

	@Override
	public void renderComponent() {
	    float x = category.getX();
	    float y = category.getY() + offSet;
	    float width = category.getWidth();
	    float height = 15;
	    
	    RenderUtil.drawQuad(x, y, x + width, y + height + offSet);

	    GL11.glPushMatrix();
	    
	    int buttonColor = mod.isEnabled() ? Theme.instance.getMainColor().getRGB() :
	                     mod.canBeEnabled() ? Color.lightGray.getRGB() :
	                     new Color(102, 102, 102).getRGB();
	                     
	    String moduleName = mod.getModuleInfo().name();
	    float textX = (float) (x + width / 2 - FontUtil.light.getStringWidth(moduleName) / 2);
	    float textY = y + 4;

	    FontUtil.light.drawStringWithShadow(moduleName, textX, textY, buttonColor);
	    
	    GL11.glPopMatrix();

	    if (open && !settings.isEmpty()) {
	        settings.forEach(Component::renderComponent);
	    }
	}

	@Override
	public int getHeight() {
		if (!open) {
			return 16;
		} else {
			int h = 16;

			for (Component c : settings) {
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
		if (!settings.isEmpty()) {
			settings.forEach(comp -> comp.updateComponent(mousePosX, mousePosY));
		}
	}

	@Override
	public void mouseClicked(int x, int y, int b) {
		if (mod.canBeEnabled()) {
			if (isMouseOnButton(x, y)) {
				switch (b) {
				case 0:
					mod.toggle();
					break;
				case 1:
					open = !open;
					category.refresh();
					break;
				}
			}
		}

		settings.forEach(comp -> comp.mouseClicked(x, y, b));
	}

	@Override
	public void mouseReleased(int x, int y, int m) {
		settings.forEach(comp -> comp.mouseReleased(x, y, m));
	}

	@Override
	public void keyTyped(char t, int k) {
		settings.forEach(comp -> comp.keyTyped(t, k));
	}

	public boolean isMouseOnButton(int x, int y) {
		return x > category.getX() && x < category.getX() + category.getWidth()
				&& y > category.getY() + offSet && y < category.getY() + 16 + offSet;
	}

	private void addComp(Setting setting, int y) {
		if (setting instanceof SliderValue) {
			settings.add(new SliderComp((SliderValue) setting, this, y));
		} else if (setting instanceof BooleanValue) {
			settings.add(new BooleanComp(mod, (BooleanValue) setting, this, y));
		} else if (setting instanceof DescValue) {
			settings.add(new DescComp((DescValue) setting, this, y));
		} else if (setting instanceof DoubleSliderValue) {
			settings.add(new DoubleSliderComp((DoubleSliderValue) setting, this, y));
		} else if (setting instanceof ModeValue) {
			settings.add(new ModeComp((ModeValue) setting, this, y));
		}
	}

	private int getOffset(Setting setting) {
		return (setting instanceof SliderValue || setting instanceof DoubleSliderValue) ? 16 : 12;
	}
}
