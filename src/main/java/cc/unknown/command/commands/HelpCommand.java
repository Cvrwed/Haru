package cc.unknown.command.commands;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.util.EnumChatFormatting;

public class HelpCommand extends Command {
	
	public HelpCommand() {
		super("help");
 	}

	@Override
	public void onExecute(String alias, String[] args) {
    	if(args.length != 1) {
    		for(Command c : Haru.instance.getCommandManager()) {
    			PlayerUtil.send(EnumChatFormatting.GRAY + " ." + c.getName());
    		}
    	}
    }
}