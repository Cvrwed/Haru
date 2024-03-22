package cc.unknown.module.impl.player;

import java.util.List;
import java.util.function.Predicate;

import com.google.common.collect.Lists;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.Cold;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.S2DPacketOpenWindow;

public class Stealer extends Module {

	private final SliderValue openDelay = new SliderValue("Open delay", 150, 0, 300, 25);
	private final SliderValue stealDelay = new SliderValue("Steal delay", 50, 0, 300, 25);
	private final BooleanValue autoClose = new BooleanValue("Auto close", true);
	private final SliderValue closeDelay = new SliderValue("Close delay", 0, 0, 300, 25);

	private Cold timer = new Cold();
	private List<Item> whiteListedItems = Lists.newArrayList(new Item[] { Items.golden_apple, Items.potionitem, Items.ender_pearl });

	public Stealer() {
		super("Stealer", ModuleCategory.Player);
		this.registerSetting(openDelay, stealDelay, autoClose, closeDelay);
	}

	@Override
	public void onEnable() {
		super.onEnable();
		timer.reset();
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

	@EventLink
	public void onTick(TickEvent e) {
		if (PlayerUtil.inGame() || mc.thePlayer.openContainer == null) {
			if (mc.thePlayer.openContainer instanceof ContainerChest) {

				int inventorySize = mc.thePlayer.inventory.getSizeInventory();
				int i = 0;

	            if (timer.elapsed(stealDelay.getInputToLong())) {
	                while (i < inventorySize) {
	                    if (isItemStealable(i)) {
	                        timer.reset();
	                        break;
	                    }
	                    i++;
	                }
	            }
				if (autoClose.isToggled() && i == inventorySize && timer.elapsed(closeDelay.getInputToLong(), true)) {
					mc.thePlayer.closeScreen();
				}
			}
		}
	}

	@EventLink
	public void onPacket(PacketEvent e) {
	    if (e.isReceive()) {
	        if (e.getPacket() instanceof S2DPacketOpenWindow) {
	            timer.reset();
	            timer.elapsed(openDelay.getInputToLong());
	        }
	    }
	}

	private boolean isItemStealable(int slotIndex) {
		Slot slot = mc.thePlayer.openContainer.getSlot(slotIndex);
		if (!slot.getHasStack())
			return false;

		ItemStack stack = slot.getStack();

		   Predicate<ItemStack> stealCondition = (itemStack) -> {
		        Item item = itemStack.getItem();
		        return (item instanceof ItemSword && (PlayerUtil.getBestSword() == null || PlayerUtil.isBetterSword(itemStack, PlayerUtil.getBestSword()))) || (item instanceof ItemAxe && (PlayerUtil.getBestAxe() == null || PlayerUtil.isBetterTool(itemStack, PlayerUtil.getBestAxe(), Blocks.planks))) || (item instanceof ItemPickaxe && (PlayerUtil.getBestAxe() == null || PlayerUtil.isBetterTool(itemStack, PlayerUtil.getBestAxe(), Blocks.stone))) || (item instanceof ItemBlock || item instanceof ItemArmor || whiteListedItems.contains(item));
		    };

		if (stealCondition.test(stack)) {
			clickSlot(slotIndex);
			return true;
		}

		return false;
	}

	private void clickSlot(int s) {
		mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, s, 0, 1, mc.thePlayer);
	}
}