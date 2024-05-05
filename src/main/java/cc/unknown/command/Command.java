package cc.unknown.command;

import java.util.HashMap;
import java.util.Map;

import cc.unknown.utils.Loona;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.util.EnumChatFormatting;

public abstract class Command implements Loona {
	
    private static final Map<String, EnumChatFormatting> colorMap = new HashMap<>();

    static {
        colorMap.put("DarkAqua", EnumChatFormatting.DARK_AQUA);
        colorMap.put("Green", EnumChatFormatting.GREEN);
        colorMap.put("White", EnumChatFormatting.WHITE);
        colorMap.put("Red", EnumChatFormatting.RED);
        colorMap.put("Gold", EnumChatFormatting.GOLD);
        colorMap.put("Gray", EnumChatFormatting.GRAY);
        colorMap.put("Yellow", EnumChatFormatting.YELLOW);
        colorMap.put("Blue", EnumChatFormatting.BLUE);
    }
	
    public abstract void onExecute(String[] args);
    
    final Flips flips = getClass().getAnnotation(Flips.class);
    public final String name = flips.name();
    public final String desc = flips.desc();
    public final String alias = flips.alias();
    public final String syntax = flips.alias();
    
    public String getColor(String colorName) {
        EnumChatFormatting color = colorMap.getOrDefault(colorName, EnumChatFormatting.RESET);
        return color.toString();
    }
    
    public void sendChat(Object text, Object... text2) {
		String format = String.format(text.toString(), text2);
    	PlayerUtil.send(format);
    }
    
    public void clearChat() {
        mc.ingameGUI.getChatGUI().clearChatMessages();
    }
}