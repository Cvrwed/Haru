package cc.unknown.module.impl.visuals;

import cc.unknown.event.impl.api.EventLink;
import cc.unknown.event.impl.move.PreUpdateEvent;
import cc.unknown.event.impl.packet.PacketEvent;
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
	public void onRenderWorldLast(Render3DEvent h) {
		mc.theWorld.setWorldTime((long)(time.getInputToInt()) * 1000L);	
	}
	
	@EventLink
	public void onUpdate(PreUpdateEvent e) {
		switch (mode.getMode()) {
			case "Clear":
				mc.theWorld.setRainStrength(0.0F);
				mc.theWorld.setThunderStrength(0.0F);
				break;
			case "Rain":
				mc.theWorld.setRainStrength(1.0F);
				mc.theWorld.setThunderStrength(0.0F);
				break;
			case "Thunder":
				mc.theWorld.setRainStrength(1.0F);
				mc.theWorld.setThunderStrength(1.0F);
				break;
		}
	}
	
	@EventLink
	public void onPacketReceive(PacketEvent e) {
		if (e.isReceive()) {
			if (e.getPacket() instanceof S03PacketTimeUpdate) {
				e.setCancelled(true);
			}
	
			if (e.getPacket() instanceof S2BPacketChangeGameState) {
				S2BPacketChangeGameState S2BPacket = (S2BPacketChangeGameState) e.getPacket();
				if (S2BPacket.getGameState() == 7 || S2BPacket.getGameState() == 8) {
					e.setCancelled(true);
				}
			}
		}
	}

}
