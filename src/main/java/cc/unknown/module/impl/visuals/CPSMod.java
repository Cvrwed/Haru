package cc.unknown.module.impl.visuals;

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

	private BooleanValue showLeft = new BooleanValue("Left button", true);
	private BooleanValue showRight = new BooleanValue("Right button", false);

	public CPSMod() {
		super("CpsDisplay", ModuleCategory.Visuals);
		this.registerSetting(showLeft, showRight);
	}

	@EventLink
	public void onRender(Render2DEvent e) {
		if (!PlayerUtil.inGame())
			return;

		ScaledResolution res = new ScaledResolution(mc);
		int width = res.getScaledWidth() / 2;
		int height = res.getScaledHeight() / 100;

		if (showLeft.isToggled()) {
			String left = CPSHelper.getCPS(MouseButton.LEFT) + " Left CPS";
			mc.fontRendererObj.drawString(left, width - mc.fontRendererObj.getStringWidth(left), height, Theme.getMainColor().getRGB(), true);
		}

		if (showRight.isToggled()) {
			String right = "Right CPS " + CPSHelper.getCPS(MouseButton.RIGHT);
			mc.fontRendererObj.drawString(right, width + 2, height, Theme.getMainColor().getRGB(), true);

		}
	}
}
