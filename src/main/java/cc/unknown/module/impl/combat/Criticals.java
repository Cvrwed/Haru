package cc.unknown.module.impl.combat;

import java.util.ArrayList;
import java.util.List;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.network.DisconnectionEvent;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.network.PacketEvent.Type;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.DescValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.Cold;
import cc.unknown.utils.network.PacketUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S14PacketEntity;

public class Criticals extends Module {

	/* Credits to Fyxar */

	private ModeValue mode = new ModeValue("Mode", "Lag", "Lag");
	private DescValue dec = new DescValue("Options for Lag Mode");
	private BooleanValue aggressive = new BooleanValue("Agressive", true);
	private SliderValue delay = new SliderValue("Delay", 500, 0, 1000, 1);
	private SliderValue chance = new SliderValue("Chance", 100, 0, 100, 1);
	private BooleanValue debug = new BooleanValue("Debug", true);

	private boolean onAir, hitGround;
	private List<Packet<INetHandlerPlayServer>> packets = new ArrayList<>(), attackPackets = new ArrayList<>();
	private Cold timer = new Cold(0);

	public Criticals() {
		super("Criticals", ModuleCategory.Combat);
		this.registerSetting(mode, dec, aggressive, delay, chance, debug);
	}

	@Override
	public void onEnable() {
		onAir = false;
		hitGround = false;
	}

	@Override
	public void onDisable() {
		releasePackets();
	}

	@SuppressWarnings("unchecked")
	@EventLink
	public void onSend(PacketEvent e) {
		if (e.getType() == Type.SEND) {
			if (mode.is("Lag")) {
				if (mc.thePlayer.onGround) hitGround = true;

				if (!timer.reached(delay.getInputToLong()) && onAir) {
					e.setCancelled(true);
					if (e.getPacket() instanceof C02PacketUseEntity && e.getPacket() instanceof C0APacketAnimation) {
						if (aggressive.isToggled()) {
							e.setCancelled(false);
						} else
							attackPackets.add((Packet<INetHandlerPlayServer>) e.getPacket());
					} else {
						packets.add((Packet<INetHandlerPlayServer>) e.getPacket());
					}
				}

				if (timer.reached(delay.getInputToLong()) && onAir) {
					onAir = false;
					releasePackets();
				}

				if (e.getPacket() instanceof C02PacketUseEntity) {
					C02PacketUseEntity wrapper = (C02PacketUseEntity) e.getPacket();

					Entity entity = wrapper.getEntityFromWorld(mc.theWorld);
					if (entity == null)
						return;
					if (wrapper.getAction() == C02PacketUseEntity.Action.ATTACK) {
						if (!mc.thePlayer.onGround) {
							if (!onAir && hitGround && mc.thePlayer.fallDistance <= 1 && (chance.getInputToInt() / 100) > Math.random()) {
								timer.reset();
								onAir = true;
								hitGround = false;
							}
							return;
						}

						switch (mode.getMode()) {
						case "Lag":
							if (onAir) {
								mc.thePlayer.onCriticalHit(entity);
								int n = 0;
							    if (debug.isToggled() && n > 0) {
							    	n++;
								    PlayerUtil.send("Crit x" + n);
								}
							}
							break;
						}
					}
				}
			}
		}

		if (e.getType() == Type.RECEIVE) {
			if (mode.is("Lag")) {
				if (mc.thePlayer == null) hitGround = true;
				if (e.getPacket() instanceof S08PacketPlayerPosLook) hitGround = true;

				if (e.getPacket() instanceof S14PacketEntity) {
					if (!timer.reached(delay.getInputToLong()) && onAir) {
						e.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventLink
	public void onDisconnect(final DisconnectionEvent e) {
		this.disable();
	}

	private void releasePackets() {
		if (PlayerUtil.inGame()) {
			if (!attackPackets.isEmpty())
				attackPackets.forEach(PacketUtil::sendPacketNoEvent);
			if (!packets.isEmpty())
				packets.forEach(PacketUtil::sendPacketNoEvent);
		}

		packets.clear();
		attackPackets.clear();
		timer.reset();
	}
}
