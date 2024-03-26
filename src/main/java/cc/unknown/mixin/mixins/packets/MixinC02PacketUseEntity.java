package cc.unknown.mixin.mixins.packets;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import cc.unknown.mixin.interfaces.network.packets.IC02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity;

@Mixin(C02PacketUseEntity.class)
public class MixinC02PacketUseEntity implements IC02PacketUseEntity {

	@Shadow
	private int entityId;

	/**
	 * @return the entityId
	 */
	@Override
	public int getEntityId() {
		return entityId;
	}
}
