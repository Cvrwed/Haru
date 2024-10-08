package cc.unknown.command.commands;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.module.impl.Module;
import net.minecraft.util.EnumChatFormatting;

public class ToggleCommand extends Command {

    public ToggleCommand() {
		super("Toggle", "Toggles modules", "t", ".toggle");
	}

	@Override
    public void onExecute(String[] args) {
        if (args.length != 1) {
        	this.sendChat(getColor("Gray") + " " + getAll());
        } else {
            String module = args[0];
            Module mod = Haru.instance.getModuleManager().getModule(module);
            if (mod == null) {
            	this.sendChat(getColor("Red") + " Module not found!");
            } else {
            	Haru.instance.getModuleManager().getModule(module).toggle();
                this.sendChat(getColor("White") + " %s " + getColor("Gray") + "%s", Haru.instance.getModuleManager().getModule(module).getModuleInfo().name(), Haru.instance.getModuleManager().getModule(module).isEnabled() ? EnumChatFormatting.GREEN + "enabled": getColor("Red") + "disabled.");
                Haru.instance.getConfigManager().save();
            }
        }
    }

    public String getAll() {
        return syntax + " - " + desc;
    }
}