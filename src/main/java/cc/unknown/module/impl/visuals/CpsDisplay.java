package cc.unknown.module.impl.visuals;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntSupplier;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.render.RenderEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.helpers.CPSHelper;
import cc.unknown.utils.helpers.CPSHelper.MouseButton;
import net.minecraft.client.gui.ScaledResolution;

@Register(name = "CpsDisplay", category = Category.Visuals)
public class CpsDisplay extends Module {

	private AtomicInteger width = new AtomicInteger();
	private AtomicInteger height = new AtomicInteger();
	private BooleanValue showLeft = new BooleanValue("Left button", true);
	private BooleanValue showRight = new BooleanValue("Right button", false);
	private SliderValue color = new SliderValue("Color [H/S/B]", 0, 0, 350, 10);

	public CpsDisplay() {
		this.registerSetting(showLeft, showRight, color);
	}

	@EventLink
	public void onDraw(RenderEvent e) {
	    if (e.is2D()) {
	        if (mc.currentScreen != null || mc.gameSettings.showDebugInfo) {
	            return;
	        }

	        ScaledResolution res = new ScaledResolution(mc);
	        int screenWidth = res.getScaledWidth();
	        int screenHeight = res.getScaledHeight();

	        width.set(screenWidth / 2);
	        height.set(screenHeight / 100);

	        drawWithBackground(showLeft, CPSHelper.getCPS(MouseButton.LEFT) + " Left CPS", () -> width.get() - 5, height::get, screenWidth, screenHeight);
	        drawWithBackground(showRight, "Right CPS " + CPSHelper.getCPS(MouseButton.RIGHT), () -> width.get() + 72, height::get, screenWidth, screenHeight);
	    }
	}

	private void drawWithBackground(BooleanValue bool, String text, IntSupplier xSupplier, IntSupplier ySupplier, int screenWidth, int screenHeight) {
	    if (bool.isToggled()) {
	        int textWidth = mc.fontRendererObj.getStringWidth(text);
	        int x = xSupplier.getAsInt() - textWidth;
	        int y = ySupplier.getAsInt();
	        mc.fontRendererObj.drawString(text, x, y, Color.getHSBColor((color.getInputToFloat() % 360) / 360.0f, 1.0f, 1.0f).getRGB(), true);
	    }
	}
}
