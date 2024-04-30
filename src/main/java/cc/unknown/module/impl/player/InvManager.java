package cc.unknown.module.impl.player;

import java.util.ArrayList;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
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
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
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

@Register(name = "InvManager", category = Category.Player)
public class InvManager extends Module {
	private final SliderValue delay = new SliderValue("Delay", 150, 0, 300, 25);
	private final BooleanValue openInv = new BooleanValue("Open Inventory", true);
	private final BooleanValue dropTrash = new BooleanValue("Drop Trash", true);
	private final BooleanValue autoArmor = new BooleanValue("Auto Armor", true);
	private final BooleanValue noMove = new BooleanValue("Disable Movement", true);

	private final int INVENTORY_ROWS = 4, INVENTORY_COLUMNS = 9, ARMOR_SLOTS = 4;
	private final int INVENTORY_SLOTS = INVENTORY_ROWS * INVENTORY_COLUMNS + ARMOR_SLOTS;

	private PlayerControllerMP playerController;

	private final Cold timer = new Cold(0);
	private boolean movedItem;
	private boolean inventoryOpen;

	public InvManager() {
		this.registerSetting(delay, openInv, dropTrash, autoArmor, noMove);
	}
	
	@EventLink
	public void onGui(ClickGuiEvent e) {
		this.setSuffix("- [" + delay.getInputToInt() + " ms]");
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
	public void onPre(MotionEvent e) {
		if (e.isPre()) {
			if (!timer.reached(delay.getInputToLong())) {
				closeInventory();
				return;
			}

			if (mc.currentScreen instanceof GuiChest)
				return;

			if ((mc.gameSettings.keyBindJump.isKeyDown() || mc.gameSettings.keyBindForward.isKeyDown()
					|| mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown()
					|| mc.gameSettings.keyBindRight.isKeyDown()) && noMove.isToggled())
				return;

			movedItem = false;
			timer.reset();
			timer.reached(delay.getInputToLong());

			if (!(mc.currentScreen instanceof GuiInventory) && openInv.isToggled())
				return;

			playerController = mc.playerController;

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

			Integer bestHelmet = null;
			Integer bestChestPlate = null;
			Integer bestLeggings = null;
			Integer bestBoots = null;
			Integer bestSword = null;
			Integer bestPickaxe = null;
			Integer bestAxe = null;
			Integer bestBlock = null;
			Integer bestBow = null;
			Integer bestPotion = null;

			for (int i = 0; i < INVENTORY_SLOTS; ++i) {
				final ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);

				if (itemStack == null || itemStack.getItem() == null)
					continue;

				final Item item = itemStack.getItem();

				if (item instanceof ItemArmor) {
					final ItemArmor armor = (ItemArmor) item;
					final int damageReductionItem = getArmorDamageReduction(itemStack);

					if (armor.armorType == 0) {
						if (bestHelmet == null || damageReductionItem > getArmorDamageReduction(
								mc.thePlayer.inventory.getStackInSlot(bestHelmet))) {
							bestHelmet = i;
						}
					}

					if (armor.armorType == 1) {
						if (bestChestPlate == null || damageReductionItem > getArmorDamageReduction(
								mc.thePlayer.inventory.getStackInSlot(bestChestPlate))) {
							bestChestPlate = i;
						}
					}

					if (armor.armorType == 2) {
						if (bestLeggings == null || damageReductionItem > getArmorDamageReduction(
								mc.thePlayer.inventory.getStackInSlot(bestLeggings))) {
							bestLeggings = i;
						}
					}

					if (armor.armorType == 3) {
						if (bestBoots == null || damageReductionItem > getArmorDamageReduction(
								mc.thePlayer.inventory.getStackInSlot(bestBoots))) {
							bestBoots = i;
						}
					}

				}

				if (item instanceof ItemSword) {
					final float damage = getSwordDamage(itemStack);
					if (bestSword == null
							|| damage > getSwordDamage(mc.thePlayer.inventory.getStackInSlot(bestSword))) {
						bestSword = i;
					}
				}

				if (item instanceof ItemPickaxe) {
					final float mineSpeed = getMineSpeed(itemStack);
					if (bestPickaxe == null
							|| mineSpeed > getMineSpeed(mc.thePlayer.inventory.getStackInSlot(bestPickaxe))) {
						bestPickaxe = i;
					}
				}

				if (item instanceof ItemAxe) {
					final float mineSpeed = getMineSpeed(itemStack);
					if (bestAxe == null || mineSpeed > getMineSpeed(mc.thePlayer.inventory.getStackInSlot(bestAxe))) {
						bestAxe = i;
					}
				}

				if (item instanceof ItemBlock && ((ItemBlock) item).getBlock().isFullCube()) {
					final float amountOfBlocks = itemStack.stackSize;
					if (bestBlock == null
							|| amountOfBlocks > mc.thePlayer.inventory.getStackInSlot(bestBlock).stackSize) {
						bestBlock = i;
					}
				}

				if (item instanceof ItemBow) {
					final int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, itemStack);
					if (bestBow == null || level > 1) {
						bestBow = i;
					}
				}

				if (item instanceof ItemPotion) {
					final ItemPotion itemPotion = (ItemPotion) item;
					if (bestPotion == null && ItemPotion.isSplash(itemStack.getMetadata())
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

						if (!isPotionActive
								&& (whitelistedPotions.contains(potionID) || (potionID == 10 || potionID == 6)))
							bestPotion = i;
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

						if ((armor.armorType == 0 && bestHelmet != null && i != bestHelmet)
								|| (armor.armorType == 1 && bestChestPlate != null && i != bestChestPlate)
								|| (armor.armorType == 2 && bestLeggings != null && i != bestLeggings)
								|| (armor.armorType == 3 && bestBoots != null && i != bestBoots)) {
							throwItem(getSlotId(i));
						}
					}

					if (item instanceof ItemSword) {
						if (bestSword != null && i != bestSword) {
							throwItem(getSlotId(i));
						}
					}

					if (item instanceof ItemPickaxe) {
						if (bestPickaxe != null && i != bestPickaxe) {
							throwItem(getSlotId(i));
						}
					}

					if (item instanceof ItemAxe) {
						if (bestAxe != null && i != bestAxe) {
							throwItem(getSlotId(i));
						}
					}

					if (item instanceof ItemBow) {
						if (bestBow != null && i != bestBow) {
							throwItem(getSlotId(i));
						}
					}
				}
			}

			if (autoArmor.isToggled()) {

				if (bestHelmet != null)
					equipArmor(getSlotId(bestHelmet));

				if (bestChestPlate != null)
					equipArmor(getSlotId(bestChestPlate));

				if (bestLeggings != null)
					equipArmor(getSlotId(bestLeggings));

				if (bestBoots != null)
					equipArmor(getSlotId(bestBoots));
			}
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
		if (!inventoryOpen) {
			inventoryOpen = true;
			mc.thePlayer.sendQueue.addToSendQueue(
					new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
		}
	}

	private void closeInventory() {
		if (inventoryOpen) {
			inventoryOpen = false;
			mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(0));
		}
	}

	private void throwItem(final int slot) {
		try {
			if (!movedItem) {
				openInventory();
				playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer);
				movedItem = true;
			}
		} catch (final IndexOutOfBoundsException ignored) {
		}
	}

	private void equipArmor(final int slot) {
		try {
			if (slot > 8 && !movedItem) {
				openInventory();
				playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, mc.thePlayer);
				movedItem = true;
			}
		} catch (final IndexOutOfBoundsException ignored) {
		}
	}

	public int getSlotId(final int slot) {
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
				add(Items.milk_bucket);
				add(Items.water_bucket);
			}
		};

		final Item item = itemStack.getItem();

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