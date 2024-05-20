package cc.unknown.module.impl.combat;

import java.util.concurrent.atomic.AtomicReference;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.event.impl.render.RenderEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.misc.ClickUtil;

@Register(name = "AutoClick", category = Category.Combat)
public class AutoClick extends Module {

	private final ModeValue clickMode = new ModeValue("Click Mode", "Left", "Left", "Right", "Both");
	private final ModeValue clickStyle = new ModeValue("Click Style", "Normal", "Normal", "Double Click");

	private final DoubleSliderValue leftCPS = new DoubleSliderValue("Left Click Speed", 16, 19, 1, 40, 1);
	private final BooleanValue weaponOnly = new BooleanValue("Only Use Weapons", false);
	private final BooleanValue breakBlocks = new BooleanValue("Break Blocks", false);
	private final BooleanValue hitSelect = new BooleanValue("Precise Hit Selection", false);
	private final SliderValue hitSelectDistance = new SliderValue("Hit Range", 10, 1, 20, 5);
	private BooleanValue invClicker = new BooleanValue("Auto-Click in Inventory", false);
	private ModeValue invMode = new ModeValue("Inventory Click Mode", "Pre", "Pre", "Post");
	private final SliderValue invDelay = new SliderValue("Click Tick Delay", 5, 0, 10, 1);

	private final DoubleSliderValue rightCPS = new DoubleSliderValue("Right Click Speed", 12, 16, 1, 40, 1);
	private final BooleanValue onlyBlocks = new BooleanValue("Only Use Blocks", false);
	private final BooleanValue allowEat = new BooleanValue("Allow Eating & Drinking", true);
	private final BooleanValue allowBow = new BooleanValue("Allow Using Bow", true);

	public AutoClick() {
		this.registerSetting(clickMode, clickStyle, leftCPS, weaponOnly, breakBlocks, hitSelect, hitSelectDistance,
				invClicker, invMode, invDelay, rightCPS, onlyBlocks, allowEat, allowBow);
	}

	@EventLink
	public void onGui(ClickGuiEvent e) {
		AtomicReference<String> suffixRef = new AtomicReference<>();

		if (clickMode.is("Left")) {
			suffixRef.set("- [" + leftCPS.getInputMinToInt() + ", " + leftCPS.getInputMaxToInt() + "]");
		} else if (clickMode.is("Right")) {
			suffixRef.set("- [" + rightCPS.getInputMinToInt() + ", " + rightCPS.getInputMaxToInt() + "]");
		}

		this.setSuffix(suffixRef.get());
	}

	@EventLink
	public void onMotion(MotionEvent e) {
		if (invClicker.isToggled()) {
			switch (invMode.getMode()) {
			case "Pre":
				if (e.isPre()) {
					ClickUtil.instance.shouldInvClick();
				}
				break;
			case "Post":
				if (e.isPost()) {
					ClickUtil.instance.shouldInvClick();
				}
				break;
			}
		}
	}

	@EventLink
	public void onRender(RenderEvent e) {
		if (e.is3D()) {
			switch (clickMode.getMode()) {
			case "Left":
				ClickUtil.instance.getLeftClick();
				break;
			case "Right":
				ClickUtil.instance.getRightClick();
				break;
			case "Both":
				ClickUtil.instance.getLeftClick();
				ClickUtil.instance.getRightClick();
				break;
			}
		}
	}

	public ModeValue getClickMode() {
		return clickMode;
	}

	public ModeValue getClickStyle() {
		return clickStyle;
	}

	public DoubleSliderValue getLeftCPS() {
		return leftCPS;
	}

	public BooleanValue getWeaponOnly() {
		return weaponOnly;
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

	public SliderValue getInvDelay() {
		return invDelay;
	}

	public DoubleSliderValue getRightCPS() {
		return rightCPS;
	}

	public BooleanValue getOnlyBlocks() {
		return onlyBlocks;
	}

	public BooleanValue getAllowEat() {
		return allowEat;
	}

	public BooleanValue getAllowBow() {
		return allowBow;
	}
}
