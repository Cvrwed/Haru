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
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.EnumChatFormatting;

public class JumpReset extends Module {
	private ModeValue mode = new ModeValue("Mode", "Normal", "Normal", "Motion", "Tick", "Hit");
	private SliderValue chance = new SliderValue("Chance", 100, 0, 100, 1);
	private BooleanValue onlyGround = new BooleanValue("Only ground", true);
	private DescValue desc = new DescValue("Options for Motion mode");
	private BooleanValue custom = new BooleanValue("Custom motion", false);
	private BooleanValue aggressive = new BooleanValue("Agressive", false);
	private SliderValue motion = new SliderValue("Motion X/Z", 0.1, 0.1, 2.0, 0.1);
	private DescValue desc2 = new DescValue("Options for Tick/Hit mode");
	private DoubleSliderValue tick = new DoubleSliderValue("Ticks", 3, 4, 1, 20, 1);
	private DoubleSliderValue hit = new DoubleSliderValue("Hits", 3, 4, 1, 20, 1);

	private int limit = 0;
	private boolean reset = false;

	public JumpReset() {
		super("JumpReset", ModuleCategory.Combat);
		this.registerSetting(mode, chance, onlyGround, desc, custom, aggressive, motion, desc2, tick, hit);
	}

	@Override
	public void onEnable() {
		super.onEnable();
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}
	
	@EventLink
	public void onReceive(PacketEvent e) {
		if (e.isReceive()) {
			if (e.getPacket() instanceof S12PacketEntityVelocity) {
				if ((getChance()) || checkLiquids())
					return;
				if (((S12PacketEntityVelocity) e.getPacket()).getEntityID() == mc.thePlayer.getEntityId() && PlayerUtil.inGame()) {
					assert mc.thePlayer != null;
					switch (mode.getMode()) {
					case "Hits":
					case "Tick": {
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
					}
					break;
					case "Normal": {
						if (e.getPacket() instanceof S12PacketEntityVelocity) {
							if (((S12PacketEntityVelocity) e.getPacket()).getEntityID() == mc.thePlayer.getEntityId()) {
								if (mc.thePlayer.onGround) {
									mc.thePlayer.jump();
								}
							}
						}
					}
					break;
					case "Motion": {
						if (e.getPacket() instanceof S12PacketEntityVelocity) {
							if (((S12PacketEntityVelocity) e.getPacket()).getEntityID() == mc.thePlayer.getEntityId()) {
							    if ((onlyGround.isToggled() && mc.thePlayer.onGround) && mc.thePlayer.fallDistance > 2.5f) {
							        float yaw = mc.thePlayer.rotationYaw * 0.017453292F;
							        double reduction = motion.getInputToFloat() * 0.5;
							        double motionX = MathHelper.sin(yaw) * reduction;
							        double motionZ = MathHelper.cos(yaw) * reduction;
							        float speed = mc.thePlayer.isSprinting() ? 1.4f : 0.9f;
		
							        if (custom.isToggled()) {
							            mc.thePlayer.motionX -= speed * motionX;
							            mc.thePlayer.motionZ += speed * motionZ;
						    			PlayerUtil.send(EnumChatFormatting.GRAY + "Reduciendo con yaw");
							        } else if (aggressive.isToggled()) {
							            mc.thePlayer.motionX -= speed * MathHelper.sin(mc.thePlayer.rotationPitch) * reduction;
							            mc.thePlayer.motionZ += speed * MathHelper.cos(mc.thePlayer.rotationPitch) * reduction;
						    			PlayerUtil.send(EnumChatFormatting.GRAY + "Reduciendo con pitch");
							        } else {
							            mc.thePlayer.motionX -= speed * motionX;
							            mc.thePlayer.motionZ += speed * motionZ;
						    			PlayerUtil.send(EnumChatFormatting.GRAY + "Reduciendo");
							        }
							    }
							}
						}
					}
					break;
					}
				}
			}
		}
	}

	@EventLink
	public void onStrafe(StrafeEvent event) {
		if ((getChance()) || checkLiquids())
			return;

		if (mc.thePlayer == null) {
			return;
		}

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

	private boolean getChance() {
		return chance.getInput() == 100 || Math.random() <= chance.getInput() / 100;
	}
}
