package cc.unknown.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import cc.unknown.Haru;
import cc.unknown.command.commands.BindCommand;
import cc.unknown.command.commands.ClearCommand;
import cc.unknown.command.commands.ConfigCommand;
import cc.unknown.command.commands.FriendCommand;
import cc.unknown.command.commands.HelpCommand;
import cc.unknown.command.commands.PetCommand;
import cc.unknown.command.commands.ToggleCommand;
import cc.unknown.command.commands.TransactionCommand;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.network.play.client.C01PacketChatMessage;

public class CommandManager {

	private List<Command> commands = new ArrayList<>();

	public CommandManager() {
		Haru.instance.getEventBus().register(this);

		add(new ConfigCommand());
		add(new HelpCommand());
		add(new BindCommand());
		add(new ToggleCommand());
		add(new PetCommand());
		add(new FriendCommand());
		add(new TransactionCommand());
		add(new ClearCommand());
	}

	private void add(Command cmd) {
		commands.add(cmd);
	}

	@EventLink
	public void onPacket(PacketEvent e) {
	    if (e.isSend()) {
	        if (!(e.getPacket() instanceof C01PacketChatMessage))
	            return;

	        C01PacketChatMessage wrapper = (C01PacketChatMessage) e.getPacket();
	        String message = wrapper.getMessage();
	        if (message.startsWith(".")) {
	            e.setCancelled(true);
	            String rawArgs = message.substring(1);
	            String[] args = rawArgs.split(" ");

	            for (Command command : getCommands()) {
	                if (message.toLowerCase().startsWith("." + command.getName().toLowerCase())) {
	                    command.execute(args);
	                    return;
	                }
	            }
	        }
	    }
	}

	public boolean execute(String string) {
	    String raw = string.substring(1);
	    String[] split = raw.split(" ");

	    if (split.length == 0)
	        return false;

	    String cmdName = split[0];

	    Command command = commands.stream().filter(cmd -> cmd.match(cmdName)).findFirst().orElse(null);

	    if (command == null) {
	        PlayerUtil.send("§c'" + cmdName + "' doesn't exist");
	        return false;
	    } else {
	        String[] args = Arrays.copyOfRange(split, 1, split.length);
	        command.execute(args);
	        return true;
	    }
	}

	public Collection<String> autoComplete(String currCmd) {
		String raw = currCmd.substring(1);
		String[] split = raw.split(" ");

		List<String> ret = new ArrayList<>();

		Command currentCommand = split.length >= 1
				? commands.stream().filter(cmd -> cmd.match(split[0])).findFirst().orElse(null)
				: null;

		if (split.length >= 2 || currentCommand != null && currCmd.endsWith(" ")) {

			if (currentCommand == null)
				return ret;

			String[] args = new String[split.length - 1];

			System.arraycopy(split, 1, args, 0, split.length - 1);

			List<String> autocomplete = currentCommand.autocomplete(args.length + (currCmd.endsWith(" ") ? 1 : 0),
					args);

			return autocomplete == null ? new ArrayList<>() : autocomplete;
		} else if (split.length == 1) {
			for (Command command : commands) {
				ret.addAll(command.getNameAndAliases());
			}

			return ret.stream().map(str -> "." + str).filter(str -> str.toLowerCase().startsWith(currCmd.toLowerCase()))
					.collect(Collectors.toList());
		}

		return ret;
	}

	public List<Command> getCommands() {
		return commands;
	}
}