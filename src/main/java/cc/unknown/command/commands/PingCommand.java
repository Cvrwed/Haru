package cc.unknown.command.commands;

import cc.unknown.command.Command;
import cc.unknown.utils.player.CombatUtil;
import cc.unknown.utils.player.PlayerUtil;

public class PingCommand extends Command {

	@Override
	public void onExecute(String[] args) {
	    if (args.length == 0) {
	        int ping = CombatUtil.instance.getPing(mc.thePlayer);
	        String color;
	        if (ping >= 0 && ping <= 99) {
	            color = getColor("Green");
	        } else if (ping >= 100 && ping <= 199) {
	            color = getColor("Yellow");
	        } else {
	            color = getColor("Red");
	        }
	        PlayerUtil.send(getColor("White") + " Your ping: " + color + ping + "ms");
	    }
	}

	@Override
	public String getName() {
		return "ping";
	}

	@Override
	public String getSyntax() {
		return ".ping";
	}

	@Override
	public String getDesc() {
		return "Show ur ping";
	}

	@Override
	public String getAlias() {
		return "ms";
	}

}
