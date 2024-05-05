package cc.unknown.command.commands;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.command.Flips;

@Flips(name = "Help", alias = "ayuda", desc = "Show the all commands", syntax = ".help")
public class HelpCommand extends Command {

	@Override
	public void onExecute(String[] args) {
	    if(args.length != 1) {
	        StringBuilder message = new StringBuilder();
	        
	        for(Command c : Haru.instance.getCommandManager().getCommand()) {
	            message.append(getColor("Green")).append(" - ").append(getColor("White")).append(getColor("Blue")).append(c.syntax).append(getColor("Gray") +" [").append(c.desc + "]").append("\n");
	        }

	        this.sendChat(message.toString());
	    }
	}
}