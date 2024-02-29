package cc.unknown.module.impl.visuals;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntSupplier;

import cc.unknown.event.impl.api.EventLink;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.ui.clickgui.theme.Theme;
import cc.unknown.utils.helpers.CPSHelper;
import cc.unknown.utils.helpers.CPSHelper.MouseButton;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.gui.ScaledResolution;

public class CPSMod extends Module {

    private AtomicInteger width = new AtomicInteger();
    private AtomicInteger height = new AtomicInteger();
	private BooleanValue showLeft = new BooleanValue("Left button", true);
	private BooleanValue showRight = new BooleanValue("Right button", false);

	public CPSMod() {
		super("CpsDisplay", ModuleCategory.Visuals);
		this.registerSetting(showLeft, showRight);
	}

    @EventLink
    public void onRender(Render2DEvent e) {
        if (!PlayerUtil.inGame() || mc.gameSettings.showDebugInfo)
            return;

        ScaledResolution res = new ScaledResolution(mc);
        width.set(res.getScaledWidth() / 2);
        height.set(res.getScaledHeight() / 100);

        draw(showLeft, CPSHelper.getCPS(MouseButton.LEFT) + " Left CPS", () -> width.get() - 5, height::get);
        draw(showRight, "Right CPS " + CPSHelper.getCPS(MouseButton.RIGHT), () -> width.get() + 72, height::get);
    }
    
    private void draw(BooleanValue bool, String text, IntSupplier xSupplier, IntSupplier ySupplier) {
        if (bool.isToggled()) {
            mc.fontRendererObj.drawString(text, xSupplier.getAsInt() - mc.fontRendererObj.getStringWidth(text), ySupplier.getAsInt(), Theme.getMainColor().getRGB(), true);
        }
    }
}
