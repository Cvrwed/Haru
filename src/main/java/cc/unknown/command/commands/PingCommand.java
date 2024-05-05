package cc.unknown.command.commands;

import cc.unknown.command.Command;
import cc.unknown.command.Flips;
import cc.unknown.utils.player.CombatUtil;

@Flips(name = "Ping", alias = "pong", desc = "Show ur ping", syntax = ".ping")
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
	        this.sendChat(getColor("White") + " Your ping: " + color + ping + "ms");
	    }
	}
}
