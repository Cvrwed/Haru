package cc.unknown.module.impl.combat;

import static cc.unknown.utils.helpers.MathHelper.randomInt;

import java.util.function.Supplier;
import java.util.stream.Stream;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.network.PacketEvent.Type;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.event.impl.player.StrafeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.DescValue;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.MathHelper;

public class JumpReset extends Module {
	private ModeValue mode = new ModeValue("Mode", "Tick", "Motion", "Hit", "Tick");
	private BooleanValue onlyGround = new BooleanValue("Only Ground", true);
	private SliderValue chance = new SliderValue("Chance", 100, 0, 100, 1);
	private DescValue motionOptionsDesc = new DescValue("Options for Motion Mode");
	private BooleanValue reduceYaw = new BooleanValue("Reduce Rotation Yaw", true);
	private BooleanValue customMotion = new BooleanValue("Custom Motion", false);
	private BooleanValue aggressive = new BooleanValue("Aggressive", false);
	private SliderValue motionXZ = new SliderValue("Motion X/Z", 0, 0, 4, 0.1);
	private SliderValue friction = new SliderValue("Friction", 10, 1, 75, 1);
	private DescValue tickHitOptionsDesc = new DescValue("Options for Tick/Hit Mode");
	private DoubleSliderValue tickTicks = new DoubleSliderValue("Ticks", 3, 4, 1, 20, 1);
	private DoubleSliderValue hitHits = new DoubleSliderValue("Hits", 3, 4, 1, 20, 1);

	private int limit = 0;
	private boolean reset = false;

	public JumpReset() {
		super("JumpReset", ModuleCategory.Combat);
		this.registerSetting(mode, onlyGround, chance, motionOptionsDesc, reduceYaw, customMotion, aggressive, 
				motionXZ, friction, tickHitOptionsDesc, tickTicks, hitHits);
	}
	
	@EventLink
	public void onGui(ClickGuiEvent e) {
		this.setSuffix(mode.getMode());
	}

	@EventLink
	public void onPacket(PacketEvent e) {
		if (PlayerUtil.inGame() && checkLiquids() || applyChance()) return;
		Packet<?> p = e.getPacket();
		
	    if (e.getType() == Type.RECEIVE) {
	        if (p instanceof S12PacketEntityVelocity) {
	        	S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity)p;

	    	    if (wrapper.getEntityID() != mc.thePlayer.getEntityId() || !PlayerUtil.inGame()) {
	    	        return;
	    	    }
	    	    
	    	    if (mode.is("Tick") || mode.is("Hit")) {
	    		    double packetDirection = Math.atan2(wrapper.motionX, wrapper.motionZ);
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
	    	        if (!mc.gameSettings.keyBindJump.pressed && onlyGround.isToggled() && mc.thePlayer.onGround && mc.thePlayer.fallDistance > 2.5f) {
	    	    	    double reduction = motionXZ.getInputToFloat() * 0.5;

	    	    	    if (reduceYaw.isToggled()) {
	    	    	        float yaw = mc.thePlayer.rotationYaw * 0.017453292f;
	    	    	        double motionX = MathHelper.sin(yaw) * reduction;
	    	    	        double motionZ = MathHelper.cos(yaw) * reduction;

	    	    	        if (customMotion.isToggled()) {
	    	    	            wrapper.motionX -= motionX;
	    	    	            wrapper.motionZ += motionZ;
	    	    	        } else if (aggressive.isToggled()) {
	    	    	            wrapper.motionX *= reduction * friction.getInput();
	    	    	            wrapper.motionZ += reduction * friction.getInput();
	    	    	        } else {
	    	    	            wrapper.motionX -= MathHelper.sin(yaw) * 0.2;
	    	    	            wrapper.motionZ += MathHelper.cos(yaw) * 0.2;
	    	    	        }
	    	    	    } else {
	    	    	        if (customMotion.isToggled()) {
	    	    	            wrapper.motionX -= reduction;
	    	    	            wrapper.motionZ += reduction;
	    	    	        } else if (aggressive.isToggled()) {
	    	    	            wrapper.motionX *= reduction * friction.getInput();
	    	    	            wrapper.motionZ += reduction * friction.getInput();
	    	    	        } else {
	    	    	            wrapper.motionX -= 0.2;
	    	    	            wrapper.motionZ += 0.2;
	    	    	        }
	    	    	    }
	    	        }
	    	    }
	        } else if (p instanceof S27PacketExplosion) {
	        	S27PacketExplosion wrapper = (S27PacketExplosion)p;
	    	    if (mode.is("Tick") || mode.is("Hit")) {
	    		    double packetDirection = Math.atan2(wrapper.field_149152_f, wrapper.field_149159_h);
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
	    	        if (!mc.gameSettings.keyBindJump.pressed && onlyGround.isToggled() && mc.thePlayer.onGround && mc.thePlayer.fallDistance > 2.5f) {
	    	    	    double reduction = motionXZ.getInputToFloat() * 0.5;
    	    	        float yaw = mc.thePlayer.rotationYaw * 0.017453292f;

	    	    	    if (customMotion.isToggled()) {
	    	    	        wrapper.field_149152_f *= MathHelper.sin(yaw) * reduction;
	    	    	        wrapper.field_149153_g *= reduction * 0.1;
	    	    	        wrapper.field_149159_h *= MathHelper.cos(yaw) * reduction;;
	    	    	    } else if (aggressive.isToggled()) {
	    	    	        wrapper.field_149152_f *= reduction * friction.getInput();
	    	    	        wrapper.field_149153_g *= reduction * 0.1;
	    	    	        wrapper.field_149159_h *= reduction * friction.getInput();
	    	    	    } else {
	    	    	        wrapper.field_149152_f -= 0.2;
	    	    	        wrapper.field_149153_g -= reduction * 0.1;
	    	    	        wrapper.field_149159_h += 0.2;
	    	    	    }
	    	        }
	    	    }
	        }
	    }
	}

	@EventLink
	public void onStrafe(StrafeEvent e) {		
		if (PlayerUtil.inGame() && checkLiquids() || applyChance()) return;

		if (mode.is("Ticks") || mode.is("Hits") && reset) {
			if (!mc.gameSettings.keyBindJump.pressed && shouldJump() && mc.thePlayer.isSprinting() && onlyGround.isToggled() && mc.thePlayer.onGround && mc.thePlayer.hurtTime == 9 && mc.thePlayer.fallDistance > 2.5F) {
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
			return limit >= randomInt(tickTicks.getInputMinToInt(), tickTicks.getInputMaxToInt());
		}
		case "Hits": {
			return limit >= randomInt(hitHits.getInputMinToInt(), hitHits.getInputMaxToInt());
		}
		default:
			return false;
		}
	}

	private boolean checkLiquids() {
	    if (mc.thePlayer == null || mc.theWorld == null) {
	        return false;
	    }
	    return Stream.<Supplier<Boolean>>of(mc.thePlayer::isInLava, mc.thePlayer::isBurning, mc.thePlayer::isInWater, () -> mc.thePlayer.isInWeb).map(Supplier::get).anyMatch(Boolean.TRUE::equals);
	}
	
	private boolean applyChance() {
	    Supplier<Boolean> chanceCheck = () -> {
	        return chance.getInput() != 100.0D && Math.random() >= chance.getInput() / 100.0D;
	    };

	    return Stream.of(chanceCheck).map(Supplier::get).anyMatch(Boolean.TRUE::equals);
	}
}
