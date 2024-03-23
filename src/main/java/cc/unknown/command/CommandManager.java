package cc.unknown.command;

import java.util.ArrayList;
import java.util.Arrays;

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
		add(new PingCommand());
	}

    public boolean executeCommand(String text) {
        if (!text.startsWith(".")) { return false; }
        text = text.substring(1);

        String[] arguments = text.split(" ");
        String cmdName = arguments[0];
        for (Command cmd : this) {
            if (cmd.getName().equalsIgnoreCase(arguments[0])) {
                String[] args = Arrays.copyOfRange(arguments, 1, arguments.length);
                cmd.onExecute(args);
                return true;
            }
        }
        PlayerUtil.send("§c'" + cmdName + "' doesn't exist");
        return false;
    }
}