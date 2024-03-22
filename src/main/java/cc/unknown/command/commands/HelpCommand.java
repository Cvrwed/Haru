package cc.unknown.command.commands;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.util.EnumChatFormatting;

public class HelpCommand extends Command {

	@Override
	public void onExecute(String[] args) {
        if(args.length != 1) {
            for(Command c : Haru.instance.getCommandManager()) {
            	PlayerUtil.send(EnumChatFormatting.GRAY + c.getSyntax() + " §7- " + c.getDesc());

            }
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
        return "Gives you the syntax of all commands and what they do.";
    }
}