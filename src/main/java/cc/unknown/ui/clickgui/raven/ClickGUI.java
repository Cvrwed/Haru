package cc.unknown.ui.clickgui.raven;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import cc.unknown.Haru;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.visuals.ClickGui;
import cc.unknown.ui.clickgui.raven.impl.CategoryComp;
import cc.unknown.ui.clickgui.raven.impl.api.Theme;
import lombok.Getter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class ClickGUI extends GuiScreen {
	@Getter
	private final ArrayList<CategoryComp> categoryList = new ArrayList<>();
	protected int topOffset = 5;

	public ClickGUI() {
	    categoryList.addAll(Arrays.stream(Category.values())
	        .map(category -> {
	            CategoryComp comp = new CategoryComp(category);
	            comp.setY(topOffset);
	            topOffset += 20;
	            return comp;
	        })
	        .collect(Collectors.toList()));
	}

	@Override
	public void initGui() {
		super.initGui();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		ScaledResolution sr = new ScaledResolution(mc);
		ClickGui cg = (ClickGui) Haru.instance.getModuleManager().getModule(ClickGui.class);

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
		
		super.drawScreen(mouseX, mouseY, partialTicks);

	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
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
		
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int state) {
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

		super.mouseReleased(mouseX, mouseY, state);

	}

	@Override
	public void keyTyped(char t, int k) throws IOException {
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
		
		super.keyTyped(t, k);

	}

	@Override
	public void onGuiClosed() {
		ClickGui cg = (ClickGui) Haru.instance.getModuleManager().getModule(ClickGui.class);
		if (cg != null && cg.isEnabled() && Haru.instance.getHudConfig() != null) {
			Haru.instance.getHudConfig().saveHud();
			cg.disable();
		}
		super.onGuiClosed();

	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;

	}
}
