package cc.unknown.utils.player;

import cc.unknown.utils.Loona;

public class MoveUtil implements Loona {
	public static float[] incrementMoveDirection(float forward, float strafe) {
	    if (forward == 0 && strafe == 0) {
	        return new float[]{forward, strafe};
	    }

	    float value = Math.abs(forward != 0 ? forward : strafe);

	    if (forward > 0) {
	        strafe = (strafe > 0) ? 0 : ((strafe == 0) ? -value : strafe);
	    } else if (forward == 0) {
	        forward = (strafe > 0) ? value : -value;
	    } else {
	        strafe = (strafe < 0) ? 0 : ((strafe == 0) ? value : (strafe > 0) ? 0 : strafe);
	    }

	    return new float[]{forward, strafe};
	}
	
	public static float getPlayerDirection() {
        float direction = mc.thePlayer.rotationYaw;

        if (mc.thePlayer.movementInput.moveForward > 0) {
            if (mc.thePlayer.movementInput.moveStrafe > 0) {
                direction -= 45;
            } else if (mc.thePlayer.movementInput.moveStrafe < 0) {
                direction += 45;
            }
        } else if (mc.thePlayer.movementInput.moveForward < 0) {
            if (mc.thePlayer.movementInput.moveStrafe > 0) {
                direction -= 135;
            } else if (mc.thePlayer.movementInput.moveStrafe < 0) {
                direction += 135;
            } else {
                direction -= 180;
            }
        } else {
            if (mc.thePlayer.movementInput.moveStrafe > 0) {
                direction -= 90;
            } else if (mc.thePlayer.movementInput.moveStrafe < 0) {
                direction += 90;
            }
        }

        return direction;
    }
	
	public static float direction() {
        return direction(mc.thePlayer.rotationYaw, mc.thePlayer.moveForward, mc.thePlayer.moveStrafing);
    }
	
	public static float direction(float rotationYaw) {
        return direction(rotationYaw, mc.thePlayer.moveForward, mc.thePlayer.moveStrafing);
    }

    public static float direction(float rotationYaw, float forward, float strafe) {
    	return (float) Math.toRadians(directionYaw(rotationYaw, forward, strafe));
    }
    
    public static float directionYaw(float rotationYaw, float forward, float strafe) {
        if (forward < 0f) rotationYaw += 180f;
        float f = 1f;
        if (forward < 0f) f = -0.5f;
        if (forward > 0f) f = 0.5f;
        if (strafe > 0f) rotationYaw -= 90f * f;
        if (strafe < 0f) rotationYaw += 90f * f;
        return rotationYaw;
    }
    
    public static void strafe(float yaw, float speed) {
        mc.thePlayer.motionX = -Math.sin(yaw) * speed;
        mc.thePlayer.motionZ = Math.cos(yaw) * speed;
    }
    
    public static void strafe(float speed) {
    	strafe(direction(), speed);
    }
    
    public static void strafeY(float speed) {
    	strafe(direction(), speed);
    	if(mc.thePlayer.onGround) {
    		mc.thePlayer.jump();
    	}
    }
    
    public static float getSpeed() {
        return getSpeed(mc.thePlayer.motionX, mc.thePlayer.motionZ);
    }

    public static float getSpeed(double motionX, double motionZ) {
        return (float) Math.sqrt(motionX * motionX + motionZ * motionZ);
    }

}
