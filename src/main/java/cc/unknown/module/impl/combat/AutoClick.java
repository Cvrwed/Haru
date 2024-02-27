package cc.unknown.module.impl.combat;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.input.Mouse;

import cc.unknown.event.impl.api.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.ClientUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;

public class AutoClick extends Module {
	private DoubleSliderValue leftCPS = new DoubleSliderValue("Left CPS", 16, 19, 1, 80, 1);
	private BooleanValue weaponOnly = new BooleanValue("Weapon only", false);
	public BooleanValue breakBlocks = new BooleanValue("Break blocks", false);
	private BooleanValue hitSelect = new BooleanValue("Hit select", false);
	private SliderValue hitSelectDistance = new SliderValue("Hit select distance", 4, 1, 15, 0.5);
	private ModeValue clickStyle = new ModeValue("Click Style", "Raven", "Raven", "Kuru", "Megumi");
	
	private long lastClick;
	private long leftHold;
	private boolean leftDown;
	protected boolean allowedClick;
	private long leftDownTime;
	private long leftUpTime;
	private long leftk;
	private long leftl;
	private double leftm;
	private boolean leftn;
	private boolean breakHeld;
	private Random rand = null;

	public AutoClick() {
		super("AutoClick", ModuleCategory.Combat);
		this.registerSetting(leftCPS, weaponOnly, breakBlocks, hitSelect, hitSelectDistance, clickStyle);
	}

	@Override
	public void onEnable() {
		this.allowedClick = false;
		this.rand = new Random();
	}

	@Override
	public void onDisable() {
		this.leftDownTime = 0L;
		this.leftUpTime = 0L;
	}

	@EventLink
	public void onRender(Render3DEvent ev) {
		if (!(mc.currentScreen == null && !(mc.currentScreen instanceof GuiInventory)
				&& !(mc.currentScreen instanceof GuiChest)))
			return;

		if (hitSelect.isToggled() && !hitSelectLogic())
			return;

		switch (clickStyle.getMode()) {
		case "Raven": {
			ravenClick();
		}
			break;
		case "Kuru": {
			kuruClick();
		}
			break;
		case "Megumi": {
			megumiClick();
		}
			break;
		}
	}

	private void megumiClick() {
		double speedLeft1 = 1.0 / ThreadLocalRandom.current().nextGaussian() * leftCPS.getInputMax()
				+ leftCPS.getInputMax();
		double leftHoldLength = speedLeft1 / ThreadLocalRandom.current().nextGaussian() * leftCPS.getInputMin()
				+ leftCPS.getInputMin();
		Mouse.poll();

		if (mc.currentScreen != null || !mc.inGameHasFocus) {
			return;
		}

		if (Mouse.isButtonDown(0)) {
			if (breakBlockLogic() || (weaponOnly.isToggled() && !PlayerUtil.isHoldingWeapon())) {
				return;
			}

			double speedLeft = 1.0
					/ ThreadLocalRandom.current().nextDouble(leftCPS.getInputMin() - 0.2D, leftCPS.getInputMax());
			if (System.currentTimeMillis() - lastClick > speedLeft * 1000) {
				lastClick = System.currentTimeMillis();
				if (leftHold < lastClick) {
					leftHold = lastClick;
				}
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), true);
				KeyBinding.onTick(mc.gameSettings.keyBindAttack.getKeyCode());
	
			} else if (leftHold > leftHoldLength * 1000) {
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
			}
		}
	}

	private void kuruClick() {
		double speedLeft1 = 1.0
				/ ThreadLocalRandom.current().nextDouble(leftCPS.getInputMin() - 0.2D, leftCPS.getInputMax());
		double leftHoldLength = speedLeft1
				/ ThreadLocalRandom.current().nextDouble(leftCPS.getInputMin() - 0.02D, leftCPS.getInputMax());

		Mouse.poll();

		if (mc.currentScreen != null || !mc.inGameHasFocus) {
			return;
		}

		if (Mouse.isButtonDown(0)) {
			if (breakBlockLogic() || (weaponOnly.isToggled() && !PlayerUtil.isHoldingWeapon())) {
				return;
			}

			double speedLeft = 1.0
					/ ThreadLocalRandom.current().nextDouble(leftCPS.getInputMin() - 0.2, leftCPS.getInputMax());
			if (System.currentTimeMillis() - lastClick > speedLeft * 1000) {
				lastClick = System.currentTimeMillis();
				if (leftHold < lastClick) {
					leftHold = lastClick;
				}
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), true);
				KeyBinding.onTick(mc.gameSettings.keyBindAttack.getKeyCode());

			} else if (System.currentTimeMillis() - leftHold > leftHoldLength * 1000) {
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);

			}
		}
	}

	private void ravenClick() {
		if (mc.currentScreen != null || !mc.inGameHasFocus) {
			return;
		}

		Mouse.poll();
		if (!Mouse.isButtonDown(0) && !leftDown) {
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);

		}
		if (Mouse.isButtonDown(0) || leftDown) {
			if (weaponOnly.isToggled() && !PlayerUtil.isHoldingWeapon()) {
				return;
			}
			this.leftClickExecute(mc.gameSettings.keyBindAttack.getKeyCode());
		}
	}

	public void leftClickExecute(int key) {

		if (breakBlockLogic())
			return;

		if (this.leftUpTime > 0L && this.leftDownTime > 0L) {
			if (System.currentTimeMillis() > this.leftUpTime && leftDown) {
				KeyBinding.setKeyBindState(key, true);
				KeyBinding.onTick(key);
				this.genLeftTimings();
				leftDown = false;
			} else if (System.currentTimeMillis() > this.leftDownTime) {
				KeyBinding.setKeyBindState(key, false);
				leftDown = true;
			}
		} else {
			this.genLeftTimings();
		}

	}

	public void genLeftTimings() {
		double clickSpeed = ClientUtil.ranModuleVal(leftCPS, this.rand) + 0.4D * this.rand.nextDouble();
		long delay = (int) Math.round(1000.0D / clickSpeed);
		if (System.currentTimeMillis() > this.leftk) {
			if (!this.leftn && this.rand.nextInt(100) >= 85) {
				this.leftn = true;
				this.leftm = 1.1D + this.rand.nextDouble() * 0.15D;
			} else {
				this.leftn = false;
			}

			this.leftk = System.currentTimeMillis() + 500L + (long) this.rand.nextInt(1500);
		}

		if (this.leftn) {
			delay = (long) ((double) delay * this.leftm);
		}

		if (System.currentTimeMillis() > this.leftl) {
			if (this.rand.nextInt(100) >= 80) {
				delay += 50L + (long) this.rand.nextInt(100);
			}

			this.leftl = System.currentTimeMillis() + 500L + (long) this.rand.nextInt(1500);
		}

		this.leftUpTime = System.currentTimeMillis() + delay;
		this.leftDownTime = System.currentTimeMillis() + delay / 2L - (long) this.rand.nextInt(10);
	}

	public boolean hitSelectLogic() {
		if (!hitSelect.isToggled())
			return false;
		if (mc.objectMouseOver != null
				&& mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
			Entity target = mc.objectMouseOver.entityHit;
			if (target instanceof EntityPlayer) {
				EntityPlayer targetPlayer = (EntityPlayer) target;
				return hitSelect.isToggled()
						&& PlayerUtil.lookingAtPlayer(mc.thePlayer, targetPlayer, hitSelectDistance.getInput());
			}
		}
		return false;
	}

	public boolean breakBlockLogic() {
		if (breakBlocks.isToggled() && mc.objectMouseOver != null) {
			BlockPos p = mc.objectMouseOver.getBlockPos();

			if (p != null) {
				Block bl = mc.theWorld.getBlockState(p).getBlock();
				if (bl != Blocks.air && !(bl instanceof BlockLiquid)) {
					if (!breakHeld) {
						int e = mc.gameSettings.keyBindAttack.getKeyCode();
						KeyBinding.setKeyBindState(e, true);
						KeyBinding.onTick(e);
						breakHeld = true;
					}
					return true;
				}
				if (breakHeld) {
					breakHeld = false;
				}
			}
		}
		return false;
	}
}
