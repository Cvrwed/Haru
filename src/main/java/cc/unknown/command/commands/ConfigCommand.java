package cc.unknown.command.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
	public void execute(String[] args) {
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
	                    args[1] + ".haru");

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

	@Override
	public ArrayList<String> autocomplete(int arg, String[] args) {
		if (args.length == 0)
			return new ArrayList<>();

		switch (args.length) {
		case 1:
			List<String> options = new ArrayList<>();
			options.add("load");
			options.add("list");
			return filterOptions((ArrayList<String>) options, args[0]);
		case 2:
			switch (args[0].toLowerCase()) {
			case "remove":
			case "save":
			case "load":
				return filterFiles(args[1]);
			}
		}
		return new ArrayList<>();
	}

	private ArrayList<String> filterOptions(ArrayList<String> options, String arg) {
		ArrayList<String> filtered = new ArrayList<>();
		for (String option : options) {
			if (option.toLowerCase().startsWith(arg.toLowerCase())) {
				filtered.add(option);
			}
		}
		return filtered;
	}

	private ArrayList<String> filterFiles(String arg) {
		ArrayList<String> fileNames = new ArrayList<>();
		for (File file : Objects.requireNonNull(Haru.instance.getConfigManager().configDirectory.listFiles())) {
			if (file.isFile()) {
				String name = file.getName();
				if (name.toLowerCase().endsWith(".json")) {
					name = name.substring(0, name.length() - 5);
				}
				if (name.toLowerCase().startsWith(arg.toLowerCase())) {
					fileNames.add(name);
				}
			}
		}
		return fileNames;
	}
}
