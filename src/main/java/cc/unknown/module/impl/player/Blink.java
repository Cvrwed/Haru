package cc.unknown.module.impl.player;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.event.impl.network.DisconnectionEvent;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.render.RenderEvent;
import cc.unknown.event.impl.world.ChangeWorldEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.ui.clickgui.raven.impl.api.Theme;
import cc.unknown.utils.network.PacketUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.Vec3;

@Register(name = "Blink", category = Category.Player)
public class Blink extends Module {

	private final List<Packet<?>> packets = new ArrayList<>();
	private final List<Packet<?>> packetsReceived = new ArrayList<>();
	private final List<Packet<?>> queuedPackets = new ArrayList<>();
	private final List<Vec3> positions = new ArrayList<>();
	private BooleanValue renderPosition = new BooleanValue("Render actual position", true);
	private BooleanValue disableDisconnect = new BooleanValue("Disable on disconnect", true);
	private BooleanValue disableAttack = new BooleanValue("Disable when attacking", true);

	public Blink() {
		this.registerSetting(renderPosition, disableDisconnect, disableAttack);
	}

	@Override
	public void onEnable() {
		super.onEnable();
		if (mc.thePlayer == null) {
			toggle();
			return;
		}
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
		final Packet<?> p = e.getPacket();
		if (mc.thePlayer == null || mc.thePlayer.isDead)
			return;

		if (p.getClass().getSimpleName().startsWith("S") || p.getClass().getSimpleName().startsWith("C00")
				|| p.getClass().getSimpleName().startsWith("C01"))
			return;

		if (e.isReceive()) {

			synchronized (packetsReceived) {
				queuedPackets.addAll(packetsReceived);
			}
			packetsReceived.clear();
		}

		if (e.isSend()) {

			e.setCancelled(true);
			synchronized (packets) {
				packets.add(p);
			}

			if (p instanceof C03PacketPlayer && ((C03PacketPlayer) p).isMoving()) {
				C03PacketPlayer wrapper = (C03PacketPlayer) p;
				Vec3 packetPos = new Vec3(wrapper.x, wrapper.y, wrapper.z);
				synchronized (positions) {
					positions.add(packetPos);

				}
			}

			if (p instanceof C02PacketUseEntity) {
				C02PacketUseEntity wrapper = (C02PacketUseEntity) p;
				if (disableAttack.isToggled() && wrapper.getAction() == C02PacketUseEntity.Action.ATTACK)
					blink();
				return;
			}
		}
	}

	@EventLink
	public void onPost(MotionEvent e) {
		if (e.isPost()) {
			if (mc.thePlayer == null || mc.thePlayer.isDead || mc.thePlayer.ticksExisted <= 10) {
				blink();
			}
			synchronized (packetsReceived) {
				queuedPackets.addAll(packetsReceived);
			}
			packetsReceived.clear();
		}
	}

	@EventLink
	public void onRender3D(RenderEvent e) {
		if (e.is3D()) {
			if (renderPosition.isToggled()) {
				synchronized (positions) {
					GL11.glPushMatrix();
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					GL11.glEnable(GL11.GL_LINE_SMOOTH);
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glDisable(GL11.GL_DEPTH_TEST);
					mc.entityRenderer.disableLightmap();
					GL11.glBegin(GL11.GL_LINE_STRIP);
					GL11.glColor4f(Theme.instance.getMainColor().getRed() / 255.0f,
							Theme.instance.getMainColor().getGreen() / 255.0f,
							Theme.instance.getMainColor().getBlue() / 255.0f,
							Theme.instance.getMainColor().getAlpha() / 255.0f);

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
		}
	}

	private void blink() {
		synchronized (packetsReceived) {
			queuedPackets.addAll(packetsReceived);
		}
		synchronized (packets) {
			PacketUtil.send(packets.toArray(new Packet<?>[0]));
		}

		reset();
	}

	private void reset() {
		packets.clear();
		packetsReceived.clear();
		positions.clear();
	}

	@EventLink
	public void onWorldLoad(ChangeWorldEvent e) {
		if (e.getWorldClient() == null) {
			reset();
		}
	}

	@EventLink
	public void onDisconnect(final DisconnectionEvent e) {
		this.packets.clear();
		if (disableDisconnect.isToggled())
			this.disable();
	}
}
