package cc.unknown.module.impl.other;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.UpdateEvent;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.Cold;
import net.minecraft.network.play.server.S02PacketChat;


public class Autoplay extends Module {

    private final ModeValue mode = new ModeValue("Mode", "Uni Bed", "Uni Bed", "Uni Sw", "Hyp Solo Insane", "Hyp Solo Normal");
    private final SliderValue delay = new SliderValue("Delay", 1500, 0, 4000, 50);
    private final Cold timer = new Cold();
    private boolean waiting;

    public Autoplay() {
        super("Autoplay", ModuleCategory.Other);
        this.registerSetting(mode, delay);
    }

    @Override
    public void onEnable() {
        waiting = false;
        timer.reset();
    }

    @EventLink
    public void onUpdate(UpdateEvent event) {
        if (waiting && timer.getTime() >= delay.getInput()) {
            String command = mode.is("Uni Bed") ? "/bedwars random" : mode.is("Uni Sw") ? "/skywars random" : mode.is("Hyp Solo Insane") ? "/play solo_insane" : mode.is("Hyp Solo Normal") ? "/play solo_normal" : "";
            if (!command.isEmpty()) {
                mc.thePlayer.sendChatMessage(command);
                timer.reset();
                waiting = false;
            }
        }
    }

    @EventLink
    public void onReceive(PacketEvent e) {
        if (e.isReceive() && e.getPacket() instanceof S02PacketChat) {
            byte[] chatBytes = ((S02PacketChat) e.getPacket()).getChatComponent().getUnformattedText().getBytes();
            if (containsAny(chatBytes, "Jugar de nuevo".getBytes(), "ha ganado".getBytes(), "Want to play again?".getBytes())) {
                waiting = true;
                timer.reset();
            }
        }
    }
    
    private boolean containsAny(byte[] source, byte[]... targets) {
        for (byte[] target : targets) {
            if (subArray(source, target)) {
                return true;
            }
        }
        return false;
    }

    private boolean subArray(byte[] source, byte[] target) {
        for (int i = 0; i <= source.length - target.length; i++) {
            boolean found = true;
            for (int j = 0; j < target.length; j++) {
                if (source[i + j] != target[j]) {
                    found = false;
                    break;
                }
            }
            if (found) {
                return true;
            }
        }
        return false;
    }
}

