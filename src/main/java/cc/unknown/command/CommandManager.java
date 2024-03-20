package cc.unknown.command;

import java.util.ArrayList;

import cc.unknown.command.commands.*;
import cc.unknown.utils.player.PlayerUtil;

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
		add(new GameCommand());
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