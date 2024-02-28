package cc.unknown.module.impl.combat;

import java.util.Random;

import cc.unknown.event.impl.api.EventLink;
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
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;

public class AutoClick extends Module {
	private DoubleSliderValue leftCPS = new DoubleSliderValue("Left CPS", 16, 19, 1, 80, 1);
	private BooleanValue weaponOnly = new BooleanValue("Weapon only", false);
	private BooleanValue breakBlocks = new BooleanValue("Break blocks", false);
	private BooleanValue hitSelect = new BooleanValue("Hit select", false);
	private SliderValue hitSelectDistance = new SliderValue("Hit select distance", 4, 1, 15, 0.5);
	private ModeValue clickEvent = new ModeValue("Click Event", "Render", "Render", "Render 2", "Tick");
	private ModeValue clickStyle = new ModeValue("Click Style", "Raven", "Raven", "Kuru", "Megumi");

	public AutoClick() {
		super("AutoClick", ModuleCategory.Combat);
		this.registerSetting(leftCPS, weaponOnly, breakBlocks, hitSelect, hitSelectDistance, clickEvent, clickStyle);
	}

	@Override
	public void onEnable() {
		ClickUtil.instance.setAllowedClick(false);
		ClickUtil.instance.setRand(new Random());
	}

	@Override
	public void onDisable() {
		ClickUtil.instance.setLeftDownTime(0L);
		ClickUtil.instance.setLeftUpTime(0L);
	}

	@EventLink
	public void onRender3D(Render3DEvent e) {
	    if (checkScreen()) return;
	    if (clickEvent.is("Render")) onClick();
	}

	@EventLink
	public void onRender2D(Render2DEvent e) {
	    if (checkScreen()) return;
	    if (clickEvent.is("Render 2")) onClick();
	}

	@EventLink
	public void onTick(TickEvent e) {
	    if (checkScreen()) return;
	    if (clickEvent.is("Tick")) onClick();
	}
	
	private void onClick() {
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
	}
	
	private boolean checkScreen() {
	    return mc.currentScreen != null || mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChest || (hitSelect.isToggled() && !ClickUtil.instance.hitSelectLogic());
	}

	public DoubleSliderValue getLeftCPS() {
		return leftCPS;
	}

	public BooleanValue getWeaponOnly() {
		return weaponOnly;
	}

	public BooleanValue getHitSelect() {
		return hitSelect;
	}

	public SliderValue getHitSelectDistance() {
		return hitSelectDistance;
	}

	public BooleanValue getBreakBlocks() {
		return breakBlocks;
	}

}
