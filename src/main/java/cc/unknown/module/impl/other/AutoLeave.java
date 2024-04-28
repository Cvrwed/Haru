package cc.unknown.module.impl.other;

import java.util.concurrent.atomic.AtomicBoolean;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.LivingEvent;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.Cold;
import net.minecraft.network.play.server.S02PacketChat;

@Register(name = "AutoLeave", category = Category.Other)
public class AutoLeave extends Module {

	private ModeValue mode = new ModeValue("Mode", "/salir", "/salir");
    private final SliderValue delay = new SliderValue("Delay", 0, 0, 4000, 50);
    private final AtomicBoolean waiting = new AtomicBoolean(false);
    private final Cold timer = new Cold(0);

    public AutoLeave() {
        this.registerSetting(mode, delay);
    }

    @Override
    public void onEnable() {
        timer.reset();
    }

    @EventLink
    public void onTick(LivingEvent event) {
        if (waiting.get() && timer.getTime() >= delay.getInput()) {
            mc.thePlayer.sendChatMessage(mode.getMode());
            timer.reset();
            waiting.set(false);
        }
    }

    @EventLink
    public void onPacket(PacketEvent e) {
        if (e.isReceive() && e.getPacket() instanceof S02PacketChat) {
            S02PacketChat packet = (S02PacketChat) e.getPacket();
            String message = packet.getChatComponent().getUnformattedText();
            if (message.contains("Deseas salirte de la arena")) {
                waiting.set(true);
                timer.reset();
            }
        }
    }
}