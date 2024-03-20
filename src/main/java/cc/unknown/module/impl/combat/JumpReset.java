package cc.unknown.module.impl.combat;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.event.impl.player.StrafeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.DescValue;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.helpers.MathHelper;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class JumpReset extends Module {
	private ModeValue mode = new ModeValue("Mode", "Tick", "Motion", "Tick", "Hit");
	private BooleanValue onlyGround = new BooleanValue("Only ground", true);
	private DescValue desc = new DescValue("Options for Motion mode");
	private BooleanValue custom = new BooleanValue("Custom motion", false);
	private BooleanValue aggressive = new BooleanValue("Agressive", false);
	private SliderValue motion = new SliderValue("Motion X/Z", 0, 0, 4, 0.1);
	private SliderValue friction = new SliderValue("Friction", 10, 1, 75, 1);
	private DescValue desc2 = new DescValue("Options for Tick/Hit mode");
	private DoubleSliderValue tick = new DoubleSliderValue("Ticks", 3, 4, 1, 20, 1);
	private DoubleSliderValue hit = new DoubleSliderValue("Hits", 3, 4, 1, 20, 1);

	private int limit = 0;
	private boolean reset = false;

	public JumpReset() {
		super("JumpReset", ModuleCategory.Combat);
		this.registerSetting(mode, onlyGround, desc, custom, aggressive, motion, friction, desc2, tick, hit);
	}

	@EventLink
	public void onPacket(PacketEvent e) {
		if (checkLiquids() && PlayerUtil.inGame()) {
			return;
		}
		if (e.isReceive()) {
			final Packet<INetHandlerPlayClient> p = e.getPacket();
			if (p instanceof S12PacketEntityVelocity) {
				S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) p;
				if (wrapper.getEntityID() == mc.thePlayer.getEntityId() && PlayerUtil.inGame()) {
					if (mode.is("Tick") || mode.is("Hit")) {
						double motionX = wrapper.motionX;
						double motionZ = wrapper.motionZ;
						double packetDirection = Math.atan2(motionX, motionZ);
						double degreePlayer = PlayerUtil.getDirection();
						double degreePacket = Math.floorMod((int) Math.toDegrees(packetDirection), 360);
						double angle = Math.abs(degreePacket + degreePlayer);
						double threshold = 120.0;
						angle = Math.floorMod((int) angle, 360);
						boolean inRange = angle >= 180 - threshold / 2 && angle <= 180 + threshold / 2;
						if (inRange) {
							reset = true;
						}
					} else if (mode.is("Motion")) {
						if (onlyGround.isToggled() && mc.thePlayer.onGround && mc.thePlayer.fallDistance > 2.5f) {
							float yaw = mc.thePlayer.rotationYaw * 0.017453292f;
							double reduction = motion.getInputToFloat() * 0.5;
							double motionX = MathHelper.sin(yaw) * reduction;
							double motionZ = MathHelper.cos(yaw) * reduction;

							if (custom.isToggled()) {
								wrapper.motionX -= motionX;
								wrapper.motionZ += motionZ;
							} else if (aggressive.isToggled()) {
								wrapper.motionX -= reduction * friction.getInput();
								wrapper.motionZ -= reduction * friction.getInput();
							} else {
								wrapper.motionX -= MathHelper.sin(yaw) * 0.2;
								wrapper.motionZ += MathHelper.cos(yaw) * 0.2;
							}
						}
					}
				}
			}
		}
	}

	@EventLink
	public void onStrafe(StrafeEvent e) {
		if (checkLiquids() || mc.thePlayer == null)
			return;

		if (mode.is("Ticks") || mode.is("Hits") && reset) {
			if (!mc.gameSettings.keyBindJump.pressed && shouldJump() && mc.thePlayer.isSprinting()
					&& onlyGround.isToggled() && mc.thePlayer.onGround && mc.thePlayer.hurtTime == 9
					&& mc.thePlayer.fallDistance > 2.5F) {
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

	private boolean shouldJump() {
		switch (mode.getMode()) {
		case "Ticks": {
			return limit >= MathHelper.randomInt(tick.getInputMinToInt(), tick.getInputMaxToInt());
		}
		case "Hits": {
			return limit >= MathHelper.randomInt(hit.getInputMinToInt(), hit.getInputMaxToInt());
		}
		default:
			return false;
		}
	}

	private boolean checkLiquids() {
		return mc.thePlayer.isInLava() || mc.thePlayer.isBurning() || mc.thePlayer.isInWater() || mc.thePlayer.isInWeb;
	}
}
