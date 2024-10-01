package cc.unknown.utils.network;

import cc.unknown.utils.client.Cold;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.Packet;

@RequiredArgsConstructor
@Getter
public class TimedPacket {

	private final Packet<?> packet;
    private final Cold cold;
    
    public TimedPacket(final Packet<?> packet, final long millis) {
        this.packet = packet;
		this.cold = null;
    }

}
