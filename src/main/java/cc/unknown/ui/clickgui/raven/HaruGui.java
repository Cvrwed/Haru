package cc.unknown.ui.clickgui.raven;

import java.util.ArrayList;

import cc.unknown.Haru;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.visuals.ClickGuiModule;
import cc.unknown.ui.clickgui.raven.impl.CategoryComp;
import cc.unknown.ui.clickgui.raven.impl.api.Theme;
import lombok.Getter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class HaruGui extends GuiScreen {
	@Getter
	private final ArrayList<CategoryComp> categoryList = new ArrayList<>();

	public HaruGui() {
		int topOffset = 5;
		for (Category category : Category.values()) {
			CategoryComp comp = new CategoryComp(category);
			comp.setY(topOffset);
			categoryList.add(comp);
			topOffset += 20;
		}
	}

	@Override
	public void initGui() {
		super.initGui();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		ScaledResolution sr = new ScaledResolution(mc);
		ClickGuiModule cg = (ClickGuiModule) Haru.instance.getModuleManager().getModule(ClickGuiModule.class);

		if (cg.backGroundMode.is("Gradient")) {
			this.drawGradientRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(),
					Theme.instance.getMainColor().getRGB(), Theme.instance.getMainColor().getAlpha());
		} else if (cg.backGroundMode.is("Normal")) {
			this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
		}

		categoryList.forEach(c -> {
			c.render(this.fontRendererObj);
			c.updatePosition(mouseX, mouseY);
			c.getModules().forEach(comp -> comp.updateComponent(mouseX, mouseY));
		});
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		ScaledResolution sr = new ScaledResolution(mc);

		categoryList.forEach(c -> {
			if (c.isInside(mouseX, mouseY)) {
				switch (mouseButton) {
				case 0:
					c.setDragging(true);
					c.setDragX(mouseX - c.getX());
					c.setDragY(mouseY - c.getY());
					break;
				case 1:
					c.setOpen(!c.isOpen());
					break;
				}
			}

			if (c.isOpen()) {
				if (!c.getModules().isEmpty()) {
					c.getModules().forEach(component -> component.mouseClicked(mouseX, mouseY, mouseButton));
				}
			}
		});
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int state) {
		ScaledResolution sr = new ScaledResolution(mc);

		categoryList.forEach(c -> {
			c.setDragging(false);
			if (c.isOpen()) {
				if (!c.getModules().isEmpty()) {
					c.getModules().forEach(component -> component.mouseReleased(mouseX, mouseY, state));
				}
			}
		});

		if (Haru.instance.getHudConfig() != null) {
			Haru.instance.getHudConfig().saveHud();
		}

	}

	@Override
	public void keyTyped(char t, int k) {
		categoryList.forEach(c -> {
			if (c.isOpen() && k != 1) {
				if (!c.getModules().isEmpty()) {
					c.getModules().forEach(component -> component.keyTyped(t, k));
				}
			}
		});

		if (k == 1 || k == 54) {
			this.mc.displayGuiScreen(null);
		}
	}

	@Override
	public void onGuiClosed() {
		ClickGuiModule cg = (ClickGuiModule) Haru.instance.getModuleManager().getModule(ClickGuiModule.class);
		if (cg != null && cg.isEnabled() && Haru.instance.getHudConfig() != null) {
			Haru.instance.getHudConfig().saveHud();
			cg.disable();
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
