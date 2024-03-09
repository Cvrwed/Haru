package cc.unknown.module.impl.other;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.UpdateEvent;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.AdvancedTimer;
import net.minecraft.network.play.server.S02PacketChat;

public class AutoLeave extends Module {

    private final SliderValue delay = new SliderValue("Delay", 0, 0, 4000, 50);
    private final AdvancedTimer timer = new AdvancedTimer(0);
    private boolean waiting;

    public AutoLeave() {
        super("AutoLeave", ModuleCategory.Other);
        this.registerSetting(delay);
    }

    @Override
    public void onEnable() {
        waiting = false;
        timer.reset();
    }

    @EventLink
    public void onTick(UpdateEvent event) {
        if (waiting && timer.getTime() >= delay.getInput()) {
            String command = "/salir";
            mc.thePlayer.sendChatMessage(command);

            timer.reset();
            waiting = false;
        }
    }

    @EventLink
    public void onPacketReceive(PacketEvent e) {
    	if (e.isReceive()) {
	    	if (e.getPacket() instanceof S02PacketChat) {
	    		S02PacketChat packet = (S02PacketChat) e.getPacket();
	    		String message = packet.getChatComponent().getUnformattedText();
	    		if (message.contains("Deseas salirte de la arena")) {
	    			waiting = true;
	    			timer.reset();
	    		}
	    	}
    	}
    }
}
