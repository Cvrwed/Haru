package cc.unknown.module.impl.other;

import java.util.HashMap;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import net.minecraft.entity.player.EntityPlayer;

public class AntiBot extends Module {
	private final BooleanValue wait = new BooleanValue("Wait 80 ticks", false);
	private final BooleanValue dead = new BooleanValue("Remove dead", true);
    private final HashMap<EntityPlayer, Long> newEntity = new HashMap<>();

	public AntiBot() {
		super("AntiBot", ModuleCategory.Other);
		this.registerSetting(wait, dead);
	}

    @Override
    public void onDisable() {
        newEntity.clear();
    }

    @EventLink
    public void onTick(TickEvent ev) {
        if (wait.isToggled() && !newEntity.isEmpty()) {
            long now = System.currentTimeMillis();
            newEntity.values().removeIf(e -> e < now - 4000L);
        }
    }

	public BooleanValue getDead() {
		return dead;
	}

	public BooleanValue getWait() {
		return wait;
	}

	public HashMap<EntityPlayer, Long> getNewEntity() {
		return newEntity;
	}
}
