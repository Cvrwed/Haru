package cc.unknown.utils.player;

import java.util.ArrayList;

import cc.unknown.utils.Loona;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;

public enum FriendUtil implements Loona {
	instance;

	public ArrayList<Entity> friends = new ArrayList<>();

	public void addFriend(Entity en) {
		friends.add(en);
	}

	public boolean removeFriend(Entity en) {
		return friends.remove(en);
	}

	public ArrayList<Entity> getFriends() {
		return friends;
	}

	public boolean addFriend(String name) {
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

	public boolean removeFriend(String name) {
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

	public boolean isAFriend(Entity entity) {
		if (entity == mc.thePlayer)
			return true;
		for (Entity en : friends) {
			if (en.equals(entity))
				return true;
		}
		return false;
	}
}
