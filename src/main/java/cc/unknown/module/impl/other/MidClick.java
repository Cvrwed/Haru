package cc.unknown.module.impl.other;

import org.lwjgl.input.Mouse;

import cc.unknown.Haru;
import cc.unknown.event.impl.api.EventLink;
import cc.unknown.event.impl.move.UpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.impl.settings.Targets;
import cc.unknown.utils.player.FriendUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

public class MidClick extends Module {

	public boolean x = false;

	public MidClick() {
		super("Midclick", ModuleCategory.Other);
	}

    @EventLink
    public void onUpdate(UpdateEvent e) {
        if (mc.currentScreen != null) return;
        if (!x && Mouse.isButtonDown(2)) {
            Entity entity = mc.objectMouseOver.entityHit;
            if (entity instanceof EntityPlayer) {
                if (!Targets.isAFriend((EntityPlayer) entity)) {
                    FriendUtil.addFriend((EntityPlayer) entity);
                    if (Haru.instance.getClientConfig() != null) {
                    	Haru.instance.getClientConfig().saveConfig();
                    }
                    PlayerUtil.send(EnumChatFormatting.GRAY + entity.getName() + " was added to your friends.");
                } else {
                	FriendUtil.removeFriend((EntityPlayer) entity);
                    if (Haru.instance.getClientConfig() != null) {
                    	Haru.instance.getClientConfig().saveConfig();
                    }
                    PlayerUtil.send(EnumChatFormatting.GRAY + entity.getName() + " was removed from your friends.");
                }
            } else {
            	PlayerUtil.send(EnumChatFormatting.GRAY + "Error: You need to select a player.");
            }
        }
        x = Mouse.isButtonDown(2);
    }

}
