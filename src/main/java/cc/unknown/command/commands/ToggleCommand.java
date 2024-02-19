package cc.unknown.command.commands;

import java.util.ArrayList;
import java.util.stream.Collectors;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.config.ClientConfig;
import cc.unknown.module.Module;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.util.EnumChatFormatting;

public class ToggleCommand extends Command {

    public ToggleCommand() {
		super("toggle", "t");
	}

    @Override
    public void execute(String alias, String[] args) {
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

    @Override
    public ArrayList<String> autocomplete(int arg, String[] args) {
        String prefix = "";
        boolean flag = false;

        if (arg == 0 || args.length == 0) {
            flag = true;
        } else if (arg == 1) {
            flag = true;
            prefix = args[0];
        }

        if (flag) {
            String finalPrefix = prefix;
            return (ArrayList<String>) Haru.instance.getModuleManager().getModule().stream().filter(mod -> mod.getName().toLowerCase().startsWith(finalPrefix)).map(Module::getName).collect(Collectors.toList());
        } else return new ArrayList<>();
    }
}