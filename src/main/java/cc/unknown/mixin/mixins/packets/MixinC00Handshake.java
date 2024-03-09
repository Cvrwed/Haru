package cc.unknown.mixin.mixins.packets;

import java.io.IOException;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.handshake.client.C00Handshake;

@Mixin(C00Handshake.class)
public class MixinC00Handshake {
	
	@Shadow
    private int protocolVersion;
	@Shadow
    private String ip;
	@Shadow
    private int port;
	@Shadow
    private EnumConnectionState requestedState;
	
	/**
	 * Writes the packet data to the given PacketBuffer after removing the "\0FML\0" symbol.
	 * 
	 * @author Cvrwed
	 * @param buf The PacketBuffer to write the data to.
	 * @throws IOException If an I/O errors occurs.
	 * @reason Removes the symbol to ensure compability with Minecraft servers using the anti-Forge plugin.
	 */
	
	@Overwrite
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarIntToBuffer(this.protocolVersion);
        buf.writeString(this.ip);
        buf.writeShort(this.port);
        buf.writeVarIntToBuffer(this.requestedState.getId());
    }
	
	/**
	 * Reads the packet data from the given PacketBuffer after removing the "\0FML\0" symbol.
	 * 
	 * @param buf The PacketBuffer to read the data from.
	 * @throws IOException If an I/O error occurs
	 * @author Cvrwed
	 * @reason Reads the packet data after removing the symbol for compability with Minecraft servers using the anti-Forge plugin.
	 */
	
	@Overwrite
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.protocolVersion = buf.readVarIntFromBuffer();
        this.ip = buf.readStringFromBuffer(255);
        this.port = buf.readUnsignedShort();
        this.requestedState = EnumConnectionState.getById(buf.readVarIntFromBuffer());
    }
}
