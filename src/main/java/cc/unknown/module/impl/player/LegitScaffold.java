package cc.unknown.module.impl.player;

import java.util.function.BooleanSupplier;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.event.impl.move.MoveInputEvent;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.event.impl.render.RenderEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.Cold;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldSettings;

@Register(name = "LegitScaffold", category = Category.Player)
public class LegitScaffold extends Module {
	private SliderValue shiftTime = new SliderValue("Shift Time", 140, 5, 200, 5);
	private SliderValue shiftMutiplier = new SliderValue("Shift speed multiplier", 0.3, 0.2, 1, 0.05);
    private BooleanValue pitchCheck = new BooleanValue("Pitch Check", false);
	private DoubleSliderValue pitchRange = new DoubleSliderValue("Pitch Range", 70, 85, 0, 90, 1);
    private BooleanValue onlyGround = new BooleanValue("Only Ground", false);
    private BooleanValue holdShift = new BooleanValue("Hold Shift", false);
    private BooleanValue slotSwap = new BooleanValue("Block Switching", true);
    private BooleanValue blocksOnly = new BooleanValue("Blocks Only", true);
    private BooleanValue backwards = new BooleanValue("Backwards Movement Only", true);

    private boolean shouldBridge = false;
    private int ticks;
    private Cold shiftTimer = new Cold(0);

	public LegitScaffold() {
		this.registerSetting(shiftTime, shiftMutiplier, pitchCheck, pitchRange, onlyGround, holdShift, slotSwap, blocksOnly, backwards);
	}

	@EventLink
	public void onGui(ClickGuiEvent e) {
		this.setSuffix("- [" + shiftTime.getInputToInt() + " ms]");
	}
	
	@Override
	public void onDisable() {
		mc.gameSettings.keyBindSneak.pressed = false;
		if (PlayerUtil.playerOverAir()) {
			mc.gameSettings.keyBindSneak.pressed = false;
		}
	}

	@EventLink
	public void onMoveInput(MoveInputEvent e) {
        e.setSneak((shouldBridge && (mc.gameSettings.keyBindSneak.isKeyDown() || !holdShift.isToggled())) || (mc.gameSettings.keyBindSneak.isKeyDown() && !holdShift.isToggled()));

        if (shouldBridge && ticks <= 2) {
            e.setSneakMultiplier(shiftMutiplier.getInput());
        }
	}
	
	@EventLink
	public void onSuicide(MotionEvent e) {
        if (mc.currentScreen != null && !e.isPre()) return;
        if (mc.playerController.getCurrentGameType() == WorldSettings.GameType.SPECTATOR) return;
        
        if (PlayerUtil.playerOverAir() && (!onlyGround.isToggled() || mc.thePlayer.onGround) && mc.thePlayer.motionY < 0.1) {
        	shiftTimer.reset();
        }
        
        if (backwards.isToggled() && shouldBridgeCheck()) {
            shouldBridge = false;
            return;
        }
        
		if (pitchCheck.isToggled() && shouldPitchCheck()) {
			shouldBridge = false;
			if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
				shouldBridge = true;
			}
			return;
		}
        
        if (blocksOnly.isToggled() && shouldSkipBlockCheck()) {
        	return;
        }
        
		shouldBridge = !shiftTimer.reached((long) shiftTime.getInput());

        if (holdShift.isToggled()) {
            mc.gameSettings.keyBindSneak.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindSneak) && shouldBridge;
        } else {
            mc.gameSettings.keyBindSneak.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindSneak) || shouldBridge;
        }
        
        if (shouldBridge) {
            ticks++;
        } else {
            ticks = 0;
        }
	}
	
	@EventLink
	public void onRender(RenderEvent e) {
		if (!PlayerUtil.inGame()&& !e.is3D()) return;
		if (slotSwap.isToggled() && shouldSkipBlockCheck()) swapToBlock();
		if (mc.currentScreen != null || mc.thePlayer.getHeldItem() == null) return;
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
	
	private boolean shouldSkipBlockCheck() {
	    return ((BooleanSupplier) () -> {
	    	ItemStack heldItem = mc.thePlayer.getHeldItem();
	    	return heldItem == null || !(heldItem.getItem() instanceof ItemBlock);
	    }).getAsBoolean();
	}
	
	private boolean shouldPitchCheck() {
	    return ((BooleanSupplier) () -> {
	        boolean maxPitch = mc.thePlayer.rotationPitch > pitchRange.getInputMax();
	        boolean minPitch = mc.thePlayer.rotationPitch < pitchRange.getInputMin();
	        return (maxPitch || minPitch);
	    }).getAsBoolean();
	}
	
	private boolean shouldBridgeCheck() {
		return ((BooleanSupplier) () -> {
			double moveForward = mc.thePlayer.movementInput.moveForward;
			double moveStrafe = mc.thePlayer.movementInput.moveStrafe;
			return (moveForward > 0 && moveStrafe == 0) || moveForward >= 0;
		}).getAsBoolean();
	}
}
