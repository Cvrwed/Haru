package cc.unknown.module.impl.combat;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.event.impl.packet.PacketType;
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
import net.minecraft.util.EnumChatFormatting;

public class JumpReset extends Module {
	private ModeValue mode = new ModeValue("Mode", "Normal", "Normal", "Motion", "Tick", "Hit");
	private BooleanValue onlyGround = new BooleanValue("Only ground", true);
	private DescValue desc = new DescValue("Options for Motion mode");
	private BooleanValue custom = new BooleanValue("Custom motion", false);
	private BooleanValue aggressive = new BooleanValue("Agressive", false);
	private SliderValue motion = new SliderValue("Motion X/Z", 0.0, 0.0, 2, 0.1);
	private DescValue desc2 = new DescValue("Options for Tick/Hit mode");
	private DoubleSliderValue tick = new DoubleSliderValue("Ticks", 3, 4, 1, 20, 1);
	private DoubleSliderValue hit = new DoubleSliderValue("Hits", 3, 4, 1, 20, 1);

	private int limit = 0;
	private boolean reset = false;

	public JumpReset() {
		super("JumpReset", ModuleCategory.Combat);
		this.registerSetting(mode, onlyGround, desc, custom, aggressive, motion, desc2, tick, hit);
	}

	@EventLink
	public void onPacket(PacketEvent e) {
		if (e.getType() == PacketType.Receive) {
			final Packet<INetHandlerPlayClient> p = e.getPacket();
			if (p instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity) p).getEntityID() == mc.thePlayer.getEntityId()) {
				if (checkLiquids() || mc.thePlayer == null) return;
				switch (mode.getMode()) {
				case "Hit":
				case "Tick":
					double motionX = ((S12PacketEntityVelocity) e.getPacket()).motionX;
					double motionZ = ((S12PacketEntityVelocity) e.getPacket()).motionZ;
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
					break;
				case "Normal":
					if (mc.thePlayer.onGround) {
						mc.thePlayer.jump();

					}
					break;
				case "Motion":
					if ((onlyGround.isToggled() && mc.thePlayer.onGround) && mc.thePlayer.fallDistance > 2.5f) {
						float yaw = mc.thePlayer.rotationYaw * 0.017453292F;
						double reduction = motion.getInputToFloat() * 0.5;
						double motionX1 = MathHelper.sin(yaw) * reduction;
						double motionZ1 = MathHelper.cos(yaw) * reduction;
						float speed = mc.thePlayer.isSprinting() ? 1.4f : 0.9f;

						if (custom.isToggled()) {
							mc.thePlayer.motionX -= speed * motionX1;
							mc.thePlayer.motionZ += speed * motionZ1;
							PlayerUtil.send(EnumChatFormatting.GRAY + "Reduciendo con yaw");
						} else if (aggressive.isToggled()) {
							mc.thePlayer.motionX -= speed * MathHelper.sin(mc.thePlayer.rotationPitch) * reduction;
							mc.thePlayer.motionZ += speed * MathHelper.cos(mc.thePlayer.rotationPitch) * reduction;
							PlayerUtil.send(EnumChatFormatting.GRAY + "Reduciendo con pitch");
						} else {
							mc.thePlayer.motionX -= speed * motionX1;
							mc.thePlayer.motionZ += speed * motionZ1;
							PlayerUtil.send(EnumChatFormatting.GRAY + "Reduciendo");

						}
					}
					break;
				}
			}
		}
	}

	@EventLink
	public void onStrafe(StrafeEvent e) {
		if (checkLiquids() || mc.thePlayer == null) return;

		if (mode.is("Ticks") || mode.is("Hits") && reset) {
			if (!mc.gameSettings.keyBindJump.pressed && shouldJump() && mc.thePlayer.isSprinting()
					&& (onlyGround.isToggled() && mc.thePlayer.onGround) && mc.thePlayer.hurtTime == 9
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
