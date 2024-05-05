package cc.unknown.module.impl.combat;

import static cc.unknown.utils.helpers.MathHelper.randomInt;

import java.util.function.Supplier;
import java.util.stream.Stream;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.LivingEvent;
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
import cc.unknown.utils.helpers.MathHelper;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

@Register(name = "JumpReset", category = Category.Combat)
public class JumpReset extends Module {
	private ModeValue mode = new ModeValue("Mode", "Legit", "Hit", "Tick", "Normal", "Legit");
	private BooleanValue onlyCombat = new BooleanValue("Enable only during combat", true);
	private SliderValue chance = new SliderValue("Chance", 100, 0, 100, 1);
	private DoubleSliderValue tickTicks = new DoubleSliderValue("Ticks", 0, 0, 0, 20, 1);
	private DoubleSliderValue hitHits = new DoubleSliderValue("Hits", 0, 0, 0, 20, 1);

	private int limit = 0;
	private boolean reset = false;

	public JumpReset() {
		this.registerSetting(mode, onlyCombat, chance, tickTicks, hitHits);
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
	public void onTick(TickEvent e) {
		if (mode.is("Normal")) {
			if (!(mc.currentScreen instanceof GuiScreen) && !mc.thePlayer.isBlocking() && !mc.thePlayer.isUsingItem()
					&& !checkLiquids() && mc.thePlayer instanceof EntityPlayer && mc.thePlayer.onGround
					&& mc.thePlayer.hurtTime > 0 && mc.thePlayer.hurtTime < 10
					&& mc.thePlayer.hurtTime == mc.thePlayer.maxHurtTime - 1
					&& MathHelper.rand().nextFloat() <= chance.getInput() / 100) {
				mc.thePlayer.jump();
			}
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

	private boolean applyChance() {
		Supplier<Boolean> chanceCheck = () -> {
			return chance.getInput() != 100.0D && Math.random() >= chance.getInput() / 100.0D;
		};

		return Stream.of(chanceCheck).map(Supplier::get).anyMatch(Boolean.TRUE::equals);
	}
}
