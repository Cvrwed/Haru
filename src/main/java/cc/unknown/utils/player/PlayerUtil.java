package cc.unknown.utils.player;

import java.util.ArrayList;
import java.util.List;

import cc.unknown.utils.Loona;
import cc.unknown.utils.helpers.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;

public class PlayerUtil implements Loona {

	public static void send(final Object message, final Object... objects) {
		if (inGame()) {
			final String format = String.format(message.toString(), objects);
			mc.thePlayer.addChatMessage(new ChatComponentText("" + format));
		}
	}

	public static boolean inGame() {
		return mc.thePlayer != null && mc.theWorld != null;
	}

	public static boolean isMoving() {
		return mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F;
	}

	public static List<EntityPlayer> getClosePlayers(double dis) {
		if (mc.theWorld == null)
			return null;
		List<EntityPlayer> players = new ArrayList<>();

		for (EntityPlayer player : mc.theWorld.playerEntities)
			if (mc.thePlayer.getDistanceToEntity(player) < dis)
				players.add(player);

		return players;
	}
	
    public static EntityPlayer getClosetPlayers(double distance) {
        EntityPlayer target = null;
        for(EntityPlayer entity : mc.theWorld.playerEntities) {
            float tempDistance = mc.thePlayer.getDistanceToEntity(entity);
            if(entity != mc.thePlayer && tempDistance <= distance) {
                target = entity;
                distance = tempDistance;
            }
        }
        return target;
    }

	public static boolean isPlayerNaked(EntityPlayer en) {
		for (int armorPiece = 0; armorPiece < 4; armorPiece++)
			if (en.getCurrentArmor(armorPiece) == null)
				return true;
		return false;
	}

	public static boolean lookingAtPlayer(EntityPlayer viewer, EntityPlayer targetPlayer, double maxDistance) {
		double deltaX = targetPlayer.posX - viewer.posX;
		double deltaY = targetPlayer.posY - viewer.posY + viewer.getEyeHeight();
		double deltaZ = targetPlayer.posZ - viewer.posZ;
		double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
		return distance < maxDistance;
	}

	public static double fovFromEntity(Entity en) {
		return ((double) (mc.thePlayer.rotationYaw - fovToEntity(en)) % 360.0D + 540.0D) % 360.0D - 180.0D;
	}

	public static float getDistanceBetweenAngles(float angle1, float angle2) {
		float angle = Math.abs(angle1 - angle2) % 360.0F;
		if (angle > 180.0F) {
			angle = 360.0F - angle;
		}
		return angle;
	}

	public static float fovToEntity(Entity ent) {
		double x = ent.posX - mc.thePlayer.posX;
		double z = ent.posZ - mc.thePlayer.posZ;
		double yaw = Math.atan2(x, z) * 57.2957795D;
		return (float) (yaw * -1.0D);
	}

	public static boolean fov(Entity entity, float fov) {
		fov = (float) ((double) fov * 0.5D);
		double v = ((double) (mc.thePlayer.rotationYaw - fovToEntity(entity)) % 360.0D + 540.0D) % 360.0D - 180.0D;
		return v > 0.0D && v < (double) fov || (double) (-fov) < v && v < 0.0D;
	}

	public static boolean playerOverAir() {
		double x = mc.thePlayer.posX;
		double y = mc.thePlayer.posY - 1.0D;
		double z = mc.thePlayer.posZ;
		BlockPos p = new BlockPos(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
		return mc.theWorld.isAirBlock(p);
	}

	public static boolean isHoldingWeapon() {
		if (mc.thePlayer.getCurrentEquippedItem() == null) {
			return false;
		} else {
			Item item = mc.thePlayer.getCurrentEquippedItem().getItem();
			return item instanceof ItemSword || item instanceof ItemAxe;
		}
	}

	public static boolean isHoldingSword() {
		if (mc.thePlayer.getCurrentEquippedItem() == null) {
			return false;
		} else {
			Item item = mc.thePlayer.getCurrentEquippedItem().getItem();
			return item instanceof ItemSword;
		}
	}

	public static double getDirection() {
		float moveYaw = mc.thePlayer.rotationYaw;

		if (mc.thePlayer.moveForward != 0f && mc.thePlayer.moveStrafing == 0f) {
			moveYaw += (mc.thePlayer.moveForward > 0) ? 0 : 180;
		} else if (mc.thePlayer.moveForward != 0f && mc.thePlayer.moveStrafing != 0f) {
			if (mc.thePlayer.moveForward > 0)
				moveYaw += (mc.thePlayer.moveStrafing > 0) ? -45 : 45;
			else
				moveYaw += (mc.thePlayer.moveStrafing > 0) ? -45 : 45;

			moveYaw += (mc.thePlayer.moveForward > 0) ? 0 : 180;
		} else if (mc.thePlayer.moveStrafing != 0f && mc.thePlayer.moveForward == 0f) {
			moveYaw += (mc.thePlayer.moveStrafing > 0) ? -90 : 90;
		}

		return (int) Math.floorMod((int) moveYaw, 360);
	}
	
	public static double getDistanceToEntityBox(Entity entity1) {
        Vec3 eyes = entity1.getPositionEyes(1.0F);
        Vec3 pos = getNearestPointBB(eyes, entity1.getEntityBoundingBox());
        double xDist = Math.abs(pos.xCoord - eyes.xCoord);
        double yDist = Math.abs(pos.yCoord - eyes.yCoord);
        double zDist = Math.abs(pos.zCoord - eyes.zCoord);
        return Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2) + Math.pow(zDist, 2));
    }
    
	private static Vec3 getNearestPointBB(Vec3 eye, AxisAlignedBB box) {
        double[] origin = { eye.xCoord, eye.yCoord, eye.zCoord };
        double[] destMins = { box.minX, box.minY, box.minZ };
        double[] destMaxs = { box.maxX, box.maxY, box.maxZ };

        for (int i = 0; i < 3; i++) {
            if (origin[i] > destMaxs[i]) {
                origin[i] = destMaxs[i];
            } else if (origin[i] < destMins[i]) {
                origin[i] = destMins[i];
            }
        }

        return new Vec3(origin[0], origin[1], origin[2]);
    }
	
    public static float getStrafeYaw(float forward, float strafe) {
        float yaw = mc.thePlayer.rotationYaw;

        if((forward == 0) && (strafe == 0))
            return yaw;

        boolean reversed = forward < 0.0f;
        float strafingYaw = 90.0f *
                (forward > 0.0f ? 0.5f : reversed ? -0.5f : 1.0f);

        if (reversed)
            yaw += 180.0f;
        if (strafe > 0.0f)
            yaw -= strafingYaw;
        else if (strafe < 0.0f)
            yaw += strafingYaw;

        return yaw;
    }
}
