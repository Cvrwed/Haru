package cc.unknown.module.impl.latency;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cc.unknown.Haru;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.impl.combat.AimAssist;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.Cold;
import cc.unknown.utils.network.PacketUtil;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.NetworkManager.InboundHandlerTuplePacketListener;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

@Register(name = "LagRange", category = Category.Latency)
public class LagRange extends Module {

	private final List<Packet<?>> packets = new CopyOnWriteArrayList<>();
	private Cold timer = new Cold(0);
	private SliderValue packetsLag = new SliderValue("Ticks delay", 2, 1, 20, 1);

	public LagRange() {
		this.registerSetting(packetsLag);
	}

	@Override
	public void onEnable() {
		packets.clear();
	}

	@Override
	public void onDisable() {
		for (Packet<?> packet : packets) {
			mc.getNetHandler().getNetworkManager().outboundPacketsQueue.add(new InboundHandlerTuplePacketListener(packet, (GenericFutureListener[]) null));
		}

		packets.clear();
	}

	@EventLink
	public void onPacket(PacketEvent e) {
		if (e.isSend()) {
			packets.add(e.getPacket());
			e.setCancelled(true);
		}
	}

	@EventLink
	public void onPost(MotionEvent e) {
		if (e.isPost()) {
			AimAssist aimAssist = (AimAssist) Haru.instance.getModuleManager().getModule(AimAssist.class);

			if (aimAssist.getEnemy() == null) {
				if (packets.isEmpty() && !timer.getCum(packetsLag.getInputToLong()) && (packets.get(0) instanceof C03PacketPlayer.C04PacketPlayerPosition)) {
					return;
				}

				PacketUtil.sendPacketSilent(packets.get(0));
				packets.remove(0);
				if (timer.getCum(packetsLag.getInputToLong())) {
					timer.reset();
				}
			} else {
				for (Packet<?> packet : packets) {
					PacketUtil.sendPacketSilent(packet);
				}
				packets.clear();
			}
		}
	}

}
