package cc.unknown.command.commands;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.module.impl.Module;
import cc.unknown.utils.misc.KeybindUtil;

public class BindCommand extends Command {

	public BindCommand() {
		super("Bind", "Sets binds for modules", "b", ".bind <module> <key>");
	}

	@Override
	public void onExecute(String[] args) {
		if (args.length != 2) {
			this.sendChat(getColor("Red") + "Syntax Error.");
			return;
		}

		String key = args[0];
		String value = args[1];

		if (key.equals(Haru.instance.getCommandManager().getPrefix())) {
			key = "none";
		}

		Module mod = Haru.instance.getModuleManager().getModule(key);

		if (mod != null) {
			KeybindUtil.instance.bind(mod, KeybindUtil.instance.toInt(value));
			this.sendChat(String.format("Bound %s to %s!", mod.getModuleInfo().name(), value));
			Haru.instance.getConfigManager().save();
		} else {
			this.sendChat(getColor("Red") + "Key or module §cwas not found!", value);
		}
	}
}