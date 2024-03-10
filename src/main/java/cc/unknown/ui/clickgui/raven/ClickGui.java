package cc.unknown.ui.clickgui.raven;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import cc.unknown.Haru;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.impl.visuals.ClickGuiModule;
import cc.unknown.ui.clickgui.raven.impl.CategoryComp;
import cc.unknown.ui.clickgui.raven.impl.api.Component;
import cc.unknown.ui.clickgui.raven.theme.Theme;
import cc.unknown.utils.client.FuckUtil;
import cc.unknown.utils.client.RenderUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

public class ClickGui extends GuiScreen {
	private final ArrayList<CategoryComp> categoryList = new ArrayList<>();
	private final Map<String, ResourceLocation> waifuMap = new HashMap<>();
	private boolean isDragging = false;
	private AtomicInteger lastMouseX = new AtomicInteger(0);
	private AtomicInteger lastMouseY = new AtomicInteger(0);

	public ClickGui() {
		int topOffset = 5;
		ModuleCategory[] values;
		int categoryAmount = (values = ModuleCategory.values()).length;

		for (int category = 0; category < categoryAmount; ++category) {
			ModuleCategory moduleCategory = values[category];
			CategoryComp currentModuleCategory = new CategoryComp(moduleCategory);
			currentModuleCategory.setY(topOffset);
			categoryList.add(currentModuleCategory);
			topOffset += 20;
		}

		String[] waifuNames = { "astolfo", "hideri", "manolo", "bunny", "kurumi", "uzaki", "fujiwara", "cat", "megumin",
				"komi" };
		Arrays.stream(waifuNames)
				.forEach(name -> waifuMap.put(name, new ResourceLocation("haru/img/clickgui/" + name + ".png")));
	}

	@Override
	public void initGui() {
		super.initGui();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		ScaledResolution sr = new ScaledResolution(mc);
		ClickGuiModule cg = (ClickGuiModule) Haru.instance.getModuleManager().getModule(ClickGuiModule.class);
		ResourceLocation waifuImage = waifuMap.get(cg.waifuMode.getMode().toLowerCase());

		for (CategoryComp category : categoryList) {
			category.render(this.fontRendererObj);
			category.updste(mouseX, mouseY);

			for (Component module : category.getModules()) {
				module.update(mouseX, mouseY);
			}

			if (waifuImage != null && !cg.waifuMode.is("None")) {
				RenderUtil.drawImage(waifuImage, FuckUtil.instance.getWaifuX(), FuckUtil.instance.getWaifuY(),
						sr.getScaledWidth() / 5.2f, sr.getScaledHeight() / 2f);
			} else {
				isDragging = false;
			}

			if (cg.gradient.isToggled()) {
				RenderUtil.drawGradientRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(),
						Theme.getMainColor().getRGB(), 100);
			}
		}

		if (isDragging) {
			FuckUtil.instance.setWaifuX(FuckUtil.instance.getWaifuX() + mouseX - lastMouseX.get());
			FuckUtil.instance.setWaifuY(FuckUtil.instance.getWaifuY() + mouseY - lastMouseY.get());
			lastMouseX.set(mouseX);
			lastMouseY.set(mouseY);
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		Iterator<CategoryComp> btnCat = categoryList.iterator();
		ScaledResolution sr = new ScaledResolution(mc);

		while (true) {
			CategoryComp category;
			do {
				do {
					if (!btnCat.hasNext()) {
						return;
					}

					if (isBound(mouseX, mouseY, sr) && mouseButton == 0) {
						isDragging = true;
						lastMouseX.set(mouseX);
						lastMouseY.set(mouseY);
						return;
					}

					category = btnCat.next();
					if (category.isInside(mouseX, mouseY) && !category.i(mouseX, mouseY)
							&& !category.mousePressed(mouseX, mouseY) && mouseButton == 0) {
						category.mousePressed(true);
						category.setXx(mouseX - category.getX());
						category.setYy(mouseY - category.getY());
					}

					if (category.mousePressed(mouseX, mouseY) && mouseButton == 0) {
						category.setOpened(!category.isOpened());
					}

					if (category.i(mouseX, mouseY) && mouseButton == 0) {
						category.cv(!category.p());
					}
				} while (!category.isOpened());
			} while (category.getModules().isEmpty());

			for (Component c : category.getModules()) {
				c.mouseDown(mouseX, mouseY, mouseButton);
			}
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int state) {
		ScaledResolution sr = new ScaledResolution(mc);

		if (state == 0) {
			if (isBound(mouseX, mouseY, sr)) {
				isDragging = false;
				return;
			}

			Iterator<CategoryComp> btnCat = categoryList.iterator();

			CategoryComp c4t;
			while (btnCat.hasNext()) {
				c4t = btnCat.next();
				c4t.mousePressed(false);
			}

			btnCat = categoryList.iterator();

			while (true) {
				do {
					do {
						if (!btnCat.hasNext()) {
							return;
						}

						c4t = btnCat.next();
					} while (!c4t.isOpened());
				} while (c4t.getModules().isEmpty());

				for (Component c : c4t.getModules()) {
					c.mouseReleased(mouseX, mouseY, state);
				}
			}
		}
		if (Haru.instance.getClientConfig() != null) {
			Haru.instance.getClientConfig().saveConfig();
		}

	}

	@Override
	public void keyTyped(char t, int k) {
		if (k == 54 || k == 1) {
			mc.displayGuiScreen(null);
		} else {
			Iterator<CategoryComp> btnCat = categoryList.iterator();

			while (true) {
				CategoryComp cat;
				do {
					do {
						if (!btnCat.hasNext()) {
							return;
						}

						cat = btnCat.next();
					} while (!cat.isOpened());
				} while (cat.getModules().isEmpty());

				for (Component c : cat.getModules()) {
					c.keyTyped(t, k);
				}
			}
		}
	}

	@Override
	public void onGuiClosed() {
		ClickGuiModule cgui = (ClickGuiModule) Haru.instance.getModuleManager().getModule(ClickGuiModule.class);
		if (cgui != null && cgui.isEnabled()) {
			cgui.disable();
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	public ArrayList<CategoryComp> getCategoryList() {
		return categoryList;
	}

	private boolean isBound(int x, int y, ScaledResolution sr) {
		return x >= FuckUtil.instance.getWaifuX() && x <= FuckUtil.instance.getWaifuX() + (sr.getScaledWidth() / 5.1f)
				&& y >= FuckUtil.instance.getWaifuY()
				&& y <= FuckUtil.instance.getWaifuY() + (sr.getScaledHeight() / 2f);
	}
}
