package cc.unknown.command.commands;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.config.Config;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.util.EnumChatFormatting;

public class ConfigCommand extends Command {

	public ConfigCommand() {
		super("cfg");
	}

	public void listConfigs() {
		PlayerUtil.send(EnumChatFormatting.GREEN + "Available configs: ");
		for (Config config : Haru.instance.getConfigManager().getConfigs()) {
			PlayerUtil.send(EnumChatFormatting.GRAY + config.getName());
		}
	}

	@Override
	public void onExecute(String alias, String[] args) {
		if (Haru.instance.getClientConfig() != null) {
	        Haru.instance.getClientConfig().saveConfig();
	        Haru.instance.getConfigManager().save();
	    }
	    
	    if (args.length == 1) {
	        if (args[0].equalsIgnoreCase("list")) {
	            this.listConfigs();
	        }
	    } else if (args.length == 2) {
	        if (args[0].equalsIgnoreCase("load")) {
	            boolean found = false;
	            for (Config config : Haru.instance.getConfigManager().getConfigs()) {
	                if (config.getName().equalsIgnoreCase(args[1])) {
	                    found = true;
	                    Haru.instance.getConfigManager().setConfig(config);
	                    PlayerUtil.send(EnumChatFormatting.GRAY + " Loaded config!");
	                    break;
	                }
	            }
	            
	            if (!found) {
	                PlayerUtil.send(EnumChatFormatting.RED + " Unable to find a config with the name " + args[1]);
	            }

	        } else if (args[0].equalsIgnoreCase("save")) {
	            Haru.instance.getConfigManager().copyConfig(Haru.instance.getConfigManager().getConfig(),
	                    args[1] + ".json");

	            PlayerUtil.send(EnumChatFormatting.GRAY + " Saved as " + args[1] + "!");
	            Haru.instance.getConfigManager().discoverConfigs();

	        } else if (args[0].equalsIgnoreCase("remove")) {
	            boolean found = false;
	            for (Config config : Haru.instance.getConfigManager().getConfigs()) {
	                if (config.getName().equalsIgnoreCase(args[1])) {
	                    Haru.instance.getConfigManager().deleteConfig(config);
	                    found = true;
	                    PlayerUtil.send(EnumChatFormatting.GRAY + " Removed " + args[1] + " successfully!");
	                    break;
	                }
	            }
	            
	            if (!found) {
	                PlayerUtil.send(EnumChatFormatting.RED + " Failed to delete " + args[1]);
	            }
	        } else {
	            PlayerUtil.send(EnumChatFormatting.RED + " Syntax Error.");
	            PlayerUtil.send(EnumChatFormatting.RED + " Use: .config remove <config name>");
	        }
	    }
	}
}
