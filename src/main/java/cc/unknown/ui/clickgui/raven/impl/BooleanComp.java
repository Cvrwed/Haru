package cc.unknown.ui.clickgui.raven.impl;

import org.lwjgl.opengl.GL11;

import cc.unknown.module.impl.Module;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.ui.clickgui.raven.impl.api.Component;
import cc.unknown.ui.clickgui.raven.impl.api.Theme;

public class BooleanComp extends Component {
	private final Module mod;
	private final BooleanValue cl1ckbUtt0n;
	private final ModuleComp module;
	private int o;
	private int x;
	private int y;

	public BooleanComp(Module mod, BooleanValue op, ModuleComp b, int o) {
		this.mod = mod;
		this.cl1ckbUtt0n = op;
		this.module = b;
		this.x = b.category.getX() + b.category.getWidth();
		this.y = b.category.getY() + b.o;
		this.o = o;
	}

	public static void e() {
		GL11.glDisable(2929);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glDepthMask(true);
		GL11.glEnable(2848);
		GL11.glHint(3154, 4354);
		GL11.glHint(3155, 4354);
	}

	@Override
	public void renderComponent() {
		GL11.glPushMatrix();
		GL11.glScaled(0.5D, 0.5D, 0.5D);
		mc.fontRendererObj.drawStringWithShadow(
				this.cl1ckbUtt0n.isToggled() ? "[+]  " + this.cl1ckbUtt0n.getName()
						: "[-]  " + this.cl1ckbUtt0n.getName(),
				(float) ((this.module.category.getX() + 4) * 2),
				(float) ((this.module.category.getY() + this.o + 5) * 2),
				this.cl1ckbUtt0n.isToggled() ? Theme.instance.getMainColor().getRGB() : -1);
		GL11.glPopMatrix();
	}

	@Override
	public void setOffset(int n) {
		this.o = n;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public void updateComponent(int mousePosX, int mousePosY) {
		this.y = this.module.category.getY() + this.o;
		this.x = this.module.category.getX();
	}

	@Override
	public void mouseClicked(int x, int y, int b) {
		if (this.i(x, y) && b == 0 && this.module.open) {
			this.cl1ckbUtt0n.toggle();
			this.mod.guiButtonToggled(this.cl1ckbUtt0n);
		}
	}

	@Override
	public void mouseReleased(int x, int y, int m) {

	}

	@Override
	public void keyTyped(char t, int k) {

	}

	public boolean i(int x, int y) {
		return x > this.x && x < this.x + this.module.category.getWidth() && y > this.y && y < this.y + 11;
	}

}
