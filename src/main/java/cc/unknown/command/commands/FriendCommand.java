package cc.unknown.command.commands;

import java.util.ArrayList;

import cc.unknown.command.Command;
import cc.unknown.utils.player.FriendUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumChatFormatting;

public class FriendCommand extends Command {

	public FriendCommand() {
		super("friend");
	}

	@Override
	public void onExecute(String alias, String[] args) {
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
	            PlayerUtil.send(EnumChatFormatting.RED + " Player not found.");
	        }
	    } else {
	        PlayerUtil.send(EnumChatFormatting.RED + " Syntax Error.");
	    }
	}

	private void listFriends() {
		ArrayList<Entity> friends = FriendUtil.instance.getFriends();
	    if (friends.isEmpty()) {
	        PlayerUtil.send(EnumChatFormatting.GRAY + " You have no friends. :(");
	    } else {
	        PlayerUtil.send(EnumChatFormatting.GRAY + " Your friends are:");
	        friends.stream().map(Entity::getName).forEach(name -> PlayerUtil.send(EnumChatFormatting.GRAY + name));
	    }
	}

	private void addFriend(Entity friendEntity) {
		FriendUtil.instance.addFriend(friendEntity);
	    PlayerUtil.send(EnumChatFormatting.GRAY + " New friend " + friendEntity.getName() + " :)");
	}

	private void removeFriend(Entity friendEntity) {
	    boolean removed = FriendUtil.instance.removeFriend(friendEntity);
	    if (removed) {
	        PlayerUtil.send(EnumChatFormatting.GRAY + " Successfully removed " + friendEntity.getName() + " from your friends list!");
	    }
	}
	
	private Entity findEntity(String name) {
	    return mc.theWorld.getLoadedEntityList().stream().filter(entity -> entity.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

}
