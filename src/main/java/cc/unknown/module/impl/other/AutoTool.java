package cc.unknown.module.impl.other;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;

@Register(name = "AutoTool", category = Category.Other)
public class AutoTool extends Module {
	@SuppressWarnings("unused")
	private int prevItem = 0;
	private boolean mining = false;
	private int bestSlot = 0;

	@EventLink
	public void onRender(Render3DEvent e) {
		if (!mc.gameSettings.keyBindUseItem.isKeyDown() && mc.gameSettings.keyBindAttack.isKeyDown()
				&& mc.objectMouseOver != null
				&& mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {

			int bestSpeed = 0;
			bestSlot = -1;

			if (!mining) {
				prevItem = mc.thePlayer.inventory.currentItem;
			}

			Block block = mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock();

			for (int i = 0; i <= 8; i++) {
				ItemStack item = mc.thePlayer.inventory.getStackInSlot(i);
				if (item == null)
					continue;

				float speed = item.getStrVsBlock(block);

				if (speed > bestSpeed) {
					bestSpeed = (int) speed;
					bestSlot = i;
				}

				if (bestSlot != -1) {
					mc.thePlayer.inventory.currentItem = bestSlot;
				}
			}
			mining = true;
		} else {
			if (mining) {
				mining = false;
			} else {
				prevItem = mc.thePlayer.inventory.currentItem;
			}
		}
	}
}