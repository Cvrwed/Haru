package cc.unknown.module.impl.player;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.PostUpdateEvent;
import cc.unknown.event.impl.move.SafeWalkEvent;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.event.impl.player.TickEvent;
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
	public DoubleSliderValue pitchRange = new DoubleSliderValue("Pitch Range", 70, 85, 0, 90, 1);
	private BooleanValue onHold = new BooleanValue("On Shift Hold", false);
	public BooleanValue blocksOnly = new BooleanValue("Blocks Only", true);
	public BooleanValue backwards = new BooleanValue("Backwards Movement Only", true);
	private BooleanValue onlySafe = new BooleanValue("Safe Walk", false);
	public BooleanValue slotSwap = new BooleanValue("Switch Blocks", true);

	private boolean shouldBridge = false;
	private boolean isShifting = false;
	private int slot;
	private Cold shiftTimer = new Cold(0);

	public LegitScaffold() {
		this.registerSetting(shiftOnJump, shiftTime, pitchRange, onHold, blocksOnly, backwards, onlySafe, slotSwap);
	}
	
	@EventLink
	public void onGui(ClickGuiEvent e) {
	    this.setSuffix(shiftTime.getInputMin() + ", " + shiftTime.getInputMax()  + " ms");
	}

	@Override
	public void onEnable() {
		slot = mc.thePlayer.inventory.currentItem;
		isShifting = false;
	}

	@Override
	public void onDisable() {
		setSneak(false);
		if (PlayerUtil.playerOverAir()) {
			setSneak(false);
		}
		
		if (slotSwap.isToggled()) {
			mc.thePlayer.inventory.currentItem = slot;
			mc.playerController.updateController();
		}

		shouldBridge = false;
		isShifting = false;
	}
	
	@EventLink
	public void onPost(PostUpdateEvent e) {
		try {
			if (slotSwap.isToggled() && mc.thePlayer.inventory.currentItem != mc.thePlayer.inventoryContainer.getSlot(furry()).getSlotIndex()) {
				mc.thePlayer.inventory.currentItem = furry() - 36;
				mc.playerController.updateController();
			}
		} catch (Exception ignor) {
		}
	}
	
    @EventLink
    public void onSafe(SafeWalkEvent e) {    	
    	if (onlySafe.isToggled() && mc.thePlayer.onGround) {
    		e.setSaveWalk(true);
    	}
    }

	@EventLink
	public void onSuicide(TickEvent e) {
		if (!(mc.currentScreen == null) || !PlayerUtil.inGame())
			return;

		boolean x = shiftTime.getInputMax() > 0;

		if (mc.thePlayer.rotationPitch < pitchRange.getInputMin() || mc.thePlayer.rotationPitch > pitchRange.getInputMax()) {
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
			if ((mc.thePlayer.movementInput.moveForward > 0) && (mc.thePlayer.movementInput.moveStrafe == 0) || mc.thePlayer.movementInput.moveForward >= 0) {
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

	private int furry() {
		for (int i = 36; i < 44; i++) {
			ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
			if (itemStack != null && itemStack.getItem() instanceof ItemBlock && itemStack.stackSize > 0) {
				ItemBlock itemBlock = (ItemBlock) itemStack.getItem();
				Block block = itemBlock.getBlock();
				if (block.isFullCube()) {
					return i;
				}
			}
		}
		return -1;
	}
	
	private void setSneak(boolean sneak) {
		mc.gameSettings.keyBindSneak.pressed = sneak;
	}
}
