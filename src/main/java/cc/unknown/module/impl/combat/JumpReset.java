package cc.unknown.module.impl.combat;

import cc.unknown.event.impl.api.EventLink;
import cc.unknown.event.impl.move.MoveInputEvent;
import cc.unknown.event.impl.move.PreUpdateEvent;
import cc.unknown.event.impl.move.UpdateEvent;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.event.impl.player.StrafeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.helpers.MathHelper;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class JumpReset extends Module {
	private ModeValue mode = new ModeValue("Mode", "Motion", "Normal", "Motion", "Tick", "Hit");
	private SliderValue chance = new SliderValue("Chance", 100, 0, 100, 1);
	private BooleanValue custom = new BooleanValue("Custom motion", false);
	private BooleanValue onlyGround = new BooleanValue("Only ground", true);
	private SliderValue motion = new SliderValue("Motion X/Y", 0.4, 0.4, 0.7, 0.1);

	private int limit = 0;
	private boolean reset = false;
	private boolean jump = false;

	public JumpReset() {
		super("JumpReset", ModuleCategory.Combat);
		this.registerSetting(mode, custom, onlyGround, motion);
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
	public void onUpdate(UpdateEvent e) {
		switch(mode.getMode()) {
		case "Motion":
		case "Hits":
		case "Tick":
		case "Normal":
			if (!(chance.getInput() == 100 || Math.random() <= chance.getInput() / 100))
				return;
			break;
		}
	}

	@EventLink
 	public void onPreUpdate(PreUpdateEvent e) {
 		if (mc.thePlayer.isInLava() || mc.thePlayer.isBurning() || mc.thePlayer.isInWater() || mc.thePlayer.isInWeb) { return; }
 		
 		switch (mode.getMode()) {
 		case "Motion": {
	 			if (mc.thePlayer.hurtTime > 0 && (onlyGround.isToggled() && mc.thePlayer.onGround) && mc.thePlayer.fallDistance > 2.5F) {
	 				mc.thePlayer.motionY = 0.42;
	 				float yaw = mc.thePlayer.rotationYaw * 0.017453292F;
	 				
	 				if (custom.isToggled()) {
		 				mc.thePlayer.motionX -= MathHelper.sin(yaw) * motion.getInput();
		 				mc.thePlayer.motionY += MathHelper.sin(yaw) * motion.getInput();
	 				} else {
		 				mc.thePlayer.motionX -= MathHelper.sin(yaw) * 0.2;
		 				mc.thePlayer.motionY += MathHelper.sin(yaw) * 0.2;
	 				}
	 			}
	 		}
 			break;
 		}
 	}
	
	@EventLink
	public void onReceive(PacketEvent e) {
	    if (e.isReceive()) {
	    	if (e.getPacket() instanceof S12PacketEntityVelocity) {
	    		S12PacketEntityVelocity p = (S12PacketEntityVelocity) e.getPacket();
	            if (p.getEntityID() == mc.thePlayer.getEntityId() && PlayerUtil.inGame()) {
	            	assert mc.thePlayer != null;
	                switch (mode.getMode()) {
                    case "Hits":
                    case "Tick": {
                    	double motionX = p.motionX;
                    	double motionZ = p.motionZ;
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
                    		final S12PacketEntityVelocity s12 = (S12PacketEntityVelocity) e.getPacket();
                    		if (reset) {
                    			reset = false;
                    		}
	                        	
                    		if (s12.getMotionX() > 0 || s12.getMotionZ() > 0) {
                    			jump = true;
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
	public void onMoveInput(MoveInputEvent e) {
 		switch (mode.getMode()) {
 		case "Normal": {
 			if (jump && mc.thePlayer.fallDistance > 2f && (onlyGround.isToggled() && mc.thePlayer.onGround)) {
 				jump = false;
 				e.setJump(true);
 			}
 		}
 		break;
 		}
	}
 	
 	@EventLink
 	public void onStrafe(StrafeEvent event) {
 	    if (mc.thePlayer == null) {
 	        return;
 	    }

 	    if (mode.is("Ticks") || mode.is("Hits") && reset) {
 	        if (!mc.gameSettings.keyBindJump.pressed && shouldJump() && mc.thePlayer.isSprinting() && (onlyGround.isToggled() && mc.thePlayer.onGround) && mc.thePlayer.hurtTime == 9 && mc.thePlayer.fallDistance > 2.5F) {
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
 	            return limit >= MathHelper.randomInt(2, 3);
 	        }

 	        case "Hits": {
 	            return limit >= MathHelper.randomInt(2, 3);
 	        }
 	        default:
 	            return false;
 	    }
 	}
}


