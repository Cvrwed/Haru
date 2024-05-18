package cc.unknown.command.commands;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.command.Flips;
import cc.unknown.config.Config;

@Flips(name = "Config", alias = "cfg", desc = "Save or load ur config", syntax = ".cfg save <name>")
public class ConfigCommand extends Command {

	@Override
	public void onExecute(String[] args) {
		if (Haru.instance.getHudConfig() != null) {
	        Haru.instance.getHudConfig().saveHud();
	        Haru.instance.getConfigManager().save();
	    }
	    
	    if (args.length == 1) {
	        if (args[0].equalsIgnoreCase("list")) {
	            this.listConfigs();
	        } else if (args[0].equalsIgnoreCase("folder") || args[0].equalsIgnoreCase("open")) {
                try {
                    Desktop desktop = Desktop.getDesktop();
                    File dirToOpen = new File(String.valueOf(Haru.instance.getConfigManager().configDirectory));
                    desktop.open(dirToOpen);
                } catch (IllegalArgumentException | IOException ex) {
                }
	        }
	    } else if (args.length == 2) {
	        if (args[0].equalsIgnoreCase("load")) {
	            boolean found = false;
	            for (Config config : Haru.instance.getConfigManager().getConfigs()) {
	                if (config.getName().equalsIgnoreCase(args[1])) {
	                    found = true;
	                    Haru.instance.getConfigManager().setConfig(config);
	                    this.sendChat(getColor("Gray") + " Loaded config!");
	                    break;
	                }
	            }
	            
	            if (!found) {
	                this.sendChat(getColor("Red") + " Unable to find a config with the name " + args[1]);
	            }

	        } else if (args[0].equalsIgnoreCase("save")) {
	            Haru.instance.getConfigManager().copyConfig(Haru.instance.getConfigManager().getConfig(),
	                    args[1] + ".haru");

	            this.sendChat(getColor("Gray") + " Saved as " + args[1] + "!");
	            Haru.instance.getConfigManager().discoverConfigs();
	            
	        } else if (args[0].equalsIgnoreCase("remove")) {
	            boolean found = false;
	            for (Config config : Haru.instance.getConfigManager().getConfigs()) {
	                if (config.getName().equalsIgnoreCase(args[1])) {
	                    Haru.instance.getConfigManager().deleteConfig(config);
	                    found = true;
	                    this.sendChat(getColor("Gray") + " Removed " + args[1] + " successfully!");
	                    break;
	                }
	            }
	            
	            if (!found) {
	                this.sendChat(getColor("Red") + " Failed to delete " + args[1]);
	            }
	        } else {
	            this.sendChat(getColor("Red") + " Syntax Error.");
	            this.sendChat(getColor("Red") + " Use: .config remove <config name>");
	        }
	    }
	}
	
	private void listConfigs() {
		this.sendChat(getColor("Green") + " Available configs: ");
		for (Config config : Haru.instance.getConfigManager().getConfigs()) {
			this.sendChat(" " + getColor("Gray") + config.getName());
		}
	}
}
