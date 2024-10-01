package cc.unknown.module.impl.combat;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.PreMotionEvent;
import cc.unknown.event.impl.netty.SendPacketEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.Cold;
import cc.unknown.utils.player.PlayerUtil;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;

@Register(name = "BlockHit", category = Category.Combat)
public class BlockHit extends Module {
	private SliderValue range = new SliderValue("Range", 3, 1, 6, 0.05);
	private SliderValue chance = new SliderValue("Block Chance", 100, 0, 100, 1);
	private SliderValue delay = new SliderValue("Block Delay", 30, 1, 300, 1);
	private SliderValue combo = new SliderValue("Combo Delay", 30, 1, 300, 1);

	private boolean block;
	private int hits, rHit;
	private boolean tryStartCombo;
	private final Cold actionTimer = new Cold(0);
	private final Cold waitTimer = new Cold(0);

	public BlockHit() {
		this.registerSetting(range, chance, delay, combo);
	}

	@EventLink
	public void onMotion(PreMotionEvent e) {
		if (!PlayerUtil.inGame())
			return;
		if (tryStartCombo && waitTimer.hasFinished()) {
			tryStartCombo = false;
			startCombo();
		}
		if (actionTimer.hasFinished() && block) {
			finishCombo();
		}
	}

	@EventLink
	public void onPacket(SendPacketEvent e) {
		if (e.getPacket() instanceof C02PacketUseEntity) {
			C02PacketUseEntity wrapper = (C02PacketUseEntity) e.getPacket();
			if (wrapper.getAction() == C02PacketUseEntity.Action.ATTACK) {
				if (block)
					return;

				hits++;

				if (!(mc.thePlayer instanceof EntityPlayer) || !(Math.random() <= chance.getInput() / 100)
						|| !PlayerUtil.isHoldingWeapon()
						|| mc.thePlayer.getDistanceToEntity(mc.thePlayer) > range.getInput() || !(rHit == hits))
					return;

				tryStartCombo();
			}
		}
	}

	private void finishCombo() {
		block = false;
		mc.gameSettings.keyBindUseItem.pressed = false;
	}

	private void startCombo() {
		if (!(Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())))
			return;

		block = true;
		mc.gameSettings.keyBindUseItem.pressed = true;
		actionTimer.setMs((long) ThreadLocalRandom.current().nextDouble(delay.getInput(), delay.getInput() + 0.01));
		actionTimer.start();
	}

	public void tryStartCombo() {
		tryStartCombo = true;
		waitTimer.setMs((long) ThreadLocalRandom.current().nextDouble(combo.getInput(), combo.getInput() + 0.01));
		waitTimer.start();
	}
}