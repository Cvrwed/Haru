package cc.unknown.module.impl.visuals;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.render.RenderEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.SliderValue;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

@Register(name = "Ambience", category = Category.Visuals)
public class Ambience extends Module {

	private SliderValue time = new SliderValue("Time", 18000, 0, 24000, 500);

	public Ambience() {
		this.registerSetting(time);
	}

	@EventLink
	public void onRender3D(RenderEvent e) {
		if (e.is3D()) {
			mc.theWorld.setWorldTime(time.getInputToLong());
		}
	}

	@EventLink
	public void onReceive(PacketEvent e) {
		if (e.isReceive()) {
			if (e.getPacket() instanceof S03PacketTimeUpdate) {
				e.setCancelled(true);
			}
		}
	}
}
