package cc.unknown.module.impl.combat;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.misc.ClickUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class AutoClick extends Module {
	private BooleanValue leftClick = new BooleanValue("Left Click", true);
	private final DoubleSliderValue leftCPS = new DoubleSliderValue("Left CPS", 16, 19, 1, 80, 1);
	private final BooleanValue weaponOnly = new BooleanValue("Weapon only", false);
	private final BooleanValue breakBlocks = new BooleanValue("Break blocks", false);
	private final BooleanValue hitSelect = new BooleanValue("Hit select", false);
	private final SliderValue hitSelectDistance = new SliderValue("Hit select distance", 10, 1, 20, 5);
	private final SliderValue hitSelectDelay = new SliderValue("Hit select delay", 25, 50, 500, 25);
	private final BooleanValue invClicker = new BooleanValue("Inv clicker", false);
	private final SliderValue invDelay = new SliderValue("Inv delay", 5, 0, 10, 1);

	private BooleanValue rightClick = new BooleanValue("Right Click", false);
	private final DoubleSliderValue rightCPS = new DoubleSliderValue("Right CPS", 12, 16, 1, 80, 0.5);
	private final BooleanValue onlyBlocks = new BooleanValue("Only blocks", false);
	private final BooleanValue allowEat = new BooleanValue("Allow eat & drink", true);
	private final BooleanValue allowBow = new BooleanValue("Allow bow", true);

	private ModeValue clickEvent = new ModeValue("Click Event", "Render", "Render", "Render 2", "Tick");
	private ModeValue clickStyle = new ModeValue("Click Style", "Raven", "Raven", "Kuru", "Megumi");

	private int invClick;

	public AutoClick() {
		super("AutoClick", ModuleCategory.Combat);
		this.registerSetting(leftClick, leftCPS, weaponOnly, breakBlocks, hitSelect, hitSelectDistance, hitSelectDelay,
				invClicker, invDelay, rightClick, rightCPS, onlyBlocks, allowEat, allowBow, clickEvent, clickStyle);
	}

	@Override
	public void onEnable() {
		ClickUtil.instance.setRand(new Random());
	}

	@Override
	public void onDisable() {
		ClickUtil.instance.setLeftDownTime(0L);
		ClickUtil.instance.setLeftUpTime(0L);
	}

	@EventLink
	public void onRender2D(Render2DEvent e) {
		if (invClicker.isToggled()) {
			if (!Mouse.isButtonDown(0) || !Keyboard.isKeyDown(54) && !Keyboard.isKeyDown(42)) {
				invClick = 0;
				return;
			}
			invClick++;
			inInvClick(mc.currentScreen);
		}
		
		if (clickEvent.is("Render 2")) {
			onClick();
		}
	}

	@EventLink
	public void onRender3D(Render3DEvent e) {
		if (clickEvent.is("Render")) {
			onClick();
		}
	}

	@EventLink
	public void onTick(TickEvent e) {
		if (clickEvent.is("Tick")) {
			onClick();
		}
	}

	private void onClick() {
		if (leftClick.isToggled() && rightClick.isToggled()) {
			switch (clickStyle.getMode()) {
			case "Raven":
				ClickUtil.instance.ravenLeftClick();
				ClickUtil.instance.ravenRightClick();
				break;
			case "Kuru":
				ClickUtil.instance.kuruLeftClick();
				ClickUtil.instance.kuruRightClick();
				break;
			case "Megumi":
				ClickUtil.instance.megumiLeftClick();
				ClickUtil.instance.megumiRightClick();
				break;
			}
		} else if (leftClick.isToggled()) {
			switch (clickStyle.getMode()) {
			case "Raven":
				ClickUtil.instance.ravenLeftClick();
				break;
			case "Kuru":
				ClickUtil.instance.kuruLeftClick();
				break;
			case "Megumi":
				ClickUtil.instance.megumiLeftClick();
				break;
			}
		} else if (rightClick.isToggled()) {
			switch (clickStyle.getMode()) {
			case "Raven":
				ClickUtil.instance.ravenRightClick();
				break;
			case "Kuru":
				ClickUtil.instance.kuruRightClick();
				break;
			case "Megumi":
				ClickUtil.instance.megumiRightClick();
				break;
			}
		}
	}

	public DoubleSliderValue getLeftCPS() {
		return leftCPS;
	}

	public DoubleSliderValue getRightCPS() {
		return rightCPS;
	}

	public BooleanValue getBreakBlocks() {
		return breakBlocks;
	}

	public BooleanValue getHitSelect() {
		return hitSelect;
	}

	public SliderValue getHitSelectDistance() {
		return hitSelectDistance;
	}

	public BooleanValue getAllowEat() {
		return allowEat;
	}

	public BooleanValue getAllowBow() {
		return allowBow;
	}

	public BooleanValue getWeaponOnly() {
		return weaponOnly;
	}

	public BooleanValue getOnlyBlocks() {
		return onlyBlocks;
	}

	public SliderValue getHitSelectDelay() {
		return hitSelectDelay;
	}

	private void inInvClick(GuiScreen gui) {
		int x = Mouse.getX() * gui.width / mc.displayWidth;
		int y = gui.height - Mouse.getY() * gui.height / mc.displayHeight - 1;

		try {
			if (invClick >= invDelay.getInput()) {
				ReflectionHelper.findMethod(GuiScreen.class, null, new String[] { "func_73864_a", "mouseClicked" },
						Integer.TYPE, Integer.TYPE, Integer.TYPE).invoke(gui, x, y, 0);
				invClick = 0;
			}
		} catch (IllegalAccessException | InvocationTargetException ignored) {
		}
	}
}
