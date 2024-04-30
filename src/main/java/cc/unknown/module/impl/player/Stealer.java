package cc.unknown.module.impl.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
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

@Register(name = "Stealer", category = Category.Player)
public class Stealer extends Module {

	private final DoubleSliderValue openDelay = new DoubleSliderValue("Open Delay", 125, 150, 25, 1000, 25);
	private final DoubleSliderValue stealDelay = new DoubleSliderValue("Steal Delay", 125, 150, 25, 1000, 25);
	private final BooleanValue autoClose = new BooleanValue("Auto Close", true);
	private final DoubleSliderValue closeDelay = new DoubleSliderValue("Close Delay", 0, 0, 0, 1000, 1);

	private final AtomicReference<ArrayList<Slot>> sortedSlots = new AtomicReference<>();
	private final AtomicReference<ContainerChest> chest = new AtomicReference<>();
	private final AtomicBoolean inChest = new AtomicBoolean(false);
	private final Cold delayTimer = new Cold(0);
	private final Cold closeTimer = new Cold(0);
	private final List<Item> whiteListedItems = Arrays.asList(Items.milk_bucket, Items.golden_apple, Items.potionitem,
			Items.ender_pearl, Items.water_bucket, Items.arrow, Items.bow);

	public Stealer() {
		this.registerSetting(openDelay, stealDelay, autoClose, closeDelay);
	}
	
	@EventLink
	public void onGui(ClickGuiEvent e) {
		this.setSuffix("- [" + openDelay.getInputMinToInt() + ", " + openDelay.getInputMaxToInt() + " ms]");
	}

	@EventLink
	public void onPre(MotionEvent e) {
		if (e.isPre()) {
			if ((mc.currentScreen != null) && (mc.thePlayer.inventoryContainer != null)
					&& (mc.thePlayer.inventoryContainer instanceof ContainerPlayer)
					&& (mc.currentScreen instanceof GuiChest)) {
				if (!inChest.get()) {
					chest.set((ContainerChest) mc.thePlayer.openContainer);
					delayTimer.setCooldown((long) ThreadLocalRandom.current().nextDouble(openDelay.getInputMin(),
							openDelay.getInputMax() + 0.01));
					delayTimer.start();
					generatePath(chest.get());
					inChest.set(true);
				}

				if (inChest.get() && sortedSlots.get() != null && !sortedSlots.get().isEmpty()) {
					if (delayTimer.hasFinished()) {
						clickSlot(sortedSlots.get().get(0).s);
						delayTimer.setCooldown((long) ThreadLocalRandom.current().nextDouble(stealDelay.getInputMin(),
								stealDelay.getInputMax() + 0.01));
						delayTimer.start();
						sortedSlots.get().remove(0);
					}
				}

				if (sortedSlots.get() != null && sortedSlots.get().isEmpty() && autoClose.isToggled()) {
					if (closeTimer.firstFinish()) {
						mc.thePlayer.closeScreen();
						inChest.set(false);
					} else {
						closeTimer.setCooldown((long) ThreadLocalRandom.current().nextDouble(closeDelay.getInputMin(),
								closeDelay.getInputMax() + 0.01));
						closeTimer.start();
					}
				}
			} else {
				inChest.set(false);
			}
		}
	}

	private void generatePath(ContainerChest chest) {
		ArrayList<Slot> slots = IntStream.range(0, chest.getLowerChestInventory().getSizeInventory()).mapToObj(i -> {
			ItemStack itemStack = chest.getInventory().get(i);
			if (itemStack != null) {
				Predicate<ItemStack> stealCondition = (stack) -> {
					Item item = stack.getItem();
					return (item instanceof ItemSword && (PlayerUtil.getBestSword() == null
							|| PlayerUtil.isBetterSword(stack, PlayerUtil.getBestSword())))
							|| (item instanceof ItemAxe && (PlayerUtil.getBestAxe() == null
									|| PlayerUtil.isBetterTool(stack, PlayerUtil.getBestAxe(), Blocks.planks)))
							|| (item instanceof ItemPickaxe && (PlayerUtil.getBestAxe() == null
									|| PlayerUtil.isBetterTool(stack, PlayerUtil.getBestAxe(), Blocks.stone)))
							|| (item instanceof ItemBlock || item instanceof ItemArmor
									|| whiteListedItems.contains(item));
				};

				if (stealCondition.test(itemStack)) {
					return new Slot(i);
				}
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toCollection(ArrayList::new));

		Slot[] sorted = sort(slots.toArray(new Slot[0]));
		sortedSlots.set(new ArrayList<>(Arrays.asList(sorted)));
	}

	private Slot[] sort(Slot[] in) {
		if (in == null || in.length == 0) {
			return in;
		}
		Slot[] out = new Slot[in.length];
		Slot current = in[ThreadLocalRandom.current().nextInt(0, in.length)];
		for (int i = 0; i < in.length; i++) {
			final Slot currentSlot = current;
			if (i == in.length - 1) {
				out[in.length - 1] = Arrays.stream(in).filter(p -> !p.visited).findAny().orElse(null);
				break;
			}
			out[i] = current;
			current.visit();
			Slot next = Arrays.stream(in).filter(p -> !p.visited)
					.min(Comparator.comparingDouble(p -> p.getDistance(currentSlot))).orElse(null);
			current = next;
		}
		return out;
	}

	class Slot {
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