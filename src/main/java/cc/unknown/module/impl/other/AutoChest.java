package cc.unknown.module.impl.other;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.UpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

public class AutoChest extends Module {

	private BooleanValue iron = new BooleanValue("Iron check", false);
	private BooleanValue gold = new BooleanValue("Gold check", true);
	private BooleanValue dia = new BooleanValue("Diamond check", false);
	private BooleanValue eme = new BooleanValue("Emerald check", true);

	public AutoChest() {
		super("AutoChest", ModuleCategory.Other);
	}

	@EventLink
	public void onUpdate(UpdateEvent e) {
		if (PlayerUtil.inGame()) {

	        if (gold.isToggled() && searchAndPlaceItem(mc.thePlayer, Items.gold_ingot)) return;
	        if (iron.isToggled() && searchAndPlaceItem(mc.thePlayer, Items.iron_ingot)) return;
	        if (dia.isToggled() && searchAndPlaceItem(mc.thePlayer, Items.diamond)) return;
	        if (eme.isToggled() && searchAndPlaceItem(mc.thePlayer, Items.emerald)) return;
		}
	}

    private boolean searchAndPlaceItem(EntityPlayerSP player, Item item) {
        for (int i = 0; i < 9; i++) {
            ItemStack stackInSlot = player.inventory.getStackInSlot(i);
            if (stackInSlot != null && stackInSlot.getItem() == item) {
                placeItemInChest();
                return true;
            }
        }
        return false;
    }

    private void placeItemInChest() {
        BlockPos playerPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        BlockPos blockPos = playerPos.offset(mc.thePlayer.getHorizontalFacing());

        if (isChestBlock(blockPos)) {
            mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, 0, 0, 1, mc.thePlayer);
        }
    }

    private boolean isChestBlock(BlockPos blockPos) {
        Block block = mc.theWorld.getBlockState(blockPos).getBlock();
        return block == Blocks.chest || block == Blocks.ender_chest;
    }
}
