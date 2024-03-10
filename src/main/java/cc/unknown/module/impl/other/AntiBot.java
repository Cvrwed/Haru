package cc.unknown.module.impl.other;

import java.util.ArrayList;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.PreUpdateEvent;
import cc.unknown.event.impl.other.WorldEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.entity.Entity;

public class AntiBot extends Module {
	private final ModeValue mode = new ModeValue("Mode", "Advanced", "Advanced");
	private static ArrayList<Entity> bots = new ArrayList<>();

	public AntiBot() {
		super("AntiBot", ModuleCategory.Other);
		this.registerSetting(mode);
	}

    @EventLink
    public void onPre(PreUpdateEvent e) {
    	switch (mode.getMode()) {
    	case "Advanced":
            mc.theWorld.playerEntities.forEach(player -> {
                if (mc.thePlayer.getDistanceSq(player.posX, mc.thePlayer.posY, player.posZ) > 200) {
                    bots.remove(player);
                }

                if (player.ticksExisted < 5 || player.isInvisible() || mc.thePlayer.getDistanceSq(player.posX, mc.thePlayer.posY, player.posZ) > 100 * 100) {
                    add(player);
                }
            });
    		break;
    	}
    }


	@Override
	public void onDisable() {
		if (!PlayerUtil.inGame())
			return;
		bots.clear();
	}
	
    @EventLink
    public void onWorld(WorldEvent e){
    	bots.clear();
    }

    public boolean add(Entity entity) {
        if (!bots.contains(entity)) bots.add(entity);
        return false;
    }
}
