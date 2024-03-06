package cc.unknown.module.impl.player;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import cc.unknown.event.impl.api.EventLink;
import cc.unknown.event.impl.move.PostUpdateEvent;
import cc.unknown.event.impl.other.ShutdownEvent;
import cc.unknown.event.impl.other.StartGameEvent;
import cc.unknown.event.impl.other.WorldEvent;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.event.impl.packet.PacketType;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.ui.clickgui.theme.Theme;
import cc.unknown.utils.client.AdvancedTimer;
import cc.unknown.utils.network.PacketUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.Vec3;

public class Blink extends Module {

	private AdvancedTimer pulseTimer = new AdvancedTimer(0);
	private final ArrayList<Packet<?>> packets = new ArrayList<>();
	private final ArrayList<Packet<?>> packetsReceived = new ArrayList<>();
	private final ArrayList<Packet<?>> queuedPackets = new ArrayList<>();
	private final ArrayList<Vec3> positions = new ArrayList<>();

	private ModeValue mode = new ModeValue("Mode", "Sent", "Sent", "Received", "Both");
	private BooleanValue pulse = new BooleanValue("Pulse", false);
	private SliderValue pulseDelay = new SliderValue("PulseDelay", 1000, 500, 5000, 100);

	public Blink() {
		super("Blink", ModuleCategory.Player);
		this.registerSetting(mode, pulse, pulseDelay);
	}

	@Override
	public void onEnable() {
		super.onEnable();
		if (mc.thePlayer == null) {
			toggle();
			return;
		}
		pulseTimer.reset();
		packets.clear();
	}

	@Override
	public void onDisable() {
		super.onDisable();

		if (mc.thePlayer == null)
			return;
		blink();
	}

	@EventLink
	public void onPacket(PacketEvent e) {
		if (mc.thePlayer == null || mc.thePlayer.isDead)
			return;

		if (e.getPacket().getClass().getSimpleName().startsWith("S"))
			return;

		if (e.getPacket().getClass().getSimpleName().startsWith("C00")
				|| e.getPacket().getClass().getSimpleName().startsWith("C01"))
			return;

		if (e.isCancelled())
			return;

		switch (mode.getMode()) {
		case "Sent":
			if (e.getType() == PacketType.Receive) {
				synchronized (packetsReceived) {
					queuedPackets.addAll(packetsReceived);
				}
				packetsReceived.clear();
			}
			if (e.getType() == PacketType.Send) {
				e.setCancelled(true);
				synchronized (packets) {
					packets.add(e.getPacket());
				}
				if (e.getPacket() instanceof C03PacketPlayer && ((C03PacketPlayer) e.getPacket()).isMoving()) {
					Vec3 packetPos = new Vec3(((C03PacketPlayer) e.getPacket()).x, ((C03PacketPlayer) e.getPacket()).y,
							((C03PacketPlayer) e.getPacket()).z);
					synchronized (positions) {
						positions.add(packetPos);
					}
				}
			}
			break;
		case "Received":
			if (e.getType() == PacketType.Receive && mc.thePlayer.ticksExisted > 10) {
				e.setCancelled(true);
				synchronized (packetsReceived) {
					packetsReceived.add(e.getPacket());
				}
			}
			if (e.getType() == PacketType.Send) {
				synchronized (packets) {
					PacketUtil.send(packets.toArray(new Packet[0]));
				}
				packets.clear();
				
				if (e.getPacket() instanceof C03PacketPlayer && ((C03PacketPlayer) e.getPacket()).isMoving()) {
					Vec3 packetPos = new Vec3(((C03PacketPlayer) e.getPacket()).x, ((C03PacketPlayer) e.getPacket()).y,
							((C03PacketPlayer) e.getPacket()).z);
					synchronized (positions) {
						positions.add(packetPos);
					}
				}
			}
			break;
		case "Both":
			if (e.getType() == PacketType.Receive && mc.thePlayer.ticksExisted > 10) {
				e.setCancelled(true);
				synchronized (packetsReceived) {
					packetsReceived.add(e.getPacket());
				}
			}
			if (e.getType() == PacketType.Send) {
				e.setCancelled(true);
				synchronized (packets) {
					packets.add(e.getPacket());
				}
				if (e.getPacket() instanceof C03PacketPlayer && ((C03PacketPlayer) e.getPacket()).isMoving()) {
					Vec3 packetPos = new Vec3(((C03PacketPlayer) e.getPacket()).x, ((C03PacketPlayer) e.getPacket()).y,
							((C03PacketPlayer) e.getPacket()).z);
					synchronized (positions) {
						positions.add(packetPos);
					}
				}
			}
			break;
		}
	}

	@EventLink
	public void onPost(PostUpdateEvent event) {
		if (mc.thePlayer == null || mc.thePlayer.isDead || mc.thePlayer.ticksExisted <= 10) {
			blink();
		}

		switch (mode.getMode()) {
		case "Sent":
			synchronized (packetsReceived) {
				queuedPackets.addAll(packetsReceived);
			}
			packetsReceived.clear();
			break;
		case "Received":
			synchronized (packets) {
				PacketUtil.send(packets.toArray(new Packet[0]));
			}
			packets.clear();
			break;
		}

		if (pulse.isToggled() && pulseTimer.hasTimeElapsed(pulseDelay.getInputToLong())) {
			blink();
			pulseTimer.reset();
		}
	}

	@EventLink
	public void onRender3D(Render3DEvent event) {
		synchronized (positions) {
			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glEnable(GL11.GL_LINE_SMOOTH);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			mc.entityRenderer.disableLightmap();
			GL11.glBegin(GL11.GL_LINE_STRIP);
			GL11.glColor4f(Theme.getMainColor().getRed() / 255.0f, Theme.getMainColor().getGreen() / 255.0f,
					Theme.getMainColor().getBlue() / 255.0f, Theme.getMainColor().getAlpha() / 255.0f);

			double renderPosX = mc.getRenderManager().viewerPosX;
			double renderPosY = mc.getRenderManager().viewerPosY;
			double renderPosZ = mc.getRenderManager().viewerPosZ;

			for (Vec3 pos : positions) {
				GL11.glVertex3d(pos.xCoord - renderPosX, pos.yCoord - renderPosY, pos.zCoord - renderPosZ);
			}

			GL11.glColor4d(1.0, 1.0, 1.0, 1.0);
			GL11.glEnd();
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL11.GL_LINE_SMOOTH);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glPopMatrix();
		}
	}

	private void blink() {
		synchronized (packetsReceived) {
			queuedPackets.addAll(packetsReceived);
		}
		synchronized (packets) {
			PacketUtil.send(packets.toArray(new Packet<?>[0]));
		}

		packets.clear();
		packetsReceived.clear();
		positions.clear();
	}

	@EventLink
	public void onStartGame(StartGameEvent e) {
		this.disable();
	}

	@EventLink
	public void onWorldLoad(WorldEvent e) {
		if (e.getWorldClient() == null) {
			packets.clear();
			packetsReceived.clear();
			positions.clear();
		}
	}

	@EventLink
	public void onShutdown(ShutdownEvent e) {
		this.disable();
	}
}
