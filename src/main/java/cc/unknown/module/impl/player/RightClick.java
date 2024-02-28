package cc.unknown.module.impl.player;

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
import cc.unknown.utils.misc.ClickUtil;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;

public class RightClick extends Module {
	private DoubleSliderValue rightCPS = new DoubleSliderValue("Right CPS", 12, 16, 1, 60, 0.5);
	private BooleanValue onlyBlocks = new BooleanValue("Only blocks", false);
	private BooleanValue allowEat = new BooleanValue("Allow eat & drink", true);
	private BooleanValue allowBow = new BooleanValue("Allow bow", true);
	private ModeValue clickEvent = new ModeValue("Click Event", "Render", "Render", "Render 2", "Tick");
	private ModeValue clickStyle = new ModeValue("Click Style", "Raven", "Raven", "Kuru", "Megumi");

	public RightClick() {
		super("RightClick", ModuleCategory.Player);
		this.registerSetting(rightCPS, onlyBlocks, allowEat, allowBow, clickEvent, clickStyle);
	}
	
	@Override
	public void onEnable() {
		ClickUtil.instance.setRand(new Random());
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
	
	private boolean checkScreen() {
	    return mc.currentScreen != null || mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChest;
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

	public ModeValue getClickStyle() {
		return clickStyle;
	}
}