package cc.unknown.module.impl.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

import com.google.common.collect.Lists;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.utils.client.Cold;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class Stealer extends Module {

	private final DoubleSliderValue openDelay = new DoubleSliderValue("Open delay", 250, 450, 25, 1000, 25);
	private final DoubleSliderValue stealDelay = new DoubleSliderValue("Steal delay", 150, 250, 25, 1000, 25);
	private final BooleanValue autoClose = new BooleanValue("Auto close", true);
	private final DoubleSliderValue closeDelay = new DoubleSliderValue("Close delay", 0, 0, 0, 1000, 1);
	
    private ArrayList<Slot> sortedSlots;
    private ContainerChest chest;
    private boolean inChest;
    private final Cold delayTimer = new Cold();
    private final Cold closeTimer = new Cold();
	private List<Item> whiteListedItems = Lists.newArrayList(new Item[] { Items.milk_bucket, Items.golden_apple, Items.potionitem, Items.ender_pearl });

	public Stealer() {
		super("Stealer", ModuleCategory.Player);
		this.registerSetting(openDelay, stealDelay, autoClose, closeDelay);
	}
	
    @EventLink
    public void onRender2D(Render2DEvent e) {
        if ((mc.currentScreen != null) && (mc.thePlayer.inventoryContainer != null) && (mc.thePlayer.inventoryContainer instanceof ContainerPlayer) && (mc.currentScreen instanceof GuiChest)) {
            if (!inChest) {
                chest = (ContainerChest) mc.thePlayer.openContainer;
                delayTimer.setCooldown((long) ThreadLocalRandom.current().nextDouble(openDelay.getInputMin(), openDelay.getInputMax() + 0.01));
                delayTimer.start();
                generatePath(chest);
                inChest = true;
            }
            
            if (inChest && !sortedSlots.isEmpty()) {
                if (delayTimer.hasFinished()) {
                	clickSlot(sortedSlots.get(0).s);
                    delayTimer.setCooldown((long) ThreadLocalRandom.current().nextDouble(stealDelay.getInputMin(), stealDelay.getInputMax() + 0.01));
                    delayTimer.start();
                    sortedSlots.remove(0);
                }
            }
            
            if (sortedSlots.isEmpty() && autoClose.isToggled()) {
                if (closeTimer.firstFinish()) {
                    mc.thePlayer.closeScreen();
                    inChest = false;
                } else {
                    closeTimer.setCooldown((long) ThreadLocalRandom.current().nextDouble(closeDelay.getInputMin(), closeDelay.getInputMax() + 0.01));
                    closeTimer.start();
                }
            }
        } else {
            inChest = false;
        }
    }

    private void generatePath(ContainerChest chest) {
        ArrayList<Slot> slots = new ArrayList<Slot>();
        for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
            ItemStack itemStack = chest.getInventory().get(i);
            if (itemStack != null) {
                Predicate<ItemStack> stealCondition = (stack) -> {
                    Item item = stack.getItem();
                    return (item instanceof ItemSword && (PlayerUtil.getBestSword() == null || PlayerUtil.isBetterSword(stack, PlayerUtil.getBestSword()))) || (item instanceof ItemAxe && (PlayerUtil.getBestAxe() == null || PlayerUtil.isBetterTool(stack, PlayerUtil.getBestAxe(), Blocks.planks))) || (item instanceof ItemPickaxe && (PlayerUtil.getBestAxe() == null || PlayerUtil.isBetterTool(stack, PlayerUtil.getBestAxe(), Blocks.stone))) || (item instanceof ItemBlock || item instanceof ItemArmor || whiteListedItems.contains(item));
                };
                
                if (stealCondition.test(itemStack)) {
                    slots.add(new Slot(i));
                }
            }
        }

        Slot[] sortedSlots = sort(slots.toArray(new Slot[slots.size()]));
        this.sortedSlots = new ArrayList<Slot>(Arrays.asList(sortedSlots));
    }

    private Slot[] sort(Slot[] in) {
        if (in == null || in.length == 0) {
            return in;
        }
        Slot[] out = new Slot[in.length];
        Slot current = in[ThreadLocalRandom.current().nextInt(0, in.length)];
        for (int i = 0; i < in.length; i++) {
            if (i == in.length - 1) {
                out[in.length - 1] = Arrays.stream(in).filter(p -> !p.visited).findAny().orElseGet(null);
                break;
            }
            Slot finalCurrent = current;
            out[i] = finalCurrent;
            finalCurrent.visit();
            Slot next = Arrays.stream(in).filter(p -> !p.visited).min(Comparator.comparingDouble(p -> p.getDistance(finalCurrent))).get();
            current = next;
        }
        return out;
    }

    final class Slot {
        final int x;
        final int y;
        final int s;
        boolean visited;

        Slot(int s) {
            this.x = (s + 1) % 10;
            this.y = s / 9;
            this.s = s;
        }

        public double getDistance(Slot s) {
            return Math.abs(this.x - s.x) + Math.abs(this.y - s.y);
        }

        public void visit() {
            visited = true;
        }
    }
	
	private void clickSlot(int x) {
        mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, x, 0, 1, mc.thePlayer);
	}
}