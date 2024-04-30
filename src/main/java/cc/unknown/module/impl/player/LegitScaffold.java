package cc.unknown.module.impl.player;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.utils.client.Cold;
import cc.unknown.utils.helpers.MathHelper;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldSettings;

@Register(name = "LegitScaffold", category = Category.Player)
public class LegitScaffold extends Module {
	public BooleanValue shiftOnJump = new BooleanValue("Shift While in Air", false);
	public DoubleSliderValue shiftTime = new DoubleSliderValue("Shift Time", 140, 200, 0, 280, 5);
	public DoubleSliderValue pitchRange = new DoubleSliderValue("Pitch Angle Range", 70, 85, 0, 90, 1);
	private BooleanValue onHold = new BooleanValue("On Shift Hold", false);
	public BooleanValue blocksOnly = new BooleanValue("Blocks Only", true);
	public BooleanValue backwards = new BooleanValue("Backwards Movement Only", true);
	public BooleanValue slotSwap = new BooleanValue("Block Switching", true);

	private boolean shouldBridge = false;
	private boolean isShifting = false;
	private Cold shiftTimer = new Cold(0);
	
	public LegitScaffold() {
		this.registerSetting(shiftOnJump, shiftTime, pitchRange, onHold, blocksOnly, backwards, slotSwap);
	}

	@EventLink
	public void onGui(ClickGuiEvent e) {
		this.setSuffix("- [" + shiftTime.getInputMinToInt() + ", " + shiftTime.getInputMaxToInt() + " ms]");
	}
	
	@Override
	public void onDisable() {
		setSneak(false);
		if (PlayerUtil.playerOverAir()) {
			setSneak(false);
		}

		shouldBridge = false;
		isShifting = false;
	}

	@EventLink
	public void onSuicide(TickEvent e) {
		if (!(mc.currentScreen == null) || !PlayerUtil.inGame())
			return;

		boolean x = shiftTime.getInputMax() > 0;

		if (mc.thePlayer.rotationPitch < pitchRange.getInputMin()
				|| mc.thePlayer.rotationPitch > pitchRange.getInputMax()) {
			shouldBridge = false;
			if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
				setSneak(true);
			}
			return;
		}

		if (onHold.isToggled()) {
			if (!Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
				shouldBridge = false;
				return;
			}
		}
		
		if (mc.playerController.getCurrentGameType() == WorldSettings.GameType.SPECTATOR) {
			return;
		}

		if (blocksOnly.isToggled()) {
			ItemStack i = mc.thePlayer.getHeldItem();
			if (i == null || !(i.getItem() instanceof ItemBlock)) {
				if (isShifting) {
					isShifting = false;
					setSneak(false);
				}
				return;
			}
		}

		if (backwards.isToggled()) {
			if ((mc.thePlayer.movementInput.moveForward > 0) && (mc.thePlayer.movementInput.moveStrafe == 0)
					|| mc.thePlayer.movementInput.moveForward >= 0) {
				shouldBridge = false;
				return;
			}
		}

		if (mc.thePlayer.onGround) {
			if (PlayerUtil.playerOverAir()) {
				if (x) {
					shiftTimer
							.setCooldown(MathHelper.randomInt(shiftTime.getInputMin(), shiftTime.getInputMax() + 0.1));
					shiftTimer.start();
				}

				isShifting = true;
				setSneak(true);
				shouldBridge = true;
			} else if (mc.thePlayer.isSneaking() && !Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())
					&& onHold.isToggled()) {
				isShifting = false;
				shouldBridge = false;
				setSneak(false);
			} else if (onHold.isToggled() && !Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
				isShifting = false;
				shouldBridge = false;
				setSneak(false);
			} else if (mc.thePlayer.isSneaking()
					&& (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && onHold.isToggled())
					&& (!x || shiftTimer.hasFinished())) {
				isShifting = false;
				setSneak(false);
				shouldBridge = true;
			} else if (mc.thePlayer.isSneaking() && !onHold.isToggled() && (!x || shiftTimer.hasFinished())) {
				isShifting = false;
				setSneak(false);
				shouldBridge = true;
			}
		} else if (shouldBridge && mc.thePlayer.capabilities.isFlying) {
			setSneak(false);
			shouldBridge = false;
		} else if (shouldBridge && PlayerUtil.playerOverAir() && shiftOnJump.isToggled()) {
			isShifting = true;
			setSneak(true);
		} else {
			isShifting = false;
			setSneak(false);
		}
	}

	@EventLink
	public void onRender(Render3DEvent e) {
		if (PlayerUtil.inGame()) {
			if ((mc.thePlayer.getHeldItem() == null || !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock)) && slotSwap.isToggled())
				swapToBlock();
			if (mc.currentScreen != null || mc.thePlayer.getHeldItem() == null)
				return;
		}
	}

	public void swapToBlock() {
		for (int slot = 0; slot <= 8; slot++) {
			ItemStack itemInSlot = mc.thePlayer.inventory.getStackInSlot(slot);
			if (itemInSlot != null && itemInSlot.getItem() instanceof ItemBlock && itemInSlot.stackSize > 0) {
				ItemBlock itemBlock = (ItemBlock) itemInSlot.getItem();
				Block block = itemBlock.getBlock();
				if (mc.thePlayer.inventory.currentItem != slot && block.isFullCube()) {
					mc.thePlayer.inventory.currentItem = slot;
				} else {
					return;
				}
				return;
			}
		}
	}

	private void setSneak(boolean sneak) {
		mc.gameSettings.keyBindSneak.pressed = sneak;
	}
}
