package cc.unknown.mixin.mixins.packets;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import cc.unknown.mixin.interfaces.network.packets.IC01PacketChatMessage;
import net.minecraft.network.play.client.C01PacketChatMessage;

@Mixin(C01PacketChatMessage.class)
public class MixinC01PacketChatMessage implements IC01PacketChatMessage {
	
	@Shadow
	private String message;

	@Override
	public void setMessage(String s) {
		this.message = s;
	}
}