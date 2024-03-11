package cc.unknown.mixin.mixins.packets;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import cc.unknown.mixin.interfaces.packet.IS08PacketPlayerPosLook;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

@Mixin(S08PacketPlayerPosLook.class)
public class MixinS08PacketPlayerPosLook implements IS08PacketPlayerPosLook {

	@Shadow
    private float yaw;
	@Shadow
	private float pitch;
	
	@Override
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}
	
	@Override
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

}
