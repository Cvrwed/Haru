package cc.unknown.module.impl.settings;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.network.PacketEvent.Type;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.network.play.client.C15PacketClientSettings;

public class Tweaks extends Module {
	private BooleanValue noClickDelay = new BooleanValue("No Click Delay", true);
	private BooleanValue noJumpDelay = new BooleanValue("No Jump Delay", true);
	public BooleanValue noScore = new BooleanValue("No ScoreBoard", false);
	public BooleanValue noHurtCam = new BooleanValue("No Hurt Cam", true);
	private BooleanValue noC15 = new BooleanValue("Cancel C15", false);

	public Tweaks() {
		super("Tweaks", ModuleCategory.Settings);
		this.registerSetting(noClickDelay, noJumpDelay, noScore, noHurtCam, noC15);
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
			if (e.getType() == Type.SEND) {
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
}
