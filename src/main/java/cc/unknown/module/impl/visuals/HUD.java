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
import cc.unknown.event.impl.render.RenderEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.ui.clickgui.EditHudPositionScreen;
import cc.unknown.ui.clickgui.raven.HaruGui;
import cc.unknown.ui.clickgui.raven.impl.api.Theme;
import cc.unknown.utils.client.ColorUtil;
import cc.unknown.utils.client.FuckUtil;
import cc.unknown.utils.client.FuckUtil.PositionMode;
import cc.unknown.utils.misc.HiddenUtil;
import net.minecraft.client.gui.Gui;

@Register(name = "HUD", category = Category.Visuals)
public class HUD extends Module {

	private ModeValue colorMode = new ModeValue("ArrayList Theme", "Static", "Static", "Slinky", "Astolfo", "Primavera",
			"Ocean", "Theme");
	private SliderValue arrayColor = new SliderValue("Array Color [H/S/B]", 0, 0, 350, 10);
	private SliderValue saturation = new SliderValue("Saturation [H/S/B]", 1.0, 0.0, 1.0, 0.1);
	private SliderValue brightness = new SliderValue("Brightness [H/S/B]", 1.0, 0.0, 1.0, 0.1);
	private BooleanValue editPosition = new BooleanValue("Edit Position", false);
	private BooleanValue noRenderModules = new BooleanValue("No Render Modules", true);
	private BooleanValue background = new BooleanValue("Background", true);
	private BooleanValue lowercase = new BooleanValue("Lowercase", false);
	public BooleanValue suffix = new BooleanValue("Suffix", false);

	public HUD() {
		this.registerSetting(colorMode, arrayColor, saturation, brightness, editPosition, noRenderModules, background,
				lowercase, suffix);
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
		}
	}

	@EventLink
	public void onDraw(RenderEvent e) {
		if (e.is2D()) {
			if (mc.gameSettings.showDebugInfo || mc.currentScreen instanceof HaruGui) {
				return;
			}

			HiddenUtil.setVisible(!noRenderModules.isToggled());

			int margin = 2;
			AtomicInteger y = new AtomicInteger(arrayListY.get());

			if (Arrays.asList(PositionMode.DOWNLEFT, PositionMode.DOWNRIGHT)
					.contains(FuckUtil.instance.getPositionMode())) {
				Haru.instance.getModuleManager().sort();
			}

			List<Module> en = new ArrayList<>(Haru.instance.getModuleManager().getModule());
			if (en.isEmpty()) {
				return;
			}

			AtomicInteger textBoxWidth = new AtomicInteger(
					Haru.instance.getModuleManager().getLongestActiveModule(mc.fontRendererObj));
			AtomicInteger textBoxHeight = new AtomicInteger(
					Haru.instance.getModuleManager().getBoxHeight(mc.fontRendererObj, margin));

			if (arrayListX.get() < 0) {
				arrayListX.set(margin);
			}

			if (arrayListY.get() < 0) {
				arrayListY.set(margin);
			}

			arrayListX.set((arrayListX.get() + textBoxWidth.get() > mc.displayWidth / 2)
					? (mc.displayWidth / 2 - textBoxWidth.get() - margin)
					: arrayListX.get());

			arrayListY.set((arrayListY.get() + textBoxHeight.get() > mc.displayHeight / 2)
					? (mc.displayHeight / 2 - textBoxHeight.get())
					: arrayListY.get());

			AtomicInteger color = new AtomicInteger(0);

			en.stream().filter(m -> m.isEnabled() && m.isHidden()).forEach(m -> {

				String nameOrSuffix = m.getRegister().name();
				if (suffix.isToggled()) {
					nameOrSuffix += " §7" + m.getSuffix();
				}
				if (lowercase.isToggled()) {
					nameOrSuffix = nameOrSuffix.toLowerCase();
				}

				switch (colorMode.getMode()) {
				case "Static":
					color.set(Color.getHSBColor((arrayColor.getInputToFloat() % 360) / 360.0f,
							saturation.getInputToFloat(), brightness.getInputToFloat()).getRGB());
					y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
					break;
				case "Slinky":
					color.set(ColorUtil.reverseGradientDraw(new Color(255, 165, 128), new Color(255, 0, 255), y.get())
							.getRGB());
					y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
					break;
				case "Astolfo":
					color.set(ColorUtil.reverseGradientDraw(new Color(243, 145, 216), new Color(152, 165, 243),
							new Color(64, 224, 208), y.get()).getRGB());
					y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
					break;
				case "Primavera":
					color.set(ColorUtil.reverseGradientDraw(new Color(0, 206, 209), new Color(255, 255, 224),
							new Color(211, 211, 211), y.get()).getRGB());
					y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
					break;
				case "Ocean":
					color.set(ColorUtil.reverseGradientDraw(new Color(0, 0, 128), new Color(0, 255, 255),
							new Color(173, 216, 230), y.get()).getRGB());
					y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
					break;
				case "Theme":
					color.set(Theme.instance.getMainColor().getRGB());
					y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
					break;
				}

				if ((FuckUtil.instance.getPositionMode() == PositionMode.DOWNRIGHT)
						|| (FuckUtil.instance.getPositionMode() == PositionMode.UPRIGHT)) {
					if (background.isToggled()) {
						int backgroundWidth;
						backgroundWidth = mc.fontRendererObj.getStringWidth(nameOrSuffix) + 5; // Ajuste adicional para
																								// la fuente
																								// predeterminada
						Gui.drawRect(arrayListX.get() + (textBoxWidth.get()) + 4, y.get(),
								arrayListX.get() + (textBoxWidth.get() - backgroundWidth),
								y.get() + mc.fontRendererObj.FONT_HEIGHT + 2, (new Color(0, 0, 0, 87)).getRGB());
					}

					mc.fontRendererObj.drawString(nameOrSuffix,
							(float) arrayListX.get()	
									+ (textBoxWidth.get() - mc.fontRendererObj.getStringWidth(nameOrSuffix)),
							(float) y.get() + 2, color.get(), true);

				} else {
					if (background.isToggled()) {
						int backgroundWidth;
						backgroundWidth = mc.fontRendererObj.getStringWidth(nameOrSuffix) + 4; // Ajuste adicional
						Gui.drawRect(arrayListX.get() - 3, y.get(), arrayListX.get() + backgroundWidth,
								y.get() + mc.fontRendererObj.FONT_HEIGHT + 2, (new Color(0, 0, 0, 100)).getRGB());
					}

					mc.fontRendererObj.drawString(nameOrSuffix, (float) arrayListX.get(), (float) y.get() + 2,
							color.get(), true);

				}
			});
		}
	}

}
