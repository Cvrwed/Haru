package cc.unknown.ui.clickgui.raven;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import cc.unknown.Haru;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.impl.visuals.ClickGuiModule;
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
		AtomicInteger offset = new AtomicInteger(5);
		Arrays.stream(ModuleCategory.values()).map(category -> { CategoryComp comp = new CategoryComp(category); comp.setY(offset.getAndAdd(20)); return comp; }).forEach(categoryList::add);
	    String[] waifuNames = { "astolfo", "hideri", "gwen", "kurumi", "uzaki", "rem", "loona", "megumi", "magic", "typh" };
	    Arrays.stream(waifuNames).forEach(name -> waifuMap.put(name, new ResourceLocation("haru/img/clickgui/" + name + ".png")));
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
			RenderUtil.drawGradientRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), Theme.getMainColor().getRGB(),
					Theme.getMainColor().getAlpha());
		}

		if (waifuImage != null) {
			RenderUtil.drawImage(waifuImage, waifuX, waifuY, sr.getScaledWidth() / 5.2f, sr.getScaledHeight() / 2f);
		} else {
			isDragging = false;
		}

		if (isDragging) {
			waifuX += mouseX - lastMouseX;
			waifuY += mouseY - lastMouseY;
			lastMouseX = mouseX;
			lastMouseY = mouseY;
		}

		categoryList.forEach(category -> {
		    category.render(this.fontRendererObj);
		    category.updste(mouseX, mouseY);

		    category.getModules().forEach(module -> module.update(mouseX, mouseY));
		});
		super.drawScreen(mouseX, mouseY, partialTicks);
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

		categoryList.forEach(category -> {
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
		        category.getModules().forEach(module -> module.mouseDown(mouseX, mouseY, mouseButton));
		    }
		});

		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int state) {
		if (state == 0) {
			ScaledResolution sr = new ScaledResolution(mc);

			if (isBound(mouseX, mouseY, sr)) {
				isDragging = false;
				return;
			}

			categoryList.forEach(category -> {
			    category.mousePressed(false);
			    if (category.isOpened() && !category.getModules().isEmpty()) {
			        category.getModules().forEach(module -> module.mouseReleased(mouseX, mouseY, state));
			    }
			});

			if (Haru.instance.getClientConfig() != null) {
				Haru.instance.getClientConfig().saveConfig();
			}
		}
		super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	protected void keyTyped(char t, int k) throws IOException {
		if (k == 54 || k == 1) {
			mc.displayGuiScreen(null);
		} else {
			categoryList.stream().filter(CategoryComp::isOpened).filter(cat -> !cat.getModules().isEmpty()).flatMap(cat -> cat.getModules().stream()).forEach(c -> c.keyTyped(t, k));
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
