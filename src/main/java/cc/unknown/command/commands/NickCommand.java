package cc.unknown.command.commands;

import java.util.concurrent.atomic.AtomicBoolean;

import cc.unknown.command.Command;

public class NickCommand extends Command {
	
	public String newnick = "You";
    public AtomicBoolean toggle = new AtomicBoolean(false);

	@Override
	public void onExecute(String[] args) {
		if (args.length == 0) {
			this.sendChat(getColor("Blue") + getSyntax());
		}
	}

	@Override
	public String getName() {
		return "nick";
	}

	@Override
	public String getAlias() {
		return "n";
	}

	@Override
	public String getSyntax() {
		return ".nick <new nick>";
	}

	@Override
	public String getDesc() {
		return "Hidden ur real nick";
	}
	
    public String getFakeName(String s) {
        if (mc.thePlayer != null) {
            s = s.replace(mc.thePlayer.getName(), newnick);
        }
        return s;
    }

}
