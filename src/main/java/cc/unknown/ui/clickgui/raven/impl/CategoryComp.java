package cc.unknown.ui.clickgui.raven.impl;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import cc.unknown.Haru;
import cc.unknown.module.impl.api.Category;
import cc.unknown.ui.clickgui.raven.impl.api.Component;
import cc.unknown.ui.clickgui.raven.impl.api.Theme;
import cc.unknown.utils.client.RenderUtil;
import net.minecraft.client.gui.FontRenderer;

public class CategoryComp {
	private ArrayList<ModuleComp> modulesInCategory = new ArrayList<>();
	private Category category;
	private boolean open = false;
	private int width = 92; // 92
	private int x = 5;
	private int y = 5;
	private final int bh = 13;
	private boolean dragging = false;
	private AtomicInteger tY = new AtomicInteger(bh + 3);
	private int dragX;
	private int dragY;
	private boolean n4m = false;
	private String pvp;
	private boolean pin = false;
	private final double marginX = 80;
	private final double marginY = 4.5;

	public CategoryComp(Category category) {
		this.category = category;
	    AtomicInteger posY = new AtomicInteger(tY.get());
	    Haru.instance.getModuleManager().getCategory(this.category).forEach(mod -> {
	        ModuleComp moduleComp = new ModuleComp(mod, this, posY.getAndAdd(16));
	        this.modulesInCategory.add(moduleComp);
	    });
	}

	public ArrayList<ModuleComp> getModules() {
		return this.modulesInCategory;
	}

	public void setX(int n) {
		this.x = n;
		if (Haru.instance.getHudConfig() != null) {
			Haru.instance.getHudConfig().saveHud();
		}
	}

	public void setY(int y) {
		this.y = y;
		if (Haru.instance.getHudConfig() != null) {
			Haru.instance.getHudConfig().saveHud();
		}
	}

	public boolean p() {
		return this.pin;
	}

	public void cv(boolean on) {
		this.pin = on;
	}

	public void setOpened(boolean on) {
		this.open = on;
		if (Haru.instance.getHudConfig() != null) {
			Haru.instance.getHudConfig().saveHud();
		}
	}

	public void render(FontRenderer r) {
		this.width = 92;
		if (!this.modulesInCategory.isEmpty() && this.open) {
			int categoryHeight = 0;

			for (ModuleComp module : this.modulesInCategory) {
				categoryHeight += module.getHeight();
			}

			RenderUtil.drawBorderedRoundedRect(this.x - 1, this.y, this.x + this.width + 1, this.y + this.bh + categoryHeight + 4f, 20f, 2f, Theme.instance.getMainColor().getRGB(), Theme.instance.getBackColor().getRGB());
		} else if (!this.open) {
			RenderUtil.drawBorderedRoundedRect(this.x - 1, this.y, this.x + this.width + 1, this.y + this.bh + 4f, 20f, 2f, Theme.instance.getMainColor().getRGB(), Theme.instance.getBackColor().getRGB());
		}

		String center = this.n4m ? this.pvp : this.category.getName();
		int gf = (int) r.getStringWidth(this.n4m ? this.pvp : this.category.getName());
		int x = this.x + (this.width - gf) / 2;
		int y = this.y + 4;
		r.drawStringWithShadow(center, (float) x, (float) y, Theme.instance.getMainColor().getRGB());

		if (!this.n4m) {
			if (this.open && !this.modulesInCategory.isEmpty()) {
			    this.modulesInCategory.forEach(Component::renderComponent);
			}
		}
	}

	public void refresh() {
		int offset = this.bh + 3;

	    for (Component c : this.modulesInCategory) {
	    	c.setOffset(offset);
	    	offset += c.getHeight();
	    }
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getWidth() {
		return this.width;
	}

	public void updatePosition(int x, int y) {
		if (this.dragging) {
			this.setX(x - this.dragX);
			this.setY(y - this.dragY);
		}
	}

	public boolean i(int x, int y) {
		return x >= this.x + 92 - 13 && x <= this.x + this.width && (float) y >= (float) this.y + 2.0F
				&& y <= this.y + this.bh + 1;
	}

	public boolean mousePressed(int x, int y) {
		return x >= this.x + 77 && x <= this.x + this.width - 6 && (float) y >= (float) this.y + 2.0F
				&& y <= this.y + this.bh + 1;
	}

	public boolean isInside(int x, int y) {
		return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.bh;
	}

	public String getName() {
		return String.valueOf(modulesInCategory);
	}

	public Category getCategory() {
		return category;
	}

	public void setDragX(int dragX) {
		this.dragX = dragX;
	}
	
	public void setDragY(int dragY) {
		this.dragY = dragY;
	}

	public ArrayList<ModuleComp> getModulesInCategory() {
		return modulesInCategory;
	}

	public boolean isN4m() {
		return n4m;
	}
	
	public String getPvp() {
		return pvp;
	}

	public AtomicInteger gettY() {
		return tY;
	}

	public boolean isPin() {
		return pin;
	}

	public int getBh() {
		return bh;
	}

	public double getMarginX() {
		return marginX;
	}

	public double getMarginY() {
		return marginY;
	}

	public boolean isDragging() {
		return dragging;
	}

	public void setDragging(boolean dragging) {
		this.dragging = dragging;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

}
