package cc.unknown.mixin.mixins.packets;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import cc.unknown.mixin.interfaces.network.packets.IC0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;

@Mixin(C0FPacketConfirmTransaction.class)
public class MixinC0FPacketConfirmTransaction implements IC0FPacketConfirmTransaction {
	
	@Shadow
	private short uid;

	@Override
	public void setUid(short uid) {
		this.uid = uid;
	}
}