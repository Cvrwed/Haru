package cc.unknown.command.commands;

import java.util.ArrayList;

import cc.unknown.command.Command;
import cc.unknown.utils.player.FriendUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.entity.Entity;

public class FriendCommand extends Command {
	
	@Override
	public void onExecute(String[] args) {
	    if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("list"))) {
	        listFriends();
	    } else if (args.length == 2 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))) {
	        Entity friendEntity = findEntity(args[1]);
	        if (friendEntity != null) {
	            if (args[0].equalsIgnoreCase("add")) {
	                addFriend(friendEntity);
	            } else {
	                removeFriend(friendEntity);
	            }
	        } else {
	            PlayerUtil.send(getRed() + " Player not found.");
	        }
	    } else {
	        PlayerUtil.send(getRed() + " Syntax Error.");
	    }
	}
	
	@Override
	public String getName() {
		return "friend";
	}

	@Override
	public String getSyntax() {
		return ".friend add <name>";
	}

	@Override
	public String getDesc() {
		return "It allows you to save a friend";
	}

	private void listFriends() {
		ArrayList<Entity> friends = FriendUtil.instance.getFriends();
	    if (friends.isEmpty()) {
	        PlayerUtil.send(getGray() + " You have no friends. :(");
	    } else {
	        PlayerUtil.send(getGray() + " Your friends are:");
	        friends.stream().map(Entity::getName).forEach(name -> PlayerUtil.send(getGray() + name));
	    }
	}

	private void addFriend(Entity friendEntity) {
		FriendUtil.instance.addFriend(friendEntity);
	    PlayerUtil.send(getGray() + " New friend " + friendEntity.getName() + " :)");
	}

	private void removeFriend(Entity friendEntity) {
	    boolean removed = FriendUtil.instance.removeFriend(friendEntity);
	    if (removed) {
	        PlayerUtil.send(getGray() + " Successfully removed " + friendEntity.getName() + " from your friends list!");
	    }
	}
	
	private Entity findEntity(String name) {
	    return mc.theWorld.getLoadedEntityList().stream().filter(entity -> entity.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	@Override
	public String getAlias() {
		return "fr";
	}
}
