package cc.unknown.module.impl.visuals;

import static cc.unknown.ui.EditHudPositionScreen.arrayListX;
import static cc.unknown.ui.EditHudPositionScreen.arrayListY;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import cc.unknown.Haru;
import cc.unknown.event.impl.api.EventLink;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.impl.settings.Colors;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.ui.EditHudPositionScreen;
import cc.unknown.ui.clickgui.raven.ClickGui;
import cc.unknown.ui.clickgui.theme.Theme;
import cc.unknown.utils.VisibilityModules;
import cc.unknown.utils.client.ColorUtil;
import cc.unknown.utils.client.FuckUtil;
import cc.unknown.utils.client.FuckUtil.PositionMode;
import cc.unknown.utils.font.FontUtil;
import net.minecraft.client.gui.Gui;

public class HUD extends Module {
	private ModeValue colorMode = new ModeValue("ArrayList Theme", "Static", "Static", "Slinky", "Astolfo", "Primavera",
			"Ocean", "Theme");
	private BooleanValue editPosition = new BooleanValue("Edit Position", false);
	public BooleanValue alphabeticalSort = new BooleanValue("Alphabetical Sort", false);
	private BooleanValue noRenderModules = new BooleanValue("No Render Modules", true);
	private BooleanValue background = new BooleanValue("Background", true);
	private BooleanValue customFont = new BooleanValue("Custom font", true);

	public HUD() {
		super("Hud", ModuleCategory.Visuals);
		this.registerSetting(colorMode, editPosition, alphabeticalSort, noRenderModules, background, customFont);
	}

	@Override
	public void onEnable() {
		Haru.instance.getModuleManager().sort();
	}

	@Override
	public void guiButtonToggled(BooleanValue b) {
		if (b == editPosition) {
			editPosition.disable();
			mc.displayGuiScreen(new EditHudPositionScreen());
		} else if (b == alphabeticalSort) {
			Haru.instance.getModuleManager().sort();
		}
	}

	@EventLink
	public void onRender(Render2DEvent ev) {
		if (mc.gameSettings.showDebugInfo) {
			return;
		} else if (mc.currentScreen instanceof ClickGui) {
			return;
		}

		int margin = 2;
		int y = arrayListY;

		if (noRenderModules.isToggled()) {
			VisibilityModules.setVisible(false);
		} else {
			VisibilityModules.setVisible(true);
		}

		if (alphabeticalSort.isToggled()) {
			if (FuckUtil.getPositionMode() == PositionMode.UPLEFT
					|| FuckUtil.getPositionMode() == PositionMode.UPRIGHT) {
				Haru.instance.getModuleManager().sortLongShort();
			} else if (FuckUtil.getPositionMode() == PositionMode.DOWNLEFT
					|| FuckUtil.getPositionMode() == PositionMode.DOWNRIGHT) {
				Haru.instance.getModuleManager().sortShortLong();
			}
		}

		List<Module> en = new ArrayList<>(Haru.instance.getModuleManager().getModule());
		if (en.isEmpty())
			return;

		int textBoxWidth = Haru.instance.getModuleManager().getLongestActiveModule(mc.fontRendererObj);
		int textBoxHeight = Haru.instance.getModuleManager().getBoxHeight(mc.fontRendererObj, margin);

		if (arrayListX < 0)
			arrayListX = margin;
		if (arrayListY < 0)
			arrayListY = margin;

		if ((arrayListX + textBoxWidth) > (mc.displayWidth / 2))
			arrayListX = (mc.displayWidth / 2) - textBoxWidth - margin;

		if ((arrayListY + textBoxHeight) > (mc.displayHeight / 2))
			arrayListY = (mc.displayHeight / 2) - textBoxHeight;

		int color = 0;
		for (Module m : en) {
			if (m.isEnabled() && m.showInHud()) {
				switch (colorMode.getMode()) {
				case "Static":
					color = Color.getHSBColor((float) (Colors.colors.getInput() % 360) / 360.0f, 1.0f, 1.0f).getRGB();
					y += mc.fontRendererObj.FONT_HEIGHT + margin;
					break;
				case "Slinky":
					color = ColorUtil.reverseGradientDraw(new Color(255, 165, 128), new Color(255, 0, 255), y).getRGB();
					y += mc.fontRendererObj.FONT_HEIGHT + margin;
					break;
				case "Astolfo":
					color = ColorUtil.reverseGradientDraw(new Color(243, 145, 216), new Color(152, 165, 243),
							new Color(64, 224, 208), y).getRGB();
					y += mc.fontRendererObj.FONT_HEIGHT + margin;
					break;
				case "Primavera":
					color = ColorUtil.reverseGradientDraw(new Color(0, 206, 209), new Color(255, 255, 224),
							new Color(211, 211, 211), y).getRGB();
					y += mc.fontRendererObj.FONT_HEIGHT + margin;
					break;
				case "Ocean":
					color = ColorUtil.reverseGradientDraw(new Color(0, 0, 128), new Color(0, 255, 255),
							new Color(173, 216, 230), y).getRGB();
					y += mc.fontRendererObj.FONT_HEIGHT + margin;
					break;
				case "Theme":
					color = Theme.getMainColor().getRGB();
					y += mc.fontRendererObj.FONT_HEIGHT + margin;
					break;
				}

				if ((FuckUtil.getPositionMode() == PositionMode.DOWNRIGHT)
						|| (FuckUtil.getPositionMode() == PositionMode.UPRIGHT)) {
					if (background.isToggled()) {
						if (customFont.isToggled()) {
							Gui.drawRect(arrayListX + (textBoxWidth) - 3, y,
									(int) (arrayListX + (textBoxWidth - FontUtil.two.getStringWidth(m.getName()) - 2)),
									y + FontUtil.two.getHeight() + 4, (new Color(0, 0, 0, 87)).getRGB());
						} else {
							Gui.drawRect(arrayListX + (textBoxWidth) + 2, y,
									(int) arrayListX
											+ (textBoxWidth - mc.fontRendererObj.getStringWidth(m.getName()) - 3),
									y + mc.fontRendererObj.FONT_HEIGHT + 3, (new Color(0, 0, 0, 87)).getRGB());
						}
					}

					if (customFont.isToggled()) {
						FontUtil.two.drawStringWithShadow(m.getName(),
								(float) arrayListX + (textBoxWidth - FontUtil.two.getStringWidth(m.getName())), y + 2,
								color);
					} else {
						mc.fontRendererObj.drawString(m.getName(),
								(float) arrayListX + (textBoxWidth - mc.fontRendererObj.getStringWidth(m.getName())),
								(float) y + 2, color, true);
					}

				} else {
					if (background.isToggled()) {
						if (customFont.isToggled()) { // background height width
							Gui.drawRect(arrayListX - 3, y,
									(int) (arrayListX + FontUtil.two.getStringWidth(m.getName()) + 3),
									y + mc.fontRendererObj.FONT_HEIGHT + 2, (new Color(0, 0, 0, 100)).getRGB());
						} else {
							Gui.drawRect(arrayListX - 2, y,
									arrayListX + mc.fontRendererObj.getStringWidth(m.getName()) + 2,
									y + mc.fontRendererObj.FONT_HEIGHT + 3, (new Color(0, 0, 0, 87)).getRGB());
						}
					}
					if (customFont.isToggled()) { // background up down
						FontUtil.two.drawStringWithShadow(m.getName(), (float) arrayListX, (float) y + 4, color);
					} else {
						mc.fontRendererObj.drawString(m.getName(), (float) arrayListX, (float) y + 2, color, true);
					}
				}
			}
		}

	}
}
