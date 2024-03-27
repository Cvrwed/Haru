package cc.unknown.module.impl.other;

import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;

public class AutoChest extends Module {

	private BooleanValue iron = new BooleanValue("Iron check", false);
	private BooleanValue gold = new BooleanValue("Gold check", true);
	private BooleanValue dia = new BooleanValue("Diamond check", false);
	private BooleanValue eme = new BooleanValue("Emerald check", true);

	public AutoChest() {
		super("AutoChest", ModuleCategory.Other);
		this.registerSetting(iron, gold, dia, eme);
		
	}
}
