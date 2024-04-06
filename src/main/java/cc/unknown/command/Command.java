package cc.unknown.command;

import java.util.HashMap;
import java.util.Map;

import cc.unknown.utils.Loona;
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
    public abstract String getName();
    public abstract String getAlias();
    public abstract String getSyntax();
    public abstract String getDesc();
    
    public String getColor(String colorName) {
        EnumChatFormatting color = colorMap.getOrDefault(colorName, EnumChatFormatting.RESET);
        return color.toString();
    }
    
    public void clearChat() {
        mc.ingameGUI.getChatGUI().clearChatMessages();
    }

}