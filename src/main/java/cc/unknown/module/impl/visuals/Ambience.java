package cc.unknown.module.impl.visuals;

import java.util.HashMap;
import java.util.Map;

import cc.unknown.event.impl.api.EventLink;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S2BPacketChangeGameState;

public class Ambience extends Module {
	private ModeValue mode = new ModeValue("Weather", "Clear", "Clear", "Rain", "Thunder");
	private SliderValue time = new SliderValue("Time", 14.0, 0.0, 24.0, 1.0);
	
	public Ambience() {
		super("Ambience", ModuleCategory.Visuals);
	      this.registerSetting(mode, time);
	}
	
	@EventLink
	public void onRender3D(Render3DEvent h) {
	    mc.theWorld.setWorldTime(time.getInputToInt() * 1000L);	
	}
	
	@EventLink
	public void onTick(TickEvent e) {
	    Map<String, Runnable> x = new HashMap<>();
	    x.put("Clear", () -> setWeather(0.0F, 0.0F));
	    x.put("Rain", () -> setWeather(1.0F, 0.0F));
	    x.put("Thunder", () -> setWeather(1.0F, 1.0F));
	    x.get(mode.getMode());
	}
	
	@EventLink
	public void onPacketReceive(PacketEvent e) {
	    if (e.isReceive()) {
	        e.setCancelled(e.getPacket() instanceof S03PacketTimeUpdate || (e.getPacket() instanceof S2BPacketChangeGameState && (((S2BPacketChangeGameState) e.getPacket()).getGameState() == 7 || ((S2BPacketChangeGameState) e.getPacket()).getGameState() == 8 )));
	    }
	}

	private void setWeather(float rainStrength, float thunderStrength) {
	    mc.theWorld.setRainStrength(rainStrength);
	    mc.theWorld.setThunderStrength(thunderStrength);
	}
}
