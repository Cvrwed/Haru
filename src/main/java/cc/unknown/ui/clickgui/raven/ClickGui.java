package cc.unknown.ui.clickgui.raven;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import cc.unknown.Haru;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.impl.visuals.ClickGuiModule;
import cc.unknown.ui.clickgui.raven.impl.CategoryComp;
import cc.unknown.utils.client.FuckUtil;
import cc.unknown.utils.client.RenderUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

public class ClickGui extends GuiScreen {
	private final ArrayList<CategoryComp> categoryList;
	private final Map<String, ResourceLocation> waifuMap = new HashMap<>();
	private boolean isDragging = false;
	private AtomicInteger lastMouseX = new AtomicInteger(0);
	private AtomicInteger lastMouseY = new AtomicInteger(0);

	public ClickGui() {
		this.categoryList = new ArrayList<>();
		AtomicInteger offset = new AtomicInteger(5);
		Arrays.stream(ModuleCategory.values()).map(category -> {
			CategoryComp comp = new CategoryComp(category);
			comp.setY(offset.getAndAdd(20));
			return comp;
		}).forEach(categoryList::add);
		
		String[] waifuNames = { "astolfo", "hideri", "manolo", "bunny", "katana", "kurumi", "uzaki", "fujiwara", "komi" };
		Arrays.stream(waifuNames).forEach(name -> waifuMap.put(name, new ResourceLocation("haru/img/clickgui/" + name + ".png")));
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

		categoryList.forEach(category -> {
			category.render(this.fontRendererObj);
			category.updste(mouseX, mouseY);
			category.getModules().forEach(module -> module.update(mouseX, mouseY));
		});
		
		if (waifuImage != null && !cg.waifuMode.is("None")) {
		RenderUtil.drawImage(waifuImage, FuckUtil.instance.getWaifuX(), FuckUtil.instance.getWaifuY(),
				sr.getScaledWidth() / 5.2f, sr.getScaledHeight() / 2f);
		} else {
			isDragging = false;
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
		ScaledResolution sr = new ScaledResolution(mc);
		if (isBound(mouseX, mouseY, sr) && mouseButton == 0) {
			isDragging = true;
			lastMouseX.set(mouseX);
			lastMouseY.set(mouseY);
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
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int state) {
		ScaledResolution sr = new ScaledResolution(mc);
		
		if (state == 0) {
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
	}

	@Override
	public void keyTyped(char t, int k) {
		if (k == 54 || k == 1) {
			mc.displayGuiScreen(null);
		} else {
			categoryList.stream().filter(CategoryComp::isOpened).filter(cat -> !cat.getModules().isEmpty())
					.flatMap(cat -> cat.getModules().stream()).forEach(c -> c.keyTyped(t, k));
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
