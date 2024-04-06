package cc.unknown.command.commands;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.config.ClientConfig;
import cc.unknown.module.Module;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.util.EnumChatFormatting;

public class ToggleCommand extends Command {

    @Override
    public void onExecute(String[] args) {
        if (args.length != 1) {
        	PlayerUtil.send(getColor("Gray") + " " + getAll());
        } else {
            String module = args[0];
            Module mod = Haru.instance.getModuleManager().getModule(module);
            if (mod == null) {
            	PlayerUtil.send(getColor("Red") + " Module not found!");
            } else {
            	Haru.instance.getModuleManager().getModule(module).toggle();
                PlayerUtil.send(getColor("White") + " %s " + getColor("Gray") + "%s", Haru.instance.getModuleManager().getModule(module).getName(), Haru.instance.getModuleManager().getModule(module).isEnabled() ? EnumChatFormatting.GREEN + "enabled": getColor("Red") + "disabled.");
                ClientConfig mcf = new ClientConfig();
                mcf.saveConfig();
            }
        }
    }

    @Override
    public String getName() {
        return "t";
    }

    @Override
    public String getDesc() {
        return "Toggles modules.";
    }

    @Override
    public String getSyntax() {
        return ".t";
    }

    public String getAll() {
        return getSyntax() + " - " + getDesc();
    }

	@Override
	public String getAlias() {
		return "toggle";
	}
}