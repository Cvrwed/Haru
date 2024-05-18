package cc.unknown.module.impl.combat;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.LivingEvent;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.Cold;
import cc.unknown.utils.network.PacketUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0BPacketEntityAction;

@Register(name = "SprintReset", category = Category.Combat)
public class SprintReset extends Module {

	private ModeValue mode = new ModeValue("Mode", "WTap", "WTap", "STap", "Packet");
	private SliderValue packets = new SliderValue("Packets", 2, 0, 10, 2);
	private SliderValue onceEvery = new SliderValue("Once Every Hits", 0, 0, 10, 1);
	private SliderValue tapRange = new SliderValue("Tap Range", 3.0, 3.0, 6.0, 0.5);
	private SliderValue chance = new SliderValue("Tap Chance", 100, 0, 100, 1);
	private final Cold timer = new Cold(0);
	private int tap;
    private int hitsCount = 0;

	public SprintReset() {
		this.registerSetting(mode, packets, onceEvery, tapRange, chance);
	}

	@EventLink
	public void onGui(ClickGuiEvent e) {
		this.setSuffix("- [" + mode.getMode() + "]");
	}

	@EventLink
	public void onPacket(PacketEvent e) {
		if (chance.getInput() != 100.0D) {
			if (Math.random() >= chance.getInput() / 100.0D) {
				return;
			}
		}

		Packet<?> p = e.getPacket();
		if (e.isSend() && p instanceof C02PacketUseEntity) {
			C02PacketUseEntity wrapper = (C02PacketUseEntity) p;
			if (wrapper.getAction() == C02PacketUseEntity.Action.ATTACK) {
                double distanceToTarget = mc.thePlayer.getDistanceToEntity(wrapper.getEntityFromWorld(mc.theWorld));
                if (distanceToTarget < tapRange.getInputToInt()) {
                	hitsCount++;
                    if (hitsCount >= onceEvery.getInputToInt()) {
						switch (mode.getMode()) {
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
						case "STap":
						case "WTap":
							if (timer.reached(500L)) {
								timer.reset();
								tap = 2;
							}
							break;
						}
						hitsCount = 0;
                    }
                }
			}
		}
	}

	@EventLink
	public void onLiving(LivingEvent e) {
		if (PlayerUtil.inGame() && PlayerUtil.isMoving()) {
			if (mode.is("STap")) {
				switch (tap) {
				case 2:
					mc.gameSettings.keyBindForward.pressed = false;
					mc.gameSettings.keyBindBack.pressed = true;
					tap--;
					break;
				case 1:
					mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
					mc.gameSettings.keyBindBack.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindBack);
					tap--;
					break;
				}
			}
	
			if (mode.is("WTap")) {
				switch (tap) {
				case 2:
					mc.gameSettings.keyBindForward.pressed = false;
					tap--;
					break;
				case 1:
					mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
					tap--;
					break;
				}
			}
		}
	}
}
