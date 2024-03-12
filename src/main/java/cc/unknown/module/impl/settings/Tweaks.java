package cc.unknown.module.impl.settings;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.UpdateEvent;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.network.play.client.C15PacketClientSettings;

public class Tweaks extends Module {
	private BooleanValue noClickDelay = new BooleanValue("No Click Delay", true);
	private BooleanValue noJumpDelay = new BooleanValue("No Jump Delay", true);
	public BooleanValue noHurtCam = new BooleanValue("No Hurt Cam", true);
	private BooleanValue noC15 = new BooleanValue("Cancel C15", false);
	public BooleanValue noRender = new BooleanValue("No Render", false);

	public Tweaks() {
		super("Tweaks", ModuleCategory.Settings);
		this.registerSetting(noClickDelay, noJumpDelay, noHurtCam, noC15, noRender);
		this.withEnabled(true, Tweaks.class);
	}

	@EventLink
	public void onClick(TickEvent e) {
		if (noClickDelay.isToggled()) {
			mc.leftClickCounter = 0;
		}
	}

	@EventLink
	public void onCancelPacket(PacketEvent e) {
		if (noC15.isToggled() && PlayerUtil.inGame()) {
			if (e.isSend()) {
				if (e.getPacket() instanceof C15PacketClientSettings) {
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventLink
	public void onJump(TickEvent e) {
		if (noJumpDelay.isToggled()) {
			mc.thePlayer.jumpTicks = 0;
		}
	}

	@EventLink
	public void onUpdate(UpdateEvent event) {
		if (noRender.isToggled()) {
			for (Entity en : mc.theWorld.loadedEntityList) {
				if (shouldStopRender(en)) {
					en.renderDistanceWeight = 0.0;
				} else {
					en.renderDistanceWeight = 1.0;
				}
			}
		}
	}

	public static boolean shouldStopRender(Entity entity) {
		return (entity instanceof EntityBoat || entity instanceof EntityMinecart || entity instanceof EntityItemFrame
				|| entity instanceof EntityTNTPrimed || entity instanceof EntityArmorStand
				|| entity instanceof EntityArrow) && entity != mc.thePlayer
				&& mc.thePlayer.getDistanceToEntity(entity) > 45.0f;
	}

	public static String getClientName() {
		return "vanilla";
	}
}
