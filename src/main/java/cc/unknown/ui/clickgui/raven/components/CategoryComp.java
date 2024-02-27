package cc.unknown.ui.clickgui.raven.components;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import cc.unknown.Haru;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.ui.clickgui.Component;
import cc.unknown.ui.clickgui.theme.Theme;
import cc.unknown.utils.client.RenderUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

public class CategoryComp extends GuiScreen {
	private ArrayList<ModuleComp> modulesInCategory = new ArrayList<>();
	private ModuleCategory categoryName;
	private boolean categoryOpened = false;
	private int width = 92;
	private int x = 5;
	private int y = 5;
	private final int bh = 13;
	private boolean inUse = false;
	private int tY = bh + 3;
	private int xx = 0;
	private int yy;
	private boolean n4m = false;
	private String pvp;
	private boolean pin = false;
	private final double marginX = 80;
	private final double marginY = 4.5;

	public CategoryComp(ModuleCategory category) {
		this.categoryName = category;
		for (Module mod : Haru.instance.getModuleManager().getCategory(this.categoryName)) {
			ModuleComp moduleComp = new ModuleComp(mod, this, tY);
			this.modulesInCategory.add(moduleComp);
			tY += 16;
		}
	}

	public ArrayList<ModuleComp> getModules() {
		return this.modulesInCategory;
	}

	public void setX(int n) {
		this.x = n;
		if (Haru.instance.getClientConfig() != null) {
			Haru.instance.getClientConfig().saveConfig();
		}
	}

	public void setY(int y) {
		this.y = y;
		if (Haru.instance.getClientConfig() != null) {
			Haru.instance.getClientConfig().saveConfig();
		}
	}

	public void mousePressed(boolean d) {
		this.inUse = d;
	}

	public boolean p() {
		return this.pin;
	}

	public void cv(boolean on) {
		this.pin = on;
	}

	public boolean isOpened() {
		return this.categoryOpened;
	}

	public void setOpened(boolean on) {
		this.categoryOpened = on;
		if (Haru.instance.getClientConfig() != null) {
			Haru.instance.getClientConfig().saveConfig();
		}
	}

	public void render(FontRenderer r) {
		this.width = 92;
		if (!this.modulesInCategory.isEmpty() && this.categoryOpened) {
			int categoryHeight = 0;

			for (ModuleComp module : this.modulesInCategory) {
				categoryHeight += module.getHeight();
			}

			RenderUtil.drawBorderedRoundedRect1(this.x - 1, this.y, this.x + this.width + 1, this.y + this.bh + categoryHeight + 4, 20, 2, Theme.getMainColor().getRGB(), Theme.getBackColor().getRGB());
		}

		String furry = this.n4m ? this.pvp : this.categoryName.name();
		int gf = (int) r.getStringWidth(this.n4m ? this.pvp : this.categoryName.name());
		int x = this.x + (this.width - gf) / 2;
		int y = this.y + 4;
		r.drawString(furry, (float) x, (float) y, Theme.getMainColor().getRGB(), true);

		if (!this.n4m) {
			GL11.glPushMatrix();
			r.drawStringWithShadow(this.categoryOpened ? "-" : "+", (float) (this.x + marginX),
					(float) ((double) this.y + marginY), Color.white.getRGB());
			GL11.glPopMatrix();
			if (this.categoryOpened && !this.modulesInCategory.isEmpty()) {
				for (Component c2 : this.modulesInCategory) {
					c2.draw();
				}
			}
		}
	}

	public void r3nd3r() {
		int o = this.bh + 3;

		Component c;
		for (Iterator<ModuleComp> var2 = this.modulesInCategory.iterator(); var2.hasNext(); o += c.getHeight()) {
			c = var2.next();
			c.setComponentStartAt(o);
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

	public void updste(int x, int y) {
		if (this.inUse) {
			this.setX(x - this.xx);
			this.setY(y - this.yy);
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

	public ModuleCategory getCategoryName() {
		return categoryName;
	}

	public int getXx() {
		return xx;
	}

	public int getYy() {
		return yy;
	}

	public ArrayList<ModuleComp> getModulesInCategory() {
		return modulesInCategory;
	}

	public void setModulesInCategory(ArrayList<ModuleComp> modulesInCategory) {
		this.modulesInCategory = modulesInCategory;
	}

	public boolean isCategoryOpened() {
		return categoryOpened;
	}

	public void setCategoryOpened(boolean categoryOpened) {
		this.categoryOpened = categoryOpened;
	}

	public boolean isInUse() {
		return inUse;
	}

	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}

	public int gettY() {
		return tY;
	}

	public void settY(int tY) {
		this.tY = tY;
	}

	public boolean isN4m() {
		return n4m;
	}

	public void setN4m(boolean n4m) {
		this.n4m = n4m;
	}

	public String getPvp() {
		return pvp;
	}

	public void setPvp(String pvp) {
		this.pvp = pvp;
	}

	public boolean isPin() {
		return pin;
	}

	public void setPin(boolean pin) {
		this.pin = pin;
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

	public void setCategoryName(ModuleCategory categoryName) {
		this.categoryName = categoryName;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setXx(int xx) {
		this.xx = xx;
	}

	public void setYy(int yy) {
		this.yy = yy;
	}
}
