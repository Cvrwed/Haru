package cc.unknown.command.commands;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.utils.player.PlayerUtil;

public class HelpCommand extends Command {

	@Override
	public void onExecute(String[] args) {
	    if(args.length != 1) {
	        StringBuilder message = new StringBuilder();
	        
	        for(Command c : Haru.instance.getCommandManager().getCommands()) {
	            message.append(getGreen()).append(" - ").append(getWhite()).append(getBlue()).append(c.getSyntax()).append(getGray() +" [").append(c.getDesc() + "]").append("\n");
	        }

	        getClear();
	        PlayerUtil.send(message.toString());
	    }
	}

	@Override
	public String getName() {
		return "help";
	}

	@Override
	public String getSyntax() {
		return ".help";
	}

	@Override
	public String getDesc() {
		return "Show the all commands";
	}
}