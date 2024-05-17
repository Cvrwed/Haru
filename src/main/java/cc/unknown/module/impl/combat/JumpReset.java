package cc.unknown.module.impl.combat;

import static cc.unknown.utils.helpers.MathHelper.randomInt;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;
import java.util.stream.Stream;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.LivingEvent;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.event.impl.network.KnockBackEvent;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.event.impl.player.StrafeEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.misc.KeybindUtil;
import cc.unknown.utils.network.TimedPacket;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;

@Register(name = "JumpReset", category = Category.Combat)
public class JumpReset extends Module {
	private ModeValue mode = new ModeValue("Mode", "Legit", "Hit", "Tick", "Legit");
	private BooleanValue onlyCombat = new BooleanValue("Enable only during combat", true);
	private BooleanValue antiCombo = new BooleanValue("Anti Combo", false);
	private SliderValue chance = new SliderValue("Chance", 100, 0, 100, 1);
	private SliderValue comboTicks = new SliderValue("Combo Hits", 1, 0, 20, 1);
	private DoubleSliderValue tickTicks = new DoubleSliderValue("Ticks", 0, 0, 0, 20, 1);
	private DoubleSliderValue hitHits = new DoubleSliderValue("Hits", 0, 0, 0, 20, 1);

	private int limit = 0;
	private boolean reset = false;
	private boolean shouldspoof = false;
	private int hitCombo = 0;
	private int enabledticks = 0;
	private ConcurrentLinkedQueue<TimedPacket> inboundPacketsQueue = new ConcurrentLinkedQueue<TimedPacket>();

	public JumpReset() {
		this.registerSetting(mode, onlyCombat, antiCombo, chance, comboTicks, tickTicks, hitHits);
	}

	@EventLink
	public void onGui(ClickGuiEvent e) {
		this.setSuffix("- [" + mode.getMode() + "]");
	}

	@EventLink
	public void onLiving(LivingEvent e) {
		if (PlayerUtil.inGame()) {
			if (mode.is("Tick") || mode.is("Hit")) {
				double direction = Math.atan2(mc.thePlayer.motionX, mc.thePlayer.motionZ);
				double degreePlayer = PlayerUtil.getDirection();
				double degreePacket = Math.floorMod((int) Math.toDegrees(direction), 360);
				double angle = Math.abs(degreePacket + degreePlayer);
				double threshold = 120.0;
				angle = Math.floorMod((int) angle, 360);
				boolean inRange = angle >= 180 - threshold / 2 && angle <= 180 + threshold / 2;
				if (inRange) {
					reset = true;
				}
			}
		}
	}

	@EventLink
	public void onTickPre(TickEvent.Pre e) {
		if (antiCombo.isToggled()) {
			if (hitCombo >= comboTicks.getInputToInt()) {
				shouldspoof = true;
			}
			if (shouldspoof) {
				++enabledticks;
			}
			if (enabledticks >= 100) {
				stop();
				clearInboundQueue();
			}
		}
	}

	@EventLink
	public void onPacket(PacketEvent e) {
		if (e.isSend()) {
			if (e.getPacket() instanceof C02PacketUseEntity && antiCombo.isToggled()) {
				C02PacketUseEntity wrapper = (C02PacketUseEntity) e.getPacket();
				if (wrapper.getAction() == C02PacketUseEntity.Action.ATTACK) {
					hitCombo = 0;
					if (shouldspoof) {
						stop();
						clearInboundQueue();
					}
				}
			}
		}
	}

	@EventLink
	public void onKnockBack(KnockBackEvent e) {
		if (mode.is("Legit")) {
			mc.gameSettings.keyBindSprint.pressed = true;
			mc.gameSettings.keyBindForward.pressed = true;
			mc.gameSettings.keyBindJump.pressed = true;
			mc.gameSettings.keyBindBack.pressed = false;
			reset = true;
		}

		if (antiCombo.isToggled()) {
			++hitCombo;
		}
	}

	@EventLink
	public void onMotion(MotionEvent e) {
		if (mode.is("Legit") && e.isPost()) {
			if (reset) {
				KeyBinding sprint = mc.gameSettings.keyBindSprint;
				KeyBinding forward = mc.gameSettings.keyBindForward;
				KeyBinding jump = mc.gameSettings.keyBindJump;
				KeyBinding back = mc.gameSettings.keyBindBack;

				KeybindUtil.instance.resetKeybindings(sprint, forward, jump, back);
			}
			reset = false;

		}
	}

	@EventLink
	public void onStrafe(StrafeEvent e) {
		if (PlayerUtil.inGame()) {
			if (checkLiquids() || !applyChance())
				return;

			if (mode.is("Ticks") || mode.is("Hits") && reset) {
				if (!mc.gameSettings.keyBindJump.pressed && shouldJump() && mc.thePlayer.isSprinting()
						&& mc.thePlayer.hurtTime == 9
						|| (!onlyCombat.isToggled() && mc.gameSettings.keyBindAttack.isKeyDown())
						|| mc.thePlayer.onGround) {
					mc.gameSettings.keyBindJump.pressed = true;
					limit = 0;
				}
				reset = false;
				return;
			}

			switch (mode.getMode()) {
			case "Ticks": {
				limit++;
			}
				break;

			case "Hits": {
				if (mc.thePlayer.hurtTime == 9) {
					limit++;
				}
			}
				break;
			}
		}
	}

	private boolean shouldJump() {
		switch (mode.getMode()) {
		case "Ticks": {
			return limit >= randomInt(tickTicks.getInputMinToInt(), tickTicks.getInputMaxToInt() + 0.1);
		}
		case "Hits": {
			return limit >= randomInt(hitHits.getInputMinToInt(), hitHits.getInputMaxToInt() + 0.1);
		}
		default:
			return false;
		}
	}

	private boolean checkLiquids() {
		if (mc.thePlayer == null || mc.theWorld == null) {
			return false;
		}
		return Stream.<Supplier<Boolean>>of(mc.thePlayer::isInLava, mc.thePlayer::isBurning, mc.thePlayer::isInWater,
				() -> mc.thePlayer.isInWeb).map(Supplier::get).anyMatch(Boolean.TRUE::equals);
	}

	private void stop() {
		shouldspoof = false;
		hitCombo = 0;
		enabledticks = 0;
	}

	private void clearInboundQueue() {
		for (final TimedPacket packet : inboundPacketsQueue) {
			@SuppressWarnings("rawtypes")
			final Packet p = packet.getPacket();
			p.processPacket(mc.thePlayer.sendQueue.getNetworkManager().getNetHandler());
			inboundPacketsQueue.remove(packet);
		}
	}

	private boolean applyChance() {
		Supplier<Boolean> chanceCheck = () -> {
			return chance.getInput() != 100.0D && Math.random() >= chance.getInput() / 100.0D;
		};

		return Stream.of(chanceCheck).map(Supplier::get).anyMatch(Boolean.TRUE::equals);
	}
}
