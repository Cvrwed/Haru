package cc.unknown.module.impl.combat;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.netty.PacketEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;

@Register(name = "SprintReset", category = Category.Combat)
public class SprintReset extends Module {
	
	private boolean reset;
	private int hits;
	
	@EventLink
	public void onSendPacket(PacketEvent event) {
		if (!event.isSend()) return;
		if (event.getPacket() instanceof C02PacketUseEntity) {
			C02PacketUseEntity wrapper = (C02PacketUseEntity) event.getPacket();
			if (wrapper.getAction() == C02PacketUseEntity.Action.ATTACK) {
				EntityPlayer target = (EntityPlayer) wrapper.getEntityFromWorld(mc.theWorld);
				if (target == null) return;
				reset = target.hurtTime >= 6;
				if (!reset) return;
				hits = 0;
			}
		}
	}
	
	
	@EventLink
	public void onTick(TickEvent event) {
		if (!reset) return;
		if (mc.thePlayer.isSprinting() && mc.thePlayer.movementInput.moveForward > 0) {
			hits++;
			switch (hits) {
			case 2:
				mc.thePlayer.setSprinting(false);
			case 3:
				mc.thePlayer.setSprinting(true);
			}
		}
	}
}
