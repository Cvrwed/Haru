package cc.unknown.event.impl.other;

import cc.unknown.event.impl.api.CancellableEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class LivingUpdateEvent extends CancellableEvent {

    public Entity entity;
    public boolean sprinting;

    public LivingUpdateEvent(Entity entity, boolean sprinting) {
		this.entity = entity;
		this.sprinting = sprinting;
	}

	public Entity getEntity() {
		return entity;
	}

	public boolean isSprinting() {
		return sprinting;
	}

	public EntityLivingBase getEntityLiving() {
        return (EntityLivingBase) entity;
    }

}