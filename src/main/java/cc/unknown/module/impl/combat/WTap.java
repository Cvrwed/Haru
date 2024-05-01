package cc.unknown.module.impl.combat;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.network.play.client.C02PacketUseEntity;

@Register(name = "WTap", category = Category.Combat)
public class WTap extends Module {

	private ModeValue mode = new ModeValue("Mode", "Pre", "Pre", "Post");
	private SliderValue chance = new SliderValue("Tap Chance", 100, 0, 100, 1);

	private boolean unsprint, tap;
	
	public WTap() {
		this.registerSetting(mode, chance);
	}
	
	@EventLink
	public void onGui(ClickGuiEvent e) {
	    this.setSuffix("- [" + mode.getMode() + "]");
	}
	
	@EventLink
	public void onPacket(PacketEvent e) {
		if (e.isSend() && e.getPacket() instanceof C02PacketUseEntity) {
			C02PacketUseEntity wrapper = (C02PacketUseEntity) e.getPacket();
			if (wrapper.getAction() == C02PacketUseEntity.Action.ATTACK) {
				tap = Math.random() * 100 < chance.getInput();
		        
				if (!tap) return;
				
				if (mode.is("Pre")) {
			        if (mc.thePlayer.isSprinting() || mc.gameSettings.keyBindSprint.isKeyDown()) {
			            mc.gameSettings.keyBindSprint.pressed = true;
			            unsprint = true;
			        }
				}
				
				if (mode.is("Post")) {
			        if (mc.thePlayer.isSprinting() || mc.gameSettings.keyBindSprint.isKeyDown()) {
			            mc.gameSettings.keyBindSprint.pressed = false;
			            unsprint = false;
			        }
				}
			}
		}
	}

	@EventLink
	public void onMotion(MotionEvent e) {
		if (!PlayerUtil.inGame()) return;
		if (e.isPre() && mode.is("Pre")) {
	        if (!tap) return;

	        if (unsprint) {
	            mc.gameSettings.keyBindSprint.pressed = false;
	            unsprint = false;
	        }
		}
		
		if (e.isPost() && mode.is("Post")) {
	        if (!tap) return;

	        if (unsprint) {
	            mc.gameSettings.keyBindSprint.pressed = true;
	            unsprint = true;
	        }
		}
	}
}

