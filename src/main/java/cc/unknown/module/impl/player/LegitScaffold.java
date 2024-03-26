package cc.unknown.module.impl.player;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.SafeWalkEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.utils.helpers.MathHelper;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.item.ItemAnvilBlock;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldSettings;

public class LegitScaffold extends Module {
	public BooleanValue shiftOnJump = new BooleanValue("Shift on air", false);
	public DoubleSliderValue shiftTime = new DoubleSliderValue("Shift time", 140, 200, 0, 280, 5);
	public DoubleSliderValue pitchRange = new DoubleSliderValue("Pitch Range", 70, 85, 0, 90, 1);
	private BooleanValue onHold = new BooleanValue("On shift hold", false);
	public BooleanValue blocksOnly = new BooleanValue("Blocks only", true);
	public BooleanValue backwards = new BooleanValue("Only backwards", true);
	private BooleanValue onlySafe = new BooleanValue("Safewalk", false);
	public BooleanValue slotSwap = new BooleanValue("Switch blocks", true);

    private final AtomicBoolean shouldBridge = new AtomicBoolean(false);
    private final AtomicBoolean isShifting = new AtomicBoolean(false);
    private final AtomicLong shiftTimer = new AtomicLong();
    
	public LegitScaffold() {
		super("LegitScaffold", ModuleCategory.Player);
		this.registerSetting(shiftOnJump, shiftTime, pitchRange, onHold, blocksOnly, backwards, onlySafe, slotSwap);
	}

    @Override
    public void onEnable() {
        isShifting.set(false);
    }

    @Override
    public void onDisable() {
        setSneak(false);
        shouldBridge.set(false);
        isShifting.set(false);
    }

    @EventLink
    public void onSafe(SafeWalkEvent e) {
        if (onlySafe.isToggled() && mc.thePlayer.onGround) {
            e.setSaveWalk(true);
        }
    }

    @EventLink
    public void onSuicide(TickEvent e) {
        if (PlayerUtil.inGame() || mc.currentScreen != null) return;

        boolean hasShiftTime = shiftTime.getInputMax() > 0;

        if (mc.thePlayer.rotationPitch < pitchRange.getInputMin() || mc.thePlayer.rotationPitch > pitchRange.getInputMax()) {
            shouldBridge.set(false);
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
                setSneak(true);
            }
            return;
        }

        if (onHold.isToggled() && !Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
            shouldBridge.set(false);
            return;
        }
        
        if (mc.playerController.getCurrentGameType() == WorldSettings.GameType.SPECTATOR || mc.thePlayer.isDead) {
            return;
        }
        
        if (blocksOnly.isToggled()) {
            ItemStack itemStack = mc.thePlayer.getHeldItem();
            if (itemStack == null || !(itemStack.getItem() instanceof ItemBlock)) {
                if (isShifting.get()) {
                    isShifting.set(false);
                    setSneak(false);
                }
                return;
            }
        }

        if (backwards.isToggled() && (mc.thePlayer.movementInput.moveForward > 0 || mc.thePlayer.movementInput.moveForward >= 0)) {
            shouldBridge.set(false);
            return;
        }

        if (mc.thePlayer.onGround) {
            if (PlayerUtil.playerOverAir()) {
                if (hasShiftTime) {
                    shiftTimer.set(MathHelper.randomInt(shiftTime.getInputMin(), shiftTime.getInputMax() + 0.1));
                }

                isShifting.set(true);
                setSneak(true);
                shouldBridge.set(true);
            } else if (mc.thePlayer.isSneaking() && !Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && onHold.isToggled()) {
                isShifting.set(false);
                shouldBridge.set(false);
                setSneak(false);
            } else if (onHold.isToggled() && !Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
                isShifting.set(false);
                shouldBridge.set(false);
                setSneak(false);
            } else if (mc.thePlayer.isSneaking() && (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && onHold.isToggled()) && (!hasShiftTime || shiftTimer.get() == 0)) {
                isShifting.set(false);
                setSneak(false);
                shouldBridge.set(true);
            } else if (mc.thePlayer.isSneaking() && !onHold.isToggled() && (!hasShiftTime || shiftTimer.get() == 0)) {
                isShifting.set(false);
                setSneak(false);
                shouldBridge.set(true);
            }
        } else if (shouldBridge.get() && mc.thePlayer.capabilities.isFlying) {
            setSneak(false);
            shouldBridge.set(false);
        } else if (shouldBridge.get() && PlayerUtil.playerOverAir() && shiftOnJump.isToggled()) {
            isShifting.set(true);
            setSneak(true);
        } else {
            isShifting.set(false);
            setSneak(false);
        }
    }

    @EventLink
    public void onRender(Render3DEvent e) {
        if (PlayerUtil.inGame()) return;

        if (mc.thePlayer.getHeldItem() == null || !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock)) {
            if (slotSwap.isToggled()) {
                swapToBlock();
            }
        }

        if (mc.currentScreen != null || mc.thePlayer.getHeldItem() == null) return;
    }

	protected void swapToBlock() {
		for (int i = 0; i < 9; ++i) {
			final ItemStack s = mc.thePlayer.inventory.getStackInSlot(i);
			if (s != null && s.getItem() instanceof ItemBlock) {
				final boolean b = s.getItem() instanceof ItemAnvilBlock;
				final String n = s.getDisplayName().toLowerCase();
				if (b || n.equals("sand") || n.equals("red sand") || n.equals("tnt") || n.equals("anvil")
						|| n.endsWith("slab") || n.startsWith("lilly") || n.startsWith("sapling")
						|| n.startsWith("chest") || n.contains("web")) {
					return;
				}
				mc.thePlayer.inventory.currentItem = i;
			}
		}
	}

	private void setSneak(boolean sneak) {
		mc.gameSettings.keyBindSneak.pressed = sneak;
	}
}
