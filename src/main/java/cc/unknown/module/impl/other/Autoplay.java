package cc.unknown.module.impl.other;

import java.util.Arrays;
import java.util.stream.IntStream;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.UpdateEvent;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.AdvancedTimer;
import net.minecraft.network.play.server.S02PacketChat;


public class Autoplay extends Module {

    private final ModeValue mode = new ModeValue("Mode", "Uni Bed", "Uni Bed", "Uni Sw", "Hyp Solo Insane", "Hyp Solo Normal");
    private final SliderValue delay = new SliderValue("Delay", 1500, 0, 4000, 50);
    private final AdvancedTimer timer = new AdvancedTimer(0);

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
            String command = "";
            
            if (mode.is("Uni Bed")) command = "/bedwars random";
            else if (mode.is("Uni Sw")) command = "/skywars random";
            else if (mode.is("Hyp Solo Insane")) command = "/play solo_insane";
            else if (mode.is("Hyp Solo Normal")) command = "/play solo_normal";
            
            mc.thePlayer.sendChatMessage(command);
            timer.reset();
            waiting = false;
        }
    }

    @EventLink
    public void onReceive(PacketEvent e) {
        if (e.isReceive() && e.getPacket() instanceof S02PacketChat) {
            if (Arrays.asList("Ha ganado".getBytes(), "Want to play again?".getBytes()).stream().anyMatch(word -> subArray(((S02PacketChat) e.getPacket()).getChatComponent().getUnformattedText().getBytes(), word))) {
                waiting = true;
                timer.reset();
            }
        }
    }
    
    private boolean subArray(byte[] s, byte[] t) {
        return IntStream.range(0, s.length - t.length + 1).anyMatch(i -> IntStream.range(0, t.length).allMatch(j -> s[i + j] == t[j]));
    }
}

