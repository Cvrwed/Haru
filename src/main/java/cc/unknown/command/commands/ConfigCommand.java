package cc.unknown.command.commands;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.config.Config;
import cc.unknown.utils.player.PlayerUtil;

public class ConfigCommand extends Command {

	@Override
	public void onExecute(String[] args) {
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
	                    PlayerUtil.send(getGray() + " Loaded config!");
	                    break;
	                }
	            }
	            
	            if (!found) {
	                PlayerUtil.send(getRed() + " Unable to find a config with the name " + args[1]);
	            }

	        } else if (args[0].equalsIgnoreCase("save")) {
	            Haru.instance.getConfigManager().copyConfig(Haru.instance.getConfigManager().getConfig(),
	                    args[1] + ".haru");

	            PlayerUtil.send(getGray() + " Saved as " + args[1] + "!");
	            Haru.instance.getConfigManager().discoverConfigs();

	        } else if (args[0].equalsIgnoreCase("remove")) {
	            boolean found = false;
	            for (Config config : Haru.instance.getConfigManager().getConfigs()) {
	                if (config.getName().equalsIgnoreCase(args[1])) {
	                    Haru.instance.getConfigManager().deleteConfig(config);
	                    found = true;
	                    PlayerUtil.send(getGray() + " Removed " + args[1] + " successfully!");
	                    break;
	                }
	            }
	            
	            if (!found) {
	                PlayerUtil.send(getRed() + " Failed to delete " + args[1]);
	            }
	        } else {
	            PlayerUtil.send(getRed() + " Syntax Error.");
	            PlayerUtil.send(getRed() + " Use: .config remove <config name>");
	        }
	    }
	}
	
	@Override
	public String getName() {
		return "cfg";
	}

	@Override
	public String getSyntax() {
		return ".cfg save <name>";
	}

	@Override
	public String getDesc() {
		return "Save or load ur config";
	}
	
	private void listConfigs() {
		PlayerUtil.send(getGreen() + " Available configs: ");
		for (Config config : Haru.instance.getConfigManager().getConfigs()) {
			PlayerUtil.send(" " + getGray() + config.getName());
		}
	}
}
