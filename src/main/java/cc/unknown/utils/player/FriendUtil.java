package cc.unknown.utils.player;

import java.util.ArrayList;

import cc.unknown.utils.Loona;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class FriendUtil implements Loona {

	public static ArrayList<Entity> friends = new ArrayList<>();

	public static void addFriend(Entity en) {
		friends.add(en);
	}

	public static boolean removeFriend(Entity en) {
		return friends.remove(en);
	}

	public static ArrayList<Entity> getFriends() {
		return friends;
	}

	public static boolean addFriend(String name) {
		boolean found = false;
		for (Entity entity : mc.theWorld.getLoadedEntityList()) {
			if (entity.getName().equalsIgnoreCase(name) || entity.getCustomNameTag().equalsIgnoreCase(name)) {
				if (!isAFriend(entity)) {
					addFriend(entity);
					found = true;
				}
			}
		}
		return found;
	}

	public static boolean removeFriend(String name) {
		boolean removed = false;
		boolean found = false;
		for (NetworkPlayerInfo networkPlayerInfo : new ArrayList<>(mc.getNetHandler().getPlayerInfoMap())) {
			if (networkPlayerInfo.getDisplayName() != null) {
				String playerName = networkPlayerInfo.getDisplayName().getUnformattedText();
				Entity entity = mc.theWorld.getPlayerEntityByName(playerName);
				if (entity != null && (entity.getName().equalsIgnoreCase(name)
						|| entity.getCustomNameTag().equalsIgnoreCase(name))) {
					removed = removeFriend(entity);
					found = true;
				}
			}
		}
		return found && removed;
	}

	public static boolean isAFriend(Entity entity) {
		if (entity == mc.thePlayer)
			return true;
		for (Entity en : friends) {
			if (en.equals(entity))
				return true;
		}
		try {
			EntityPlayer e = (EntityPlayer) entity;
			if (mc.thePlayer.isOnSameTeam((EntityLivingBase) entity) || mc.thePlayer.getDisplayName()
					.getUnformattedText().startsWith(e.getDisplayName().getUnformattedText().substring(0, 2)))
				return true;
		} catch (Exception x) {
			PlayerUtil.send(x.getMessage());
		}
		return false;
	}
}
