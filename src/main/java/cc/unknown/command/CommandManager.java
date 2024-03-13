package cc.unknown.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import cc.unknown.command.commands.*;
import cc.unknown.utils.player.PlayerUtil;

public class CommandManager {

	private final static CommandManager instance = new CommandManager();
	private List<Command> commands = new ArrayList<>();
    private String prefix = ".";

    public CommandManager() {
    	add(new ConfigCommand());
    	add(new HelpCommand());
    	add(new BindCommand());
    	add(new ToggleCommand());
    	add(new PetCommand());
    	add(new FriendCommand());
    	add(new TransactionCommand());
    }
    
	private void add(Command cmd) {
    	commands.add(cmd);
    }

    public static CommandManager get() {
        return instance;
    }

    public String getPrefix() {
        return this.prefix;
    }
    
    public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public boolean execute(String string) {
        String raw = string.substring(1);
        String[] split = raw.split(" ");

        if (split.length == 0) return false;

        String cmdName = split[0];

        Command command = commands.stream().filter(cmd -> cmd.match(cmdName)).findFirst().orElse(null);

        if (command == null) {
        	PlayerUtil.send("§c'" + cmdName + "' doesn't exist");
        	return false;
        } else {
        	String[] args = new String[split.length - 1];
        	System.arraycopy(split, 1, args, 0, split.length - 1);

        	command.execute(split[0], args);
        	return true;
        }
    }
    
    public Collection<String> autoComplete(String currCmd) {
        String raw = currCmd.substring(1);
        String[] split = raw.split(" ");

        List<String> ret = new ArrayList<>();

        Command currentCommand = split.length >= 1 ? commands.stream().filter(cmd -> cmd.match(split[0])).findFirst().orElse(null) : null;

        if (split.length >= 2 || currentCommand != null && currCmd.endsWith(" ")) {

            if (currentCommand == null) return ret;

            String[] args = new String[split.length - 1];

            System.arraycopy(split, 1, args, 0, split.length - 1);

            List<String> autocomplete = currentCommand.autocomplete(args.length + (currCmd.endsWith(" ") ? 1 : 0), args);

            return autocomplete == null ? new ArrayList<>() : autocomplete;
        } else if (split.length == 1) {
            for (Command command : commands) {
                ret.addAll(command.getNameAndAliases());
            }

            return ret.stream().map(str -> prefix + str).filter(str -> str.toLowerCase().startsWith(currCmd.toLowerCase())).collect(Collectors.toList());
        }

        return ret;
    }

	public List<Command> getCommands() {
		return commands;
	}
    
}