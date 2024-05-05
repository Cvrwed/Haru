package cc.unknown.mixin.mixins.packets;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import cc.unknown.mixin.interfaces.network.packets.IS14PacketEntity;
import net.minecraft.network.play.server.S14PacketEntity;

@Mixin(S14PacketEntity.class)
public class MixinS14PacketEntity implements IS14PacketEntity {

	@Shadow
    protected int entityId;

	/**
	 * @return the entityId
	 */
	@Override
	public int getEntityId() {
		return entityId;
	}
}
