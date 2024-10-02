package cc.unknown.module.impl.visuals;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.PreMotionEvent;
import cc.unknown.event.impl.netty.PacketEvent;
import cc.unknown.event.impl.render.RenderEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.ModuleInfo;
import cc.unknown.module.setting.impl.SliderValue;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S2BPacketChangeGameState;

@ModuleInfo(name = "Ambience", category = Category.Visuals)
public class Ambience extends Module {

	private SliderValue time = new SliderValue("Time", 18000, 0, 24000, 500);

	public Ambience() {
		this.registerSetting(time);
	}

	@EventLink
	public void onRender3D(RenderEvent event) {
		if (event.is3D()) {
			mc.theWorld.setWorldTime(time.getInputToLong());
		}
	}
	
	@EventLink
	public void onPreMotion(PreMotionEvent event) {
		mc.theWorld.setRainStrength(0);
		mc.theWorld.getWorldInfo().setCleanWeatherTime(Integer.MAX_VALUE);
		mc.theWorld.getWorldInfo().setRainTime(0);
		mc.theWorld.getWorldInfo().setThunderTime(0);
		mc.theWorld.getWorldInfo().setRaining(false);
		mc.theWorld.getWorldInfo().setThundering(false);
	}

	@EventLink
	public void onReceive(PacketEvent event) {
		if (event.getPacket() instanceof S03PacketTimeUpdate) {
			event.setCancelled(true);
		} else if (event.getPacket() instanceof S2BPacketChangeGameState) {
			S2BPacketChangeGameState wrapper = (S2BPacketChangeGameState) event.getPacket();

			if (wrapper.getGameState() == 1 || wrapper.getGameState() == 2) {
				event.setCancelled(true);
			}
		}
	}
}
