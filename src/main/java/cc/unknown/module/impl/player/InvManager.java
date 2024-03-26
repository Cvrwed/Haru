package cc.unknown.module.impl.player;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.Cold;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockTNT;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAnvilBlock;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;

public class InvManager extends Module {
	private final SliderValue Mdelay = new SliderValue("Delay", 150, 0, 300, 25);
	private final BooleanValue openInv = new BooleanValue("Open inv", true);
	private final BooleanValue dropTrash = new BooleanValue("Drop trash", true);
	private final BooleanValue autoArmor = new BooleanValue("Auto armor", true);

	private final int INVENTORY_ROWS = 4, INVENTORY_COLUMNS = 9, ARMOR_SLOTS = 4;
	private final int INVENTORY_SLOTS = INVENTORY_ROWS * INVENTORY_COLUMNS + ARMOR_SLOTS;

	private final PlayerControllerMP playerController;

	private final Cold timer = new Cold();
	private final AtomicBoolean movedItem = new AtomicBoolean(false);
	private final AtomicBoolean inventoryOpen = new AtomicBoolean(false);

	public InvManager() {
		super("InvManager", ModuleCategory.Player);
		this.registerSetting(Mdelay, openInv, dropTrash, autoArmor);
		this.playerController = mc.playerController;
	}

	@Override
	public void onEnable() {
		timer.reset();
	}

	@Override
	public void onDisable() {
		closeInventory();
	}

	@EventLink
	public void onPre(final PreUpdateEvent e) {
		if (!timer.reached(Mdelay.getInputToLong())) {
			closeInventory();
			return;
		}

		if (mc.currentScreen instanceof GuiChest)
			return;

		if (mc.gameSettings.keyBindJump.isKeyDown() || mc.gameSettings.keyBindForward.isKeyDown()
				|| mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown()
				|| mc.gameSettings.keyBindRight.isKeyDown())
			return;

		movedItem.set(false);
		timer.reset();
		timer.reached(Mdelay.getInputToLong());

		if (!(mc.currentScreen instanceof GuiInventory) && openInv.isToggled())
			return;

		if (dropTrash.isToggled()) {
			for (int i = 0; i < INVENTORY_SLOTS; ++i) {
				final ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);

				if (itemStack == null || itemStack.getItem() == null)
					continue;

				if (!itemWhitelisted(itemStack)) {
					throwItem(getSlotId(i));
				}
			}
		}

		AtomicInteger bestHelmet = new AtomicInteger();
		AtomicInteger bestChestPlate = new AtomicInteger();
		AtomicInteger bestLeggings = new AtomicInteger();
		AtomicInteger bestBoots = new AtomicInteger();
		AtomicInteger bestSword = new AtomicInteger();
		AtomicInteger bestPickaxe = new AtomicInteger();
		AtomicInteger bestAxe = new AtomicInteger();
		AtomicInteger bestBlock = new AtomicInteger();
		AtomicInteger bestPotion = new AtomicInteger();
		AtomicInteger bestGaps = new AtomicInteger();

		for (int i = 0; i < INVENTORY_SLOTS; ++i) {
			final ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);

			if (itemStack == null || itemStack.getItem() == null)
				continue;

			final Item item = itemStack.getItem();

			if (item instanceof ItemArmor) {
				final ItemArmor armor = (ItemArmor) item;
				final int damageReductionItem = getArmorDamageReduction(itemStack);

				if (armor.armorType == 0) {
					if (bestHelmet.get() == 0 || damageReductionItem > getArmorDamageReduction(
							mc.thePlayer.inventory.getStackInSlot(bestHelmet.get()))) {
						bestHelmet.set(i);
					}
				}

				if (armor.armorType == 1) {
					if (bestChestPlate.get() == 0 || damageReductionItem > getArmorDamageReduction(
							mc.thePlayer.inventory.getStackInSlot(bestChestPlate.get()))) {
						bestChestPlate.set(i);
					}
				}

				if (armor.armorType == 2) {
					if (bestLeggings.get() == 0 || damageReductionItem > getArmorDamageReduction(
							mc.thePlayer.inventory.getStackInSlot(bestLeggings.get()))) {
						bestLeggings.set(i);
					}
				}

				if (armor.armorType == 3) {
					if (bestBoots.get() == 0 || damageReductionItem > getArmorDamageReduction(
							mc.thePlayer.inventory.getStackInSlot(bestBoots.get()))) {
						bestBoots.set(i);
					}
				}

			}

			if (item instanceof ItemSword) {
				final float damage = getSwordDamage(itemStack);
				if (bestSword.get() == 0
						|| damage > getSwordDamage(mc.thePlayer.inventory.getStackInSlot(bestSword.get()))) {
					bestSword.set(i);
				}
			}

			if (item instanceof ItemPickaxe) {
				final float mineSpeed = getMineSpeed(itemStack);
				if (bestPickaxe.get() == 0
						|| mineSpeed > getMineSpeed(mc.thePlayer.inventory.getStackInSlot(bestPickaxe.get()))) {
					bestPickaxe.set(i);
				}
			}

			if (item instanceof ItemAxe) {
				final float mineSpeed = getMineSpeed(itemStack);
				if (bestAxe.get() == 0
						|| mineSpeed > getMineSpeed(mc.thePlayer.inventory.getStackInSlot(bestAxe.get()))) {
					bestAxe.set(i);
				}
			}

			if (item instanceof ItemBlock && ((ItemBlock) item).getBlock().isFullCube()) {
				final float amountOfBlocks = itemStack.stackSize;
				if (bestBlock.get() == 0
						|| amountOfBlocks > mc.thePlayer.inventory.getStackInSlot(bestBlock.get()).stackSize) {
					bestBlock.set(i);
				}
			}

			if (item instanceof ItemAppleGold) {
				final float x = itemStack.stackSize;
				if (bestGaps.get() == 0 || x > mc.thePlayer.inventory.getStackInSlot(bestGaps.get()).stackSize) {
					bestGaps.set(i);
				}
			}

			if (item instanceof ItemPotion) {
				final ItemPotion itemPotion = (ItemPotion) item;
				if (bestPotion.get() == 0 && ItemPotion.isSplash(itemStack.getMetadata())
						&& itemPotion.getEffects(itemStack.getMetadata()) != null) {
					final int potionID = itemPotion.getEffects(itemStack.getMetadata()).get(0).getPotionID();
					boolean isPotionActive = false;

					for (final PotionEffect potion : mc.thePlayer.getActivePotionEffects()) {
						if (potion.getPotionID() == potionID && potion.getDuration() > 0) {
							isPotionActive = true;
							break;
						}
					}

					final ArrayList<Integer> whitelistedPotions = new ArrayList<Integer>() {
						{
							add(1);
							add(5);
							add(8);
							add(14);
							add(12);
							add(16);
						}
					};

					if (!isPotionActive && (whitelistedPotions.contains(potionID) || (potionID == 10 || potionID == 6)))
						bestPotion.set(i);
				}
			}
		}

		if (dropTrash.isToggled()) {
			for (int i = 0; i < INVENTORY_SLOTS; ++i) {
				final ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);

				if (itemStack == null || itemStack.getItem() == null)
					continue;

				final Item item = itemStack.getItem();

				if (item instanceof ItemArmor) {
					final ItemArmor armor = (ItemArmor) item;

					if ((armor.armorType == 0 && bestHelmet.get() != 0 && i != bestHelmet.get())
							|| (armor.armorType == 1 && bestChestPlate.get() != 0 && i != bestChestPlate.get())
							|| (armor.armorType == 2 && bestLeggings.get() != 0 && i != bestLeggings.get())
							|| (armor.armorType == 3 && bestBoots.get() != 0 && i != bestBoots.get())) {
						throwItem(getSlotId(i));
					}

				}

				if (item instanceof ItemSword) {
					if (bestSword.get() != 0 && i != bestSword.get()) {
						throwItem(getSlotId(i));
					}
				}

				if (item instanceof ItemPickaxe) {
					if (bestPickaxe.get() != 0 && i != bestPickaxe.get()) {
						throwItem(getSlotId(i));
					}
				}

				if (item instanceof ItemAxe) {
					if (bestAxe.get() != 0 && i != bestAxe.get()) {
						throwItem(getSlotId(i));
					}
				}
			}
		}

		if (autoArmor.isToggled()) {

			if (bestHelmet.get() != 0)
				equipArmor(getSlotId(bestHelmet.get()));

			if (bestChestPlate.get() != 0)
				equipArmor(getSlotId(bestChestPlate.get()));

			if (bestLeggings.get() != 0)
				equipArmor(getSlotId(bestLeggings.get()));

			if (bestBoots.get() != 0)
				equipArmor(getSlotId(bestBoots.get()));

		}
	}

	private float getSwordDamage(final ItemStack itemStack) {
		final ItemSword sword = (ItemSword) itemStack.getItem();
		final int efficiencyLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack);
		return (float) (sword.getDamageVsEntity() + efficiencyLevel * 1.25);
	}

	private int getArmorDamageReduction(final ItemStack itemStack) {
		return ((ItemArmor) itemStack.getItem()).damageReduceAmount
				+ EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[] { itemStack }, DamageSource.generic);
	}

	private void openInventory() {
		if (inventoryOpen.compareAndSet(false, true)) {
			mc.thePlayer.sendQueue.addToSendQueue(
					new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
		}
	}

	private void closeInventory() {
		if (inventoryOpen.compareAndSet(true, false)) {
			mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(0));
		}
	}

	private void throwItem(final int slot) {
		try {
			if (!movedItem.get()) {
				openInventory();
				playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer);
				movedItem.set(true);
			}
		} catch (final IndexOutOfBoundsException ignored) {
		}
	}

	private void equipArmor(final int slot) {
		try {
			if (slot > 8 && !movedItem.get()) {
				openInventory();
				playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, mc.thePlayer);
				movedItem.set(true);
			}
		} catch (final IndexOutOfBoundsException ignored) {
		}
	}

	private int getSlotId(final int slot) {
		if (slot >= 36)
			return 8 - (slot - 36);
		if (slot < 9)
			return slot + 36;
		return slot;
	}

	private boolean itemWhitelisted(final ItemStack itemStack) {
		final ArrayList<Item> whitelistedItems = new ArrayList<Item>() {
			{
				add(Items.ender_pearl);
				add(Items.bow);
				add(Items.arrow);
			}
		};

		final Item item = itemStack.getItem();
		final String itemName = itemStack.getDisplayName();

		if (itemName.contains("Click to Use"))
			return true;

		final ArrayList<Integer> whitelistedPotions = new ArrayList<Integer>() {
			{
				add(6);
				add(1);
				add(5);
				add(8);
				add(14);
				add(12);
				add(10);
				add(16);
			}
		};

		if (item instanceof ItemPotion) {
			final int potionID = getPotionId(itemStack);
			return whitelistedPotions.contains(potionID);
		}

		return (item instanceof ItemBlock && !(((ItemBlock) item).getBlock() instanceof BlockTNT)
				&& !(((ItemBlock) item).getBlock() instanceof BlockChest)
				&& !(((ItemBlock) item).getBlock() instanceof BlockFalling)) || item instanceof ItemAnvilBlock
				|| item instanceof ItemSword || item instanceof ItemArmor || item instanceof ItemTool
				|| item instanceof ItemFood || whitelistedItems.contains(item) && !item.equals(Items.spider_eye);
	}

	private int getPotionId(final ItemStack potion) {
		final Item item = potion.getItem();

		try {
			if (item instanceof ItemPotion) {
				final ItemPotion p = (ItemPotion) item;
				return p.getEffects(potion.getMetadata()).get(0).getPotionID();
			}
		} catch (final NullPointerException ignored) {
		}

		return 0;
	}

	private float getMineSpeed(final ItemStack itemStack) {
		final Item item = itemStack.getItem();
		int efficiencyLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack);

		switch (efficiencyLevel) {
		case 1:
			efficiencyLevel = 30;
			break;
		case 2:
			efficiencyLevel = 69;
			break;
		case 3:
			efficiencyLevel = 120;
			break;
		case 4:
			efficiencyLevel = 186;
			break;
		case 5:
			efficiencyLevel = 271;
			break;

		default:
			efficiencyLevel = 0;
			break;
		}

		if (item instanceof ItemPickaxe || item instanceof ItemAxe) {
			return getToolEfficiency(item) + efficiencyLevel;
		}
		return 0;
	}

	private float getToolEfficiency(Item item) {
		if (item instanceof ItemPickaxe) {
			return ((ItemPickaxe) item).getToolMaterial().getEfficiencyOnProperMaterial();
		} else if (item instanceof ItemAxe) {
			return ((ItemAxe) item).getToolMaterial().getEfficiencyOnProperMaterial();
		}
		return 0;
	}

}