package cc.unknown.command.commands;

import cc.unknown.command.Command;
import net.minecraft.entity.player.EntityPlayer;

public class SpyCommand extends Command {

	@Override // remote view cmd of lb
	public void onExecute(String[] args) {
		if (args.length < 2) {
            if (mc.getRenderViewEntity() != mc.thePlayer) {
                mc.setRenderViewEntity(mc.thePlayer);
                return;
            }
            sendChat(getColor("Red") + " Syntax Error. Use: " + getSyntax());
            return;
		}
		
		String target = args[1];
		
		for (EntityPlayer entity : mc.theWorld.playerEntities) {
		    if (target.equals(entity.getName())) {
		        mc.setRenderViewEntity(entity);
		        sendChat("Spying to §8${entity.name}§3.");
		        sendChat("Execute §8.spy §3again to go back to yours.");
                break;
            }
        }
	}
	
	@Override
    public String getSyntax() {
        return ".spy <user>";
    }

    @Override
    public String getDesc() {
        return "Spying...";
    }

	@Override
	public String getName() {
		return "spy";
	}

	@Override
	public String getAlias() {
		return "spy";
	}
}
