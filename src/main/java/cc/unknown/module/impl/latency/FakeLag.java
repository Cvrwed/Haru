package cc.unknown.module.impl.latency;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.network.DisconnectionEvent;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.network.PacketUtil;
import cc.unknown.utils.network.TimedPacket;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

@Register(name = "FakeLag", category = Category.Latency)
public class FakeLag extends Module {

	private Queue<TimedPacket> inboundPackets = new ConcurrentLinkedQueue<>();
	private Queue<TimedPacket> outboundPackets = new ConcurrentLinkedQueue<>();

	private SliderValue inboundDelay = new SliderValue("Inbound Delay", 142, 0, 1000, 10);
	private SliderValue outboundDelay = new SliderValue("Outbound Delay", 250, 0, 1000, 10);

	public FakeLag() {
		this.registerSetting(inboundDelay, outboundDelay);
	}

	@EventLink
	public void onGui(ClickGuiEvent e) {
		AtomicReference<String> suffixRef = new AtomicReference<>();
		suffixRef.set("- [" + inboundDelay.getInputToInt() + " | " + outboundDelay.getInputToInt() + " ms]");
		this.setSuffix(suffixRef.get());
	}

	@Override
	public void onEnable() {
		super.onEnable();
		if (mc.thePlayer == null || mc.isIntegratedServerRunning()) {
			toggle();
			return;
		}
		clearPackets();
	}

	@Override
	public void onDisable() {
		super.onDisable();

		if (mc.thePlayer == null)
			return;

		if (mc.thePlayer != null && !inboundPackets.isEmpty())
			inboundPackets.forEach(p -> {
				PacketUtil.receivePacketSilent(p.getPacket());
			});
		inboundPackets.clear();

		if (mc.thePlayer != null && !outboundPackets.isEmpty())
			outboundPackets.forEach(p -> {
				PacketUtil.sendPacketSilent(p.getPacket());
			});
		outboundPackets.clear();

	}

	@EventLink
	public void onPacket(PacketEvent e) {
		if (mc.thePlayer == null || mc.thePlayer.isDead)
			return;

		if (e.getPacket() instanceof S03PacketTimeUpdate) {
			return;
		}

		if (e.isReceive()) {
			inboundPackets.add(new TimedPacket(e.getPacket()));
			e.setCancelled(true);

			while (!inboundPackets.isEmpty()) {
				if (inboundPackets.peek().getCold().getCum(inboundDelay.getInputToInt())) {
					Packet<?> p = inboundPackets.poll().getPacket();
					PacketUtil.receivePacketSilent(p);
				} else {
					break;
				}
			}
		}

		if (e.isSend()) {
			outboundPackets.add(new TimedPacket(e.getPacket()));
			e.setCancelled(true);

			while (!outboundPackets.isEmpty()) {
				if (outboundPackets.peek().getCold().getCum(outboundDelay.getInputToInt())) {
					Packet<?> p = outboundPackets.poll().getPacket();
					PacketUtil.sendPacketSilent(p);
				} else {
					break;
				}
			}
		}
	}

	@EventLink
	public void onDisconnect(final DisconnectionEvent e) {
		this.disable();
	}

	private void clearPackets() {
		this.outboundPackets.clear();
		this.inboundPackets.clear();
	}

}
