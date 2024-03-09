package cc.unknown.module.impl.combat;

import java.util.ArrayList;
import java.util.List;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.other.ShutdownEvent;
import cc.unknown.event.impl.other.StartGameEvent;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.AdvancedTimer;
import cc.unknown.utils.network.PacketUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.EnumChatFormatting;

public class Criticals extends Module {

	/* Credits to Fyxar */

	private ModeValue mode = new ModeValue("Mode", "Lag", "Lag");
	private BooleanValue aggressive = new BooleanValue("Agressive", false);
	private SliderValue delay = new SliderValue("Delay", 250, 0, 500, 1);
	private boolean isInAirServerSided, hitGroundYet;
	private long lastDelay = 0;
	private AdvancedTimer timer = new AdvancedTimer(0);
	private List<Packet<?>> packets = new ArrayList<>(), attackPackets = new ArrayList<>();

	public Criticals() {
		super("Criticals", ModuleCategory.Combat);
		this.registerSetting(mode, aggressive, delay);
	}

	@Override
	public void onEnable() {
		isInAirServerSided = false;
		hitGroundYet = false;
		lastDelay = 0;
	}

	@Override
	public void onDisable() {
		releasePackets();
	}

	@EventLink
	public void onSend(PacketEvent e) {
		if (e.isSend()) {
			if (mode.is("Lag Based")) {
				if (mc.thePlayer.onGround)
					hitGroundYet = true;

				if (!timer.reached(delay.getInputToLong()) && isInAirServerSided) {
	                e.setCancelled(true);
	                if (e.getPacket() instanceof C02PacketUseEntity && e.getPacket() instanceof C0APacketAnimation) {
	                    if (aggressive.isToggled()) {
	                        e.setCancelled(false);
	                    } else attackPackets.add(e.getPacket());
	                } else {
	                    packets.add(e.getPacket());
	                }
	            }

				if (timer.reached(delay.getInputToLong()) && isInAirServerSided) {
					isInAirServerSided = false;
					releasePackets();
				}
			}

			if (e.getPacket() instanceof C02PacketUseEntity) {
				C02PacketUseEntity wrapper = (C02PacketUseEntity) e.getPacket();

				Entity entity = wrapper.getEntityFromWorld(mc.theWorld);
				if (entity == null)
					return;
				if (wrapper.getAction() == C02PacketUseEntity.Action.ATTACK) {
					if (!mc.thePlayer.onGround) {
						if (!isInAirServerSided && hitGroundYet && mc.thePlayer.fallDistance <= 1
								&& System.currentTimeMillis() - lastDelay > 50L) {
							timer.reset();
							lastDelay = System.currentTimeMillis();
							isInAirServerSided = true;
							hitGroundYet = false;
						}
						return;
					}

					switch (mode.getMode()) {
					case "Lag Based":
						if (isInAirServerSided) {
							mc.thePlayer.onCriticalHit(entity);
							PlayerUtil.send(EnumChatFormatting.RED + "[ඞ]" + EnumChatFormatting.WHITE + "Crit");
						}
						break;
					}
				}
			}
		}
		
		if (e.isReceive()) {
			if (e.getPacket() instanceof S08PacketPlayerPosLook) hitGroundYet = true;
		}
	}

	@EventLink
	public void onStartGame(StartGameEvent e) {
		this.disable();
	}

	@EventLink
	public void onShutdown(ShutdownEvent e) {
		this.disable();
	}

	private void releasePackets() {
		if (PlayerUtil.inGame()) {
			if (!attackPackets.isEmpty()) attackPackets.forEach(PacketUtil::sendPacketNoEvent);
			if (!packets.isEmpty()) packets.forEach(PacketUtil::sendPacketNoEvent);
		}
		
		packets.clear();
		attackPackets.clear();
		timer.reset();

	}
}
