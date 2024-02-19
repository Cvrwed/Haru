package cc.unknown.module.impl.visuals;

import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.DescValue;

public class Animations extends Module {
	
	public BooleanValue block = new BooleanValue("Block", true);
	public BooleanValue consumible = new BooleanValue("Eat/Drink", true);
	public BooleanValue bow = new BooleanValue("Bow", true);
	public BooleanValue rod = new BooleanValue("Rod", true);
	
	public Animations() {
		super("Animations", ModuleCategory.Visuals);
		this.registerSetting(new DescValue("1.7 Animations"));
		this.registerSetting(block, consumible, bow, rod);
	}
    
    
}