package cc.unknown.module.impl.other;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

import cc.unknown.Haru;
import cc.unknown.event.impl.api.EventLink;
import cc.unknown.event.impl.other.MouseEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.impl.settings.Targets;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.utils.player.FriendUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public class MidClick extends Module {

	private boolean x = false;
	private int prevSlot;
	private Robot bot;
	private int pearlEvent = 4;
	private ModeValue mode = new ModeValue("Mode", "Add/Remove friend", "Add/Remove friend", "Throw pearl");

	public MidClick() {
		super("Midclick", ModuleCategory.Other);
		this.registerSetting(mode);
	}

	@Override
	public void onEnable() {
		try {
			this.bot = new Robot();
		} catch (AWTException x) {
			this.disable();
		}
	}

	@EventLink
	public void onMouse(MouseEvent e) {
		if (mc.currentScreen != null)
			return;

		if (pearlEvent < 4) {
			if (pearlEvent == 3)
				mc.thePlayer.inventory.currentItem = prevSlot;
			pearlEvent++;
		}

		if (!x && e.getButton() == 2) {
			if (mode.is("Add/Remove friend") && mc.objectMouseOver.entityHit instanceof EntityPlayer) {
				if (!Targets.isAFriend((EntityPlayer) mc.objectMouseOver.entityHit)) {
					FriendUtil.addFriend((EntityPlayer) mc.objectMouseOver.entityHit);
					if (Haru.instance.getClientConfig() != null) {
						Haru.instance.getClientConfig().saveConfig();
					}
					PlayerUtil.send(EnumChatFormatting.GRAY + mc.objectMouseOver.entityHit.getName()
							+ " was added to your friends.");
				} else {
					FriendUtil.removeFriend((EntityPlayer) mc.objectMouseOver.entityHit);
					if (Haru.instance.getClientConfig() != null) {
						Haru.instance.getClientConfig().saveConfig();
					}
					PlayerUtil.send(EnumChatFormatting.GRAY + mc.objectMouseOver.entityHit.getName()
							+ " was removed from your friends.");
				}
			} else {
				PlayerUtil.send(EnumChatFormatting.GRAY + "Error: You need to select a player.");
			}

			if (mode.is("Throw pearl")) {
				for (int s = 0; s <= 8; s++) {
					ItemStack item = mc.thePlayer.inventory.getStackInSlot(s);
					if (item != null && item.getItem() instanceof ItemEnderPearl) {
						prevSlot = mc.thePlayer.inventory.currentItem;
						mc.thePlayer.inventory.currentItem = s;
						bot.mousePress(InputEvent.BUTTON3_MASK);
						bot.mouseRelease(InputEvent.BUTTON3_MASK);
						pearlEvent = 0;
						x = true;
						return;
					}
				}
			}
		}
		x = e.getButton() == 2;
	}

}
