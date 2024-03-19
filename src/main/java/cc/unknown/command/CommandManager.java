package cc.unknown.command;

import java.util.ArrayList;

import cc.unknown.command.commands.BindCommand;
import cc.unknown.command.commands.ClearCommand;
import cc.unknown.command.commands.ConfigCommand;
import cc.unknown.command.commands.FriendCommand;
import cc.unknown.command.commands.HelpCommand;
import cc.unknown.command.commands.MusicCommand;
import cc.unknown.command.commands.PetCommand;
import cc.unknown.command.commands.ToggleCommand;
import cc.unknown.command.commands.TransactionCommand;
import cc.unknown.utils.player.PlayerUtil;

@SuppressWarnings("serial")
public class CommandManager extends ArrayList<Command> {

	public CommandManager() {
		add(new ConfigCommand());
		add(new HelpCommand());
		add(new BindCommand());
		add(new ToggleCommand());
		add(new PetCommand());
		add(new FriendCommand());
		add(new TransactionCommand());
		add(new ClearCommand());
		add(new MusicCommand());
	}

	public boolean executeCommand(String string) {
		String raw = string.substring(1);
		String[] split = raw.split(" ");

		if (split.length == 0)
			return false;

		String cmdName = split[0];

		Command command = stream().filter(cmd -> cmd.match(cmdName)).findFirst().orElse(null);

		if (command == null) {
			PlayerUtil.send("§c'" + cmdName + "' doesn't exist");
			return false;
		} else {
			String[] args = new String[split.length - 1];

			System.arraycopy(split, 1, args, 0, split.length - 1);

			command.onExecute(split[0], args);
			return true;
		}
	}
}