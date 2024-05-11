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
import cc.unknown.utils.network.PacketUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0BPacketEntityAction;

@Register(name = "SprintReset", category = Category.Combat)
public class SprintReset extends Module {

	private ModeValue mode = new ModeValue("Mode", "WTap", "WTap", "STap", "ShiftTap", "Packet");
	private SliderValue packets = new SliderValue("Packets", 2, 0, 10, 2);
	private SliderValue chance = new SliderValue("Tap Chance", 100, 0, 100, 1);
	private boolean unsprint;

	public SprintReset() {
		this.registerSetting(mode, packets, chance);
	}

	@EventLink
	public void onGui(ClickGuiEvent e) {
		this.setSuffix("- [" + mode.getMode() + "]");
	}

	@EventLink
	public void onPacket(PacketEvent e) { // Testing..
		if (!(chance.getInput() == 100 || Math.random() <= chance.getInput() / 100))
			return;

		Packet<?> p = e.getPacket();
		if (e.isSend() && p instanceof C02PacketUseEntity) {
			C02PacketUseEntity wrapper = (C02PacketUseEntity) p;
			if (wrapper.getAction() == C02PacketUseEntity.Action.ATTACK) {
				switch (mode.getMode()) {
				case "ShiftTap":
					KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
					KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
					break;
				case "Packet":
					if (mc.thePlayer.isSprinting()) PacketUtil.sendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
					for (int i = 0; i < (packets.getInputToInt() - 2.0); i++) {
						if (i % 2 == 0) {
							PacketUtil.sendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
						} else {
							PacketUtil.sendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
						}
					}
					if (mc.thePlayer.isSprinting()) PacketUtil.sendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
					break;
				case "WTap":
					if (mc.thePlayer.isSprinting() || mc.gameSettings.keyBindSprint.isKeyDown()) {
						mc.gameSettings.keyBindSprint.pressed = true;
						unsprint = true;
					}
					break;
				}

			}
		}
	}

	@EventLink
	public void onMotion(MotionEvent e) {
		if (!PlayerUtil.inGame()) return;
		if (e.isPre()) {
			if (mode.is("WTap")) {
				if (unsprint) {
					mc.gameSettings.keyBindSprint.pressed = false;
					unsprint = false;
				}
			}
		}
	}
}
