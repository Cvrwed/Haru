package cc.unknown.module.impl.other;

import java.util.Arrays;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.event.impl.packet.PacketType;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.AdvancedTimer;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;

public class Autoplay extends Module {
    private final ModeValue mode = new ModeValue("Mode", "Uni", "Hyp", "Uni");
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
    public void onReceive(PacketEvent e) {
        if (e.getType() == PacketType.Receive) {
            if (e.getPacket() instanceof S02PacketChat) {
                handleS02Packet((S02PacketChat) e.getPacket());
            } else if (e.getPacket() instanceof S3BPacketScoreboardObjective) {
                handleS3BPacket((S3BPacketScoreboardObjective) e.getPacket());
            }
        }
    }

    private void handleS02Packet(S02PacketChat packetChat) {
        String chatMessage = packetChat.getChatComponent().getUnformattedText();
        if (Arrays.asList("Jugar de nuevo", "Want to play again?").contains(chatMessage) || chatMessage.contains(mc.thePlayer.getName() + " ha ganado")) {
            waiting = true;
            timer.reset();
        }
    }

    private void handleS3BPacket(S3BPacketScoreboardObjective scoreboard) {
        if (scoreboard.func_149339_c().equals("§l§bSky§6Wars  §l§6Speed") && mode.is("Uni")) {
            if (waiting && timer.getTime() >= delay.getInput()) {
                String command = getCommandByMode();
                if (!command.isEmpty()) {
                    sendRepeatedChatMessages("/skywars random", 4);
                    timer.reset();
                    waiting = false;
                }
            }
        }
    }

    private String getCommandByMode() {
        switch (mode.getMode()) {
            case "Uni Bed":
                return "/bedwars random";
            case "Uni Sw":
                return "";
            case "Hyp Solo Insane":
                return "/play solo_insane";
            case "Hyp Solo Normal":
                return "/play solo_normal";
            default:
                return "";
        }
    }

    private void sendRepeatedChatMessages(String message, int count) {
        for (int i = 0; i < count; i++) {
            mc.thePlayer.sendChatMessage(message);
        }
    }
}
