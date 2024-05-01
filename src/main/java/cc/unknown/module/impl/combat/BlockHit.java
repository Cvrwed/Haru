package cc.unknown.module.impl.combat;

import cc.unknown.Haru;
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

@Register(name = "BlockHit", category = Category.Combat)
public class BlockHit extends Module {
	private ModeValue mode = new ModeValue("Mode", "Pre", "Pre", "Post");
	private SliderValue chance = new SliderValue("Block Chance", 100, 0, 100, 1);

	private boolean unblock, block;

	public BlockHit() {
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
			AimAssist aimAssist = (AimAssist) Haru.instance.getModuleManager().getModule(AimAssist.class);

			if (wrapper.getAction() == C02PacketUseEntity.Action.ATTACK && aimAssist.getEnemy() != null) {
				block = Math.random() * 100 < chance.getInput();

				if (!block)
					return;

				if (mode.is("Pre")) {
					mc.gameSettings.keyBindUseItem.pressed = true;
					unblock = true;
				}

				if (mode.is("Post")) {
					mc.gameSettings.keyBindUseItem.pressed = false;
					unblock = false;
				}
			}
		}
	}

	@EventLink
	public void onMotion(MotionEvent e) {
		if (!PlayerUtil.inGame())
			return;
		if (e.isPre() && mode.is("Pre")) {
			if (!block)
				return;

			if (unblock) {
				mc.gameSettings.keyBindUseItem.pressed = false;
				unblock = false;
			}
		}

		if (e.isPost() && mode.is("Post")) {
			if (!block)
				return;

			if (unblock) {
				mc.gameSettings.keyBindUseItem.pressed = true;
				unblock = true;
			}
		}
	}

}
