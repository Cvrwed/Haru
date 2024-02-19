package cc.unknown.module.impl.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cc.unknown.event.impl.api.EventLink;
import cc.unknown.event.impl.move.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.AdvancedTimer;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFlower;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.util.DamageSource;

@SuppressWarnings("all")
public class InvManager extends Module {

	private int bestSwordSlot, bestPickaxeSlot, bestBowSlot, bestBlockSlot, bestGapSlot;
	private int[] bestArmorDamageReducment, bestArmorSlot;

	private final List<Integer> allSwords = new ArrayList<>();
	private final List<Integer> allBows = new ArrayList<>();
	private final List<Integer> allPickaxes = new ArrayList<>();
	private final List<Integer>[] allArmors = new List[4];
	private final List<Integer> allBlocks = new ArrayList<>();
	private final List<Integer> trash = new ArrayList<>();

	private final AdvancedTimer delayTimer = new AdvancedTimer(0);
	private final AdvancedTimer startDelayTimer = new AdvancedTimer(0);

	public SliderValue minDelay = new SliderValue("Min Delay", 10, 0, 100, 5);
	public SliderValue maxDelay = new SliderValue("Max Delay", 20, 0, 100, 5);
	public SliderValue startDelay = new SliderValue("Start Delay", 20, 0, 100, 1);
	public SliderValue maxBlocks = new SliderValue("Maximum Block Stacks", 2, 0, 8, 1);
	public BooleanValue autoArmor = new BooleanValue("Auto Armor", true);
	public BooleanValue random = new BooleanValue("Random", true);
	public BooleanValue openInv = new BooleanValue("Open Inv", true);

	private boolean invOpen;

	public InvManager() {
		super("InvManager", ModuleCategory.Player);
		this.registerSetting(minDelay, maxDelay, startDelay, maxBlocks, autoArmor, random, openInv);
	}

	@EventLink
	public void onPre(final PreUpdateEvent event) {
		closeInvServerSide();

		if (mc.currentScreen instanceof GuiChest)
			return;

		if (mc.gameSettings.keyBindJump.isKeyDown() || mc.gameSettings.keyBindForward.isKeyDown()
				|| mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown()
				|| mc.gameSettings.keyBindRight.isKeyDown())
			return;

		searchForItems();
		searchForBestArmor();
		searchForTrash();

		for (final int slot : trash) {
			if (hasNoDelay())
				return;

			try {
				openInvServerSide();
				mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot < 9 ? slot + 36 : slot,
						1, 4, mc.thePlayer);

			} catch (final IndexOutOfBoundsException p) {
			}

			delayTimer.reset();
		}

		if (autoArmor.isToggled()) {

			for (int i = 0; i < 4; i++) {
				if (bestArmorSlot[i] != -1) {
					final int bestSlot = bestArmorSlot[i];
					final ItemStack oldArmor = mc.thePlayer.inventory.armorItemInSlot(i);

					if (hasNoDelay())
						return;

					if (oldArmor != null && oldArmor.getItem() != null) {
						openInvServerSide();
						mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 8 - i, 0, 4,
								mc.thePlayer);
						delayTimer.reset();
					}

					final int slot = bestSlot < 9 ? bestSlot + 36 : bestSlot;

					if (hasNoDelay())
						return;

					openInvServerSide();
					mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, mc.thePlayer);
					delayTimer.reset();
				}
			}
		}
	}

	private void openInvServerSide() {
		if (!invOpen && !openInv.isToggled()) {
			mc.thePlayer.sendQueue.addToSendQueue(
					new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.OPEN_INVENTORY));
			invOpen = true;
		}
	}

	private void closeInvServerSide() {
		if (invOpen && !openInv.isToggled()) {
			mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(0));
			invOpen = false;
		}
	}

	private boolean hasNoDelay() {
		return !startDelayTimer.reached((long) ((long) startDelay.getInput() * 10 + Math.random() * 2))
				|| !delayTimer.reached((long) (Math.random() * (maxDelay.getInput() * 10 - minDelay.getInput() * 10 + 1)
						+ minDelay.getInput() * 10))
				|| (!(mc.currentScreen instanceof GuiInventory)) && openInv.isToggled();
	}

	private void searchForTrash() {
		trash.clear();

		for (int i = 0; i < allArmors.length; i++) {
			final List<Integer> armorItem = allArmors[i];
			if (armorItem != null) {
				final int finalI = i;
				armorItem.stream().filter(slot -> slot != bestArmorSlot[finalI]).forEach(trash::add);
			}
		}

		allBows.stream().filter(slot -> slot != bestBowSlot).forEach(trash::add);
		allSwords.stream().filter(slot -> slot != bestSwordSlot).forEach(trash::add);
		allPickaxes.stream().filter(slot -> slot != bestPickaxeSlot).forEach(trash::add);

		int blockStacks = allBlocks.size();

		for (final int slot : allBlocks) {
			if (blockStacks <= Math.round(maxBlocks.getInput()))
				break;

			trash.add(slot);
			blockStacks -= 1;
		}

		if (random.isToggled())
			Collections.shuffle(trash);
		else
			Collections.sort(trash);
	}

	private void searchForBestArmor() {
		bestArmorDamageReducment = new int[4];
		bestArmorSlot = new int[4];

		Arrays.fill(bestArmorDamageReducment, -1);
		Arrays.fill(bestArmorSlot, -1);

		for (int i = 0; i < bestArmorSlot.length; i++) {
			final ItemStack itemStack = mc.thePlayer.inventory.armorItemInSlot(i);
			allArmors[i] = new ArrayList<>();

			if (itemStack != null && itemStack.getItem() != null) {
				if (itemStack.getItem() instanceof ItemArmor) {
					final ItemArmor armor = (ItemArmor) itemStack.getItem();
					final int currentProtection = armor.damageReduceAmount + EnchantmentHelper
							.getEnchantmentModifierDamage(new ItemStack[] { itemStack }, DamageSource.generic);
					bestArmorDamageReducment[i] = currentProtection;
				}
			}
		}

		for (int i = 0; i < 9 * 4; i++) {
			final ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
			if (itemStack == null || itemStack.getItem() == null)
				continue;

			if (itemStack.getItem() instanceof ItemArmor) {
				final ItemArmor armor = (ItemArmor) itemStack.getItem();

				final int armorType = 3 - armor.armorType;
				allArmors[armorType].add(i);
				final int slotProtectionLevel = armor.damageReduceAmount + EnchantmentHelper
						.getEnchantmentModifierDamage(new ItemStack[] { itemStack }, DamageSource.generic);

				if (bestArmorDamageReducment[armorType] < slotProtectionLevel) {
					bestArmorDamageReducment[armorType] = slotProtectionLevel;
					bestArmorSlot[armorType] = i;
				}
			}
		}
	}

	private void searchForItems() {
		bestSwordSlot = -1;
		bestBowSlot = -1;
		bestPickaxeSlot = -1;
		bestBlockSlot = -1;
		bestGapSlot = -1;

		allSwords.clear();
		allBows.clear();
		allPickaxes.clear();
		allBlocks.clear();

		float bestSwordDamage = -1, bestSwordDurability = -1, bestPickaxeEfficiency = -1, bestPickaxeDurability = -1,
				bestBowDurability = -1;
		int bestBowDamage = -1, bestBlockSize = -1;
		int gapStackSize = -1;

		for (int i = 0; i < 9 * 4; i++) {
			final ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
			if (itemStack == null || itemStack.getItem() == null)
				continue;

			if (itemStack.getItem() instanceof ItemSword) {
				final ItemSword sword = (ItemSword) itemStack.getItem();
				final int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack);
				final float damageLevel = (float) (sword.getDamageVsEntity() + level * 1.25); // Enchantment Multiplier

				allSwords.add(i);

				if (bestSwordDamage < damageLevel) {
					bestSwordDamage = damageLevel;
					bestSwordDurability = sword.getDamageVsEntity();
					bestSwordSlot = i;
				}

				if ((damageLevel == bestSwordDamage) && (sword.getDamageVsEntity() < bestSwordDurability)) {
					bestSwordDurability = sword.getDamageVsEntity();
					bestSwordSlot = i;
				}
			}

			if (itemStack.getItem() instanceof ItemPickaxe) {
				final ItemPickaxe pickaxe = (ItemPickaxe) itemStack.getItem();
				allPickaxes.add(i);

				final float efficiencyLevel = getPickaxeEfficiency(pickaxe);

				if (bestPickaxeEfficiency < efficiencyLevel) {
					bestPickaxeEfficiency = efficiencyLevel;
					bestPickaxeDurability = pickaxe.getMaxDamage();
					bestPickaxeSlot = i;
				}

				if ((efficiencyLevel == bestPickaxeEfficiency) && (pickaxe.getMaxDamage() < bestPickaxeDurability)) {
					bestPickaxeDurability = pickaxe.getMaxDamage();
					bestPickaxeSlot = i;
				}
			}

			if (itemStack.getItem() instanceof ItemBow) {
				final ItemBow bow = (ItemBow) itemStack.getItem();
				final int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, itemStack);
				allBows.add(i);

				if (bestBowDamage < level || (bestBowDamage == -1 && level == 0)) {
					bestBowDamage = level;
					bestBowDurability = bow.getMaxDamage();
					bestBowSlot = i;
				}

				if ((level == bestBowDamage) && bow.getMaxDamage() < bestBowDurability) {
					bestBowDurability = bow.getMaxDamage();
					bestBowSlot = i;
				}
			}

			if (itemStack.getItem() instanceof ItemBlock) {
				final ItemBlock block = (ItemBlock) itemStack.getItem();

				if (block.getBlock() == Blocks.web || block.getBlock() == Blocks.bed
						|| block.getBlock() == Blocks.noteblock || block.getBlock() == Blocks.cactus
						|| block.getBlock() == Blocks.cake || block.getBlock() == Blocks.anvil
						|| block.getBlock() == Blocks.skull || block.getBlock() instanceof BlockDoor
						|| block.getBlock() instanceof BlockFlower || block.getBlock() instanceof BlockCarpet)
					continue;

				allBlocks.add(i);

				if (bestBlockSize < itemStack.stackSize) {
					bestBlockSize = itemStack.stackSize;
					bestBlockSlot = i;
				}
			}

			if (itemStack.getItem() instanceof ItemAppleGold) {
				if (gapStackSize < itemStack.stackSize) {
					gapStackSize = itemStack.stackSize;
					bestGapSlot = i;
				}
			}
		}
	}

	private float getPickaxeEfficiency(final ItemPickaxe pickaxe) {
		int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, new ItemStack(pickaxe));

		switch (level) {
		case 1:
			level = 30;
			break;
		case 2:
			level = 69;
			break;
		case 3:
			level = 120;
			break;
		case 4:
			level = 186;
			break;
		case 5:
			level = 271;
			break;

		default:
			level = 0;
			break;
		}

		return pickaxe.getToolMaterial().getEfficiencyOnProperMaterial() + level;
	}
}