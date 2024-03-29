package cc.unknown.module.impl.other;

import java.util.concurrent.atomic.AtomicBoolean;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.LivingUpdateEvent;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.network.PacketEvent.Type;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.Cold;
import net.minecraft.network.play.server.S02PacketChat;

public class AutoLeave extends Module {

    private final SliderValue delay = new SliderValue("Delay", 0, 0, 4000, 50);
    private final AtomicBoolean waiting = new AtomicBoolean(false);
    private final Cold timer = new Cold(0);

    public AutoLeave() {
        super("AutoLeave", ModuleCategory.Other);
        this.registerSetting(delay);
    }

    @Override
    public void onEnable() {
        timer.reset();
    }

    @EventLink
    public void onTick(LivingUpdateEvent event) {
        if (waiting.get() && timer.getTime() >= delay.getInput()) {
            String command = "/salir";
            mc.thePlayer.sendChatMessage(command);

            timer.reset();
            waiting.set(false);
        }
    }

    @EventLink
    public void onPacketReceive(PacketEvent e) {
        if (e.getType() == Type.RECEIVE && e.getPacket() instanceof S02PacketChat) {
            S02PacketChat packet = (S02PacketChat) e.getPacket();
            String message = packet.getChatComponent().getUnformattedText();
            if (message.contains("Deseas salirte de la arena")) {
                waiting.set(true);
                timer.reset();
            }
        }
    }
}