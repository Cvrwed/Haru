package cc.unknown.command.commands;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.config.ClientConfig;
import cc.unknown.module.Module;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.util.EnumChatFormatting;

public class ToggleCommand extends Command {

    public ToggleCommand() {
		super("t");
	}

    @Override
    public void onExecute(String alias, String[] args) {
        if (args.length == 1) {
            String module = args[0];
            Module mod = Haru.instance.getModuleManager().getModule(module);
            if (mod == null) {
            	PlayerUtil.send(EnumChatFormatting.RED + " Module not found!");
            } else {
            	Haru.instance.getModuleManager().getModule(module).toggle();
                PlayerUtil.send(EnumChatFormatting.WHITE + " %s " + EnumChatFormatting.GRAY + "%s", Haru.instance.getModuleManager().getModule(module).getName(), Haru.instance.getModuleManager().getModule(module).isEnabled() ? EnumChatFormatting.GREEN + "enabled": EnumChatFormatting.RED + "disabled.");
                ClientConfig mcf = new ClientConfig();
                mcf.saveConfig();
            }
        }
    }
}