package cc.unknown.command.commands;

import java.util.ArrayList;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.util.EnumChatFormatting;

public class HelpCommand extends Command {
	
	public HelpCommand() {
		super("help");
	}

	@Override
	public void execute(String[] args) {
    	if(args.length != 1) {
    		for(Command c : Haru.instance.getCommandManager().getCommands()) {
    			PlayerUtil.send(EnumChatFormatting.GRAY + " ." + c.getName());
    		}
    	}
    }

	@Override
	public ArrayList<String> autocomplete(int arg, String[] args) {
		return new ArrayList<>();
	}
}