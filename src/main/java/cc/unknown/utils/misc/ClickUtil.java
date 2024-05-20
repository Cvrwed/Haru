package cc.unknown.utils.misc;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cc.unknown.Haru;
import cc.unknown.module.impl.combat.AutoClick;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.Loona;
import cc.unknown.utils.helpers.MathHelper;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public enum ClickUtil implements Loona {
	instance;

	private boolean breakHeld;
	private int invClick;
	private long leftDelay = 50L;
	private long leftLastSwing = 0L;
	private long rightDelay = 0L;
	private long rightLastSwing = 0L;
	private int clickDelay = 0;

	public int getClickDelay() {
		AutoClick clicker = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);

		switch (clicker.getClickStyle().getMode()) {
		case "Normal":
			switch (clicker.getClickMode().getMode()) {
			case "Left":
				setClickType(clicker.getLeftCPS().getInputMinToInt(), clicker.getLeftCPS().getInputMaxToInt());
				break;
			case "Right":
				setClickType(clicker.getRightCPS().getInputMinToInt(), clicker.getRightCPS().getInputMaxToInt());
				break;
			case "Both":
				setClickType(clicker.getLeftCPS().getInputMinToInt(), clicker.getLeftCPS().getInputMaxToInt());
				setClickType(clicker.getRightCPS().getInputMinToInt(), clicker.getRightCPS().getInputMaxToInt());
				break;
			}
			break;

		}
		return clickDelay;
	}

	private void inInvClick(GuiScreen gui) {
		AutoClick clicker = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);

		int x = Mouse.getX() * gui.width / mc.displayWidth;
		int y = gui.height - Mouse.getY() * gui.height / mc.displayHeight - 1;

		try {
			if (invClick >= clicker.getInvDelay().getInput()) {
				ReflectionHelper.findMethod(GuiScreen.class, null, new String[] { "func_73864_a", "mouseClicked" },
						Integer.TYPE, Integer.TYPE, Integer.TYPE).invoke(gui, x, y, 0);
				invClick = 0;
			}
		} catch (IllegalAccessException | InvocationTargetException ignored) {
		}
	}

	public void getLeftClick() {
		AutoClick clicker = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);
		Mouse.poll();

		if (!mc.inGameHasFocus || checkScreen() || checkHit()) {
			return;
		}

		final int doubleClick = clicker.getClickStyle().is("Double Click") ? MathHelper.simpleRandom(-1, 1) : 0;

		if (Mouse.isButtonDown(0)) {
			if (breakBlockLogic() || (clicker.getWeaponOnly().isToggled() && !PlayerUtil.isHoldingWeapon())) {
				return;
			}

			for (int i = 0; i < 1 + doubleClick; i++) {
				if (System.currentTimeMillis() - leftLastSwing >= leftDelay) {
					leftLastSwing = System.currentTimeMillis();
					leftDelay = getClickDelay();
					KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), true);
					KeyBinding.onTick(mc.gameSettings.keyBindAttack.getKeyCode());
				} else if (leftLastSwing > leftDelay * 1000) {
					KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
				}
			}
		}
	}

	public void getRightClick() {
		AutoClick clicker = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);
		Mouse.poll();

		if (checkScreen() || !mc.inGameHasFocus)
			return;
		
		final int doubleClick = clicker.getClickStyle().is("Double Click") ? MathHelper.simpleRandom(-1, 1) : 0;

		if (Mouse.isButtonDown(1)) {
			if (!rightClickAllowed())
				return;

			for (int i = 0; i < 1 + doubleClick; i++) {
				if (System.currentTimeMillis() - rightLastSwing >= rightDelay) {
					rightLastSwing = System.currentTimeMillis();
					rightDelay = getClickDelay();
					KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
					KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
				} else if (System.currentTimeMillis() - rightLastSwing > rightDelay * 1000) {
					KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
				}
			}
		}
	}

	public boolean hitSelectLogic() {
		AutoClick clicker = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);

		if (clicker.getHitSelect().isToggled()) {
			if (mc.objectMouseOver != null
					&& mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
				Entity target = mc.objectMouseOver.entityHit;
				if (target instanceof EntityPlayer) {
					EntityPlayer targetPlayer = (EntityPlayer) target;
					return PlayerUtil.lookingAtPlayer(mc.thePlayer, targetPlayer,
							clicker.getHitSelectDistance().getInput());
				}
			}
		}
		return false;
	}

	public boolean breakBlockLogic() {
		AutoClick clicker = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);

		if (clicker.getBreakBlocks().isToggled() && mc.objectMouseOver != null) {
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

	public boolean rightClickAllowed() {
		AutoClick right = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);

		ItemStack item = mc.thePlayer.getHeldItem();
		if (item != null) {

			if (item.getItem() instanceof ItemSword) {
				return false;
			} else if (item.getItem() instanceof ItemFishingRod) {
				return false;
			} else if (item.getItem() instanceof ItemBow) {
				return false;
			}

			if (right.getAllowEat().isToggled()) {
				if ((item.getItem() instanceof ItemFood) || item.getItem() instanceof ItemPotion
						|| item.getItem() instanceof ItemBucketMilk) {
					return false;
				}
			}

			if (right.getOnlyBlocks().isToggled()) {
				if (!(item.getItem() instanceof ItemBlock)) {
					return false;
				}
			}
		}

		return true;
	}

	public boolean isClicking() {
		AutoClick clicker = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);
		if (clicker != null && clicker.isEnabled()) {
			return clicker.isEnabled() && Mouse.isButtonDown(0);
		}
		return false;
	}

	public double ranModuleVal(SliderValue a, SliderValue b, Random r) {
		return a.getInput() == b.getInput() ? a.getInput()
				: a.getInput() + r.nextDouble() * (b.getInput() - a.getInput());
	}

	public double ranModuleVal(DoubleSliderValue a, Random r) {
		return a.getInputMin() == a.getInputMax() ? a.getInputMin()
				: a.getInputMin() + r.nextDouble() * (a.getInputMax() - a.getInputMin());
	}

	public boolean checkScreen() {
		return mc.currentScreen != null || mc.currentScreen instanceof GuiInventory
				|| mc.currentScreen instanceof GuiChest;
	}

	public boolean checkHit() {
		AutoClick left = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);
		return (left.getHitSelect().isToggled() && !hitSelectLogic());
	}

	public void shouldInvClick() {
		if (Mouse.isButtonDown(0) && (Keyboard.isKeyDown(54) || Keyboard.isKeyDown(42))) {
			invClick++;
			inInvClick(mc.currentScreen);
			return;
		}
	}

	private void setClickType(int min, int max) {
		clickDelay = MathHelper.randomClickDelay(min, max);
	}
}
