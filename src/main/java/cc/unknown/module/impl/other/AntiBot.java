package cc.unknown.module.impl.other;

import java.util.ArrayList;

import cc.unknown.Haru;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.UpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class AntiBot extends Module {
	private final ModeValue mode = new ModeValue("Mode", "Advanced", "Advanced", "Matrix");
	private static ArrayList<Entity> bots = new ArrayList<>();

	public AntiBot() {
		super("AntiBot", ModuleCategory.Other);
		this.registerSetting(mode);
	}

	@EventLink
	public void onUpdate(UpdateEvent e) {
		if (!PlayerUtil.inGame()) return;
		switch (mode.getMode()) {
        case "Advanced":
            mc.theWorld.playerEntities.stream().filter(player -> player != mc.thePlayer).forEach(player -> {
            	if (mc.thePlayer.getDistanceSq(player.posX, mc.thePlayer.posY, player.posZ) > 200) {
            		bots.remove(player);
            	}
            });
			break;
		case "Matrix":
            if (mc.thePlayer.ticksExisted > 110) {
                mc.theWorld.loadedEntityList.stream().filter(entity -> entity instanceof EntityPlayer && entity != mc.thePlayer && entity.getCustomNameTag().isEmpty() && !bots.contains(entity)).forEach(entity -> {
                	bots.add(entity);
                });
            } else {
            	bots.clear();
            }
			break;
		}
	}

	public static boolean bot(Entity entity) {
		if (Haru.instance.getModuleManager().getModule(AntiBot.class).isEnabled()) {
			return bots.contains(entity);
		}
		return false;
	}

	@Override
	public void onDisable() {
		if (!PlayerUtil.inGame())
			return;
		bots.clear();
	}
}
