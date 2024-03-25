package cc.unknown.module.impl.visuals;

import static cc.unknown.ui.clickgui.EditHudPositionScreen.arrayListX;
import static cc.unknown.ui.clickgui.EditHudPositionScreen.arrayListY;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import cc.unknown.Haru;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.impl.settings.Colors;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.ui.clickgui.EditHudPositionScreen;
import cc.unknown.ui.clickgui.raven.ClickGui;
import cc.unknown.ui.clickgui.raven.theme.Theme;
import cc.unknown.utils.client.ColorUtil;
import cc.unknown.utils.client.FuckUtil;
import cc.unknown.utils.client.FuckUtil.PositionMode;
import cc.unknown.utils.font.FontUtil;
import net.minecraft.client.gui.Gui;

public class HUD extends Module {
	private ModeValue colorMode = new ModeValue("ArrayList Theme", "Slinky", "Static", "Slinky", "Astolfo", "Primavera",
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
		if (mc.gameSettings.showDebugInfo || mc.currentScreen instanceof ClickGui) {
			return;
		}

		setVisible(!noRenderModules.isToggled());

		int margin = 2;
		AtomicInteger y = new AtomicInteger(arrayListY.get());

		if (alphabeticalSort.isToggled()) {
			if (Arrays.asList(PositionMode.UPLEFT, PositionMode.UPRIGHT).contains(FuckUtil.instance.getPositionMode())) {
				Haru.instance.getModuleManager().sortLongShort();
			} else if (Arrays.asList(PositionMode.DOWNLEFT, PositionMode.DOWNRIGHT).contains(FuckUtil.instance.getPositionMode())) {
				Haru.instance.getModuleManager().sortShortLong();
			}
		}

		List<Module> en = new ArrayList<>(Haru.instance.getModuleManager().getModule());
		if (en.isEmpty()) {
			return;
		}

		AtomicInteger textBoxWidth = new AtomicInteger(Haru.instance.getModuleManager().getLongestActiveModule(mc.fontRendererObj));
		AtomicInteger textBoxHeight = new AtomicInteger(Haru.instance.getModuleManager().getBoxHeight(mc.fontRendererObj, margin));

		if (arrayListX.get() < 0) {
			arrayListX.set(margin);
		}

		if (arrayListY.get() < 0) {
			arrayListY.set(margin);
		}

		arrayListX.set((arrayListX.get() + textBoxWidth.get() > mc.displayWidth / 2) ? (mc.displayWidth / 2 - textBoxWidth.get() - margin) : arrayListX.get());

		arrayListY.set((arrayListY.get() + textBoxHeight.get() > mc.displayHeight / 2) ? (mc.displayHeight / 2 - textBoxHeight.get()) : arrayListY.get());

		AtomicInteger color = new AtomicInteger(0);

		en.stream().filter(m -> m.isEnabled() && m.isHidden()).forEach(m -> {
			Colors col = (Colors) Haru.instance.getModuleManager().getModule(Colors.class);
			switch (colorMode.getMode()) {
			case "Static":
				color.set(Color.getHSBColor((col.getArrayColor().getInputToFloat() % 360) / 360.0f,
						col.getSaturation().getInputToFloat(), col.getBrightness().getInputToFloat()).getRGB());
				y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
				break;
			case "Slinky":
				color.set(ColorUtil.reverseGradientDraw(new Color(255, 165, 128), new Color(255, 0, 255), y.get()).getRGB());
				y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
				break;
			case "Astolfo":
				color.set(ColorUtil.reverseGradientDraw(new Color(243, 145, 216), new Color(152, 165, 243), new Color(64, 224, 208), y.get()).getRGB());
				y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
				break;
			case "Primavera":
				color.set(ColorUtil.reverseGradientDraw(new Color(0, 206, 209), new Color(255, 255, 224), new Color(211, 211, 211), y.get()).getRGB());
				y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
				break;
			case "Ocean":
				color.set(ColorUtil.reverseGradientDraw(new Color(0, 0, 128), new Color(0, 255, 255), new Color(173, 216, 230), y.get()).getRGB());
				y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
				break;
			case "Theme":
				color.set(Theme.getMainColor().getRGB());
				y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
				break;
			}

			if ((FuckUtil.instance.getPositionMode() == PositionMode.DOWNRIGHT)
					|| (FuckUtil.instance.getPositionMode() == PositionMode.UPRIGHT)) {
				if (background.isToggled()) {
					if (customFont.isToggled()) {
						Gui.drawRect(arrayListX.get() + (textBoxWidth.get()) + 4, y.get(),
								(int) (arrayListX.get()
										+ (textBoxWidth.get() - FontUtil.two.getStringWidth(m.getName()) - 2)),
								y.get() + FontUtil.two.getHeight() + 6, (new Color(0, 0, 0, 87)).getRGB());
					} else {
						Gui.drawRect(arrayListX.get() + (textBoxWidth.get()) + 2, y.get(),
								(int) arrayListX.get()
										+ (textBoxWidth.get() - mc.fontRendererObj.getStringWidth(m.getName()) - 3),
								y.get() + mc.fontRendererObj.FONT_HEIGHT + 3, (new Color(0, 0, 0, 87)).getRGB());
					}
				}

				if (customFont.isToggled()) {
					FontUtil.two.drawStringWithShadow(m.getName(),
							(float) arrayListX.get() + (textBoxWidth.get() - FontUtil.two.getStringWidth(m.getName())),
							y.get() + 4, color.get());
				} else {
					mc.fontRendererObj.drawString(m.getName(),
							(float) arrayListX.get()
									+ (textBoxWidth.get() - mc.fontRendererObj.getStringWidth(m.getName())),
							(float) y.get() + 2, color.get(), true);
				}

			} else {
				if (background.isToggled()) {
					if (customFont.isToggled()) { // background height width
						Gui.drawRect(arrayListX.get() - 3, y.get(),
								(int) (arrayListX.get() + FontUtil.two.getStringWidth(m.getName()) + 3),
								y.get() + mc.fontRendererObj.FONT_HEIGHT + 2, (new Color(0, 0, 0, 100)).getRGB());
					} else {
						Gui.drawRect(arrayListX.get() - 2, y.get(),
								arrayListX.get() + mc.fontRendererObj.getStringWidth(m.getName()) + 2,
								y.get() + mc.fontRendererObj.FONT_HEIGHT + 3, (new Color(0, 0, 0, 87)).getRGB());
					}
				}
				if (customFont.isToggled()) { // background up down
					FontUtil.two.drawStringWithShadow(m.getName(), (float) arrayListX.get(), (float) y.get() + 4,
							color.get());
				} else {
					mc.fontRendererObj.drawString(m.getName(), (float) arrayListX.get(), (float) y.get() + 2,
							color.get(), true);
				}
			}
		});
	}
	
}
