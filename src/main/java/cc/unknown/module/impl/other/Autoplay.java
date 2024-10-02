package cc.unknown.module.impl.other;

import java.util.concurrent.atomic.AtomicReference;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.PreMotionEvent;
import cc.unknown.event.impl.netty.PacketEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.ModuleInfo;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.Cold;
import net.minecraft.network.play.server.S02PacketChat;

@ModuleInfo(name = "AutoPlay", category = Category.Other)
public class Autoplay extends Module {

    private final ModeValue mode = new ModeValue("Mode", "Uni Bed", "Uni Bed", "Uni Sw", "Hyp Solo Insane", "Hyp Solo Normal");
    private final SliderValue delay = new SliderValue("Delay", 1500, 0, 4000, 50);
    private final Cold timer = new Cold(0);
    private final AtomicReference<String> sender = new AtomicReference<>("");
    private final AtomicReference<String> command = new AtomicReference<>("");

    public Autoplay() {
        this.registerSetting(mode, delay);
    }

    @Override
    public void onEnable() {
    	sender.set("");
        timer.reset();
    }

    @EventLink
    public void onUpdate(PreMotionEvent event) {
        if (!sender.get().isEmpty() && timer.getTime() >= delay.getInput()) {
            String cmd = command.get();
            if (!cmd.isEmpty()) {
                mc.thePlayer.sendChatMessage(cmd);
                timer.reset();
                sender.set("");
            }
        }
    }

    @EventLink
    public void onPacket(PacketEvent e) {
        if (e.getPacket() instanceof S02PacketChat) {
            S02PacketChat wrapper = (S02PacketChat) e.getPacket();
            String message = wrapper.getChatComponent().getUnformattedText();
            
            if (wrapper.getType() == (byte) 1) {
                if (containsAny(message, "Jugar de nuevo", "ha ganado", "Want to play again?")) {
                	sender.set(message);
                    command.set(getCommand());
                    timer.reset();
                }
            }
        }
    }

    private String getCommand() {
        if (mode.is("Uni Bed")) {
            return "/bedwars random";
        } else if (mode.is("Uni Sw")) {
            return "/skywars random";
        } else if (mode.is("Hyp Solo Insane")) {
            return "/play solo_insane";
        } else if (mode.is("Hyp Solo Normal")) {
            return "/play solo_normal";
        } else {
            return "";
        }
    }

    private boolean containsAny(String source, String... targets) {
        for (String target : targets) {
            if (source.contains(target)) {
                return true;
            }
        }
        return false;
    }
}