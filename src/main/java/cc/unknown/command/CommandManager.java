package cc.unknown.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cc.unknown.Haru;
import cc.unknown.command.commands.BindCommand;
import cc.unknown.command.commands.ClearCommand;
import cc.unknown.command.commands.ConfigCommand;
import cc.unknown.command.commands.FriendCommand;
import cc.unknown.command.commands.GameCommand;
import cc.unknown.command.commands.HelpCommand;
import cc.unknown.command.commands.PingCommand;
import cc.unknown.command.commands.ToggleCommand;
import cc.unknown.command.commands.TransactionCommand;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.network.ChatSendEvent;
import cc.unknown.utils.player.PlayerUtil;

public class CommandManager {
	private List<Command> commands = new ArrayList<>();

	public CommandManager() {
		Haru.instance.getEventBus().register(this);
		add(new ConfigCommand());
		add(new HelpCommand());
		add(new BindCommand());
		add(new ToggleCommand());
		add(new FriendCommand());
		add(new TransactionCommand());
		add(new ClearCommand());
		add(new GameCommand());
		add(new PingCommand());
	}
	
    @EventLink
    public void onChatSend(ChatSendEvent e) {
        String message = e.getMessage();

        if(message.startsWith(".")) {
            e.setCancelled(true);
            if (!message.startsWith(".")) { return; }
            message = message.substring(1);

            String[] arguments = message.split(" ");
            String cmdName = arguments[0];
            for (Command cmd : commands) {
                if (cmd.getName().equalsIgnoreCase(arguments[0])) {
                    String[] args = Arrays.copyOfRange(arguments, 1, arguments.length);
                    cmd.onExecute(args);
                    return;
                }
            }
            PlayerUtil.send("§c'" + cmdName + "' doesn't exist");
            return;
        }
    }

	
	private void add(Command cmd) {
    	commands.add(cmd);
    }

	public List<Command> getCommands() {
		return commands;
	}
}