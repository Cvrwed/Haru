package cc.unknown.ui.clickgui.raven;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cc.unknown.Haru;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.impl.visuals.ClickGuiModule;
import cc.unknown.ui.clickgui.Component;
import cc.unknown.ui.clickgui.raven.components.CategoryComp;
import cc.unknown.ui.clickgui.theme.Theme;
import cc.unknown.utils.client.RenderUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

public class ClickGui extends GuiScreen {
	private final ArrayList<CategoryComp> categoryList;
	private final Map<String, ResourceLocation> waifuMap = new HashMap<>();
	private boolean isDragging = false;
	private int lastMouseX = 0;
	private int lastMouseY = 0;
	public static int waifuX = 340;
	public static int waifuY = 135;
	public static final String WaifuX = "WaifuX:";
	public static final String WaifuY = "WaifuY:";

	public ClickGui() {
		this.categoryList = new ArrayList<>();
		int offset = 5;

		for (ModuleCategory category : ModuleCategory.values()) {
			CategoryComp comp = new CategoryComp(category);
			comp.setY(offset);
			categoryList.add(comp);
			offset += 20;
		}

		String[] waifuNames = { "astolfo", "hideri", "gwen", "kurumi", "uzaki", "rem", "loona", "megumi", "magic", "typh" };
		for (int i = 0; i < waifuNames.length; i++) {
			waifuMap.put(waifuNames[i], new ResourceLocation("haru/img/clickgui/" + waifuNames[i] + ".png"));
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
		ResourceLocation waifuImage = waifuMap.get(cg.waifuMode.getMode().toLowerCase());
		
		if (cg.gradient.isToggled()) {
			RenderUtil.drawGradientRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), Theme.getMainColor().getRGB(), Theme.getMainColor().getAlpha());
		}
		
		if (waifuImage != null) {
			RenderUtil.drawImage(waifuImage, waifuX, waifuY, sr.getScaledWidth() / 5.2f, sr.getScaledHeight() / 2f);
		} else if (waifuImage == null) {
			isDragging = false;
		}

		if (isDragging) {
			waifuX += mouseX - lastMouseX;
			waifuY += mouseY - lastMouseY;
			lastMouseX = mouseX;
			lastMouseY = mouseY;
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
		
		for (CategoryComp category : categoryList) {
			category.render(this.fontRendererObj);
			category.updste(mouseX, mouseY);

			for (Component module : category.getModules()) {
				module.update(mouseX, mouseY);
			}
		}
		
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		ScaledResolution sr = new ScaledResolution(mc);
		if (isBound(mouseX, mouseY, sr) && mouseButton == 0) {
			isDragging = true;
			lastMouseX = mouseX;
			lastMouseY = mouseY;
			return;
		}

		for (CategoryComp category : categoryList) {
			if (category.isInside(mouseX, mouseY) && mouseButton == 0) {
				category.mousePressed(true);
				isDragging = false;
				category.setXx(mouseX - category.getX());
				category.setYy(mouseY - category.getY());

				if (category.mousePressed(mouseX, mouseY)) {
					category.setOpened(!category.isOpened());
				}
			}

			if (category.isOpened()) {
				for (Component module : category.getModules()) {
					module.mouseDown(mouseX, mouseY, mouseButton);
				}
			}
		}
		
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int state) {
		if (state == 0) {
			if (isBound(mouseX, mouseY, new ScaledResolution(mc))) {
				isDragging = false;
				return;
			}

			for (CategoryComp category : categoryList) {
				category.mousePressed(false);
				if (category.isOpened() && !category.getModules().isEmpty()) {
					for (Component module : category.getModules()) {
						module.mouseReleased(mouseX, mouseY, state);
					}
				}
			}

			if (Haru.instance.getClientConfig() != null) {
				Haru.instance.getClientConfig().saveConfig();
			}
			super.mouseReleased(mouseX, mouseY, state);
		}
	}

	@Override
	protected void keyTyped(char t, int k) throws IOException {   
		if (k == 54 || k == 1) {
			mc.displayGuiScreen(null);
		} else {
			for (CategoryComp cat : categoryList) {
				if (cat.isOpened() && !cat.getModules().isEmpty()) {
					cat.getModules().forEach(c -> c.keyTyped(t, k));
				}
			}
		}
		super.keyTyped(t, k);
	}

	@Override
	public void onGuiClosed() {
		ClickGuiModule cgui = (ClickGuiModule) Haru.instance.getModuleManager().getModule(ClickGuiModule.class);
		if (cgui != null && cgui.isEnabled()) {
			cgui.disable();
		}
		super.onGuiClosed();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	public ArrayList<CategoryComp> getCategoryList() {
		return categoryList;
	}

	public static boolean isBound(int x, int y, ScaledResolution sr) {
		return x >= waifuX && x <= waifuX + (sr.getScaledWidth() / 5.1f) && y >= waifuY
				&& y <= waifuY + (sr.getScaledHeight() / 2f);
	}
}
