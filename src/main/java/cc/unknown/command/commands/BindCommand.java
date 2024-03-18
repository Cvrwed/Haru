package cc.unknown.command.commands;

import java.util.ArrayList;
import java.util.stream.Collectors;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.config.ClientConfig;
import cc.unknown.module.Module;
import cc.unknown.utils.misc.KeybindUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.util.EnumChatFormatting;

public class BindCommand extends Command {
	
    public BindCommand() {
        super("bind");
    }
    
	@Override
	public void execute(String[] args) {
		if (args.length == 2) {
            String key = args[0];
            String value = args[1];
            Module mod = Haru.instance.getModuleManager().getModule(key);
            if (mod == null) {
            	PlayerUtil.send(EnumChatFormatting.RED + " Key or module §cwas not found!", value);
            } else {
            	KeybindUtil.bind(mod, KeybindUtil.toInt(value));
            	PlayerUtil.send(String.format("Bound %s to %s!", mod.getName(), value));
                ClientConfig moduleConfig = new ClientConfig();
                moduleConfig.saveConfig();
            }
        } else {
        	PlayerUtil.send(EnumChatFormatting.RED + " Syntax Error.");
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
        } else if (arg == 2) {
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add("none");
            return arrayList;
        } else return new ArrayList<>();
    }
}