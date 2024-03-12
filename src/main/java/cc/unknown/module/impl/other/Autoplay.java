package cc.unknown.module.impl.other;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.UpdateEvent;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;

public class Autoplay extends Module {

	private final ModeValue mode = new ModeValue("Mode", "Bed", "Uni", "Hyp");
	private final SliderValue delay = new SliderValue("Delay", 1500, 0, 4000, 50);

	public Autoplay() {
		super("Autoplay", ModuleCategory.Other);
		this.registerSetting(mode, delay);
	}

    @Override
    public void onEnable() {

    }

    @EventLink
    public void onUpdate(UpdateEvent event) {

    }

    @EventLink
    public void onReceive(PacketEvent e) {

    }
        
}
