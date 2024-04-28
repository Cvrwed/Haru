package cc.unknown.module.impl.combat;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.Potion;

@Register(name = "AutoRefill", category = Category.Combat)
public class AutoRefill extends Module {

	private DoubleSliderValue delay = new DoubleSliderValue("Delay", 0, 0, 0, 500, 1);
	private BooleanValue pots = new BooleanValue("Pots", true);
	private BooleanValue soup = new BooleanValue("Soup", true);

	private int lastShiftedPotIndex = -1;
	private long lastUsageTime = 0;
	private long delay1 = 800;
	private boolean refillOpened = false;

	public AutoRefill() {
		this.registerSetting(delay, pots, soup);
	}

	@Override
	public void onEnable() {
		if (PlayerUtil.inGame() && mc.currentScreen == null) {
			refillOpened = true;
			newDelay();
			openInventory();
			if (isHotbarFull()) {
				closeInventory();
			}
		}
	}

	@EventLink
	public void onPost(MotionEvent e) {
		if (e.isPost()) {
			long currentTime = System.currentTimeMillis();
			if (mc.currentScreen instanceof GuiInventory && !isHotbarFull()) {
				if (refillOpened && currentTime - lastUsageTime >= delay1) {
					refillHotbar();
					lastUsageTime = currentTime;
				}
			} else if (mc.currentScreen == null && this.isEnabled()) {
				this.disable();
			}
		}
	}

	private boolean isHotbarFull() {
		for (int i = 36; i < 45; i++) {
			if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				return false;
			}
		}
		return true;
	}

	private void openInventory() {
		mc.getNetHandler()
				.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
		mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
	}

	private void refillHotbar() {
		int nextPotIndex = findNextPotIndex();
		if (nextPotIndex != -1) {
			newDelay();
			shiftRightClickItem(nextPotIndex);
			lastShiftedPotIndex = nextPotIndex;
			if (isHotbarFull()) {
				closeInventory();
			}
		} else {
			closeInventory();
		}
	}

	private int findNextPotIndex() {
		int inventorySize = mc.thePlayer.inventory.getSizeInventory();
		int startIndex = (lastShiftedPotIndex + 1 + 9) % inventorySize;

		for (int i = startIndex; i != startIndex - 1; i = (i + 1) % inventorySize) {
			int slotIndex = i % inventorySize;

			if (slotIndex < 9) {
				continue;
			}

			ItemStack stack = mc.thePlayer.inventory.getStackInSlot(slotIndex);

			if (isValidStack(stack)) {
				lastShiftedPotIndex = slotIndex;
				return slotIndex;
			}

			if (i == (startIndex - 1 + inventorySize) % inventorySize) {
				break;
			}
		}

		return -1;
	}

	private void newDelay() {
		delay1 = (long) (delay.getInputMin() + (Math.random() * (delay.getInputMax() - delay.getInputMin())));
	}

	private boolean isValidStack(ItemStack stack) {
		if (stack == null)
			return false;
		return (pots.isToggled() && isPot(stack)) || (soup.isToggled() && isSoup(stack));
	}

	private boolean isSoup(ItemStack stack) {
		return stack.getItem() == Items.mushroom_stew;
	}

	private boolean isPot(ItemStack stack) {
		return stack != null && stack.getItem() instanceof ItemPotion && ((ItemPotion) stack.getItem())
				.getEffects(stack).stream().anyMatch(effect -> effect.getPotionID() == Potion.heal.id);
	}

	private void shiftRightClickItem(int slotIndex) {
		mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slotIndex, 0, 1, mc.thePlayer);
		mc.playerController.updateController();
	}

	private void closeInventory() {
		mc.thePlayer.closeScreen();
		mc.playerController.sendPacketDropItem(mc.thePlayer.inventory.getItemStack());
		refillOpened = false;
		this.disable();
	}
}
