package cc.unknown.command;

import cc.unknown.utils.Loona;
import net.minecraft.util.EnumChatFormatting;

public abstract class Command implements Loona {
    public abstract void onExecute(String[] args);
    public abstract String getName();
    public abstract String getSyntax();
    public abstract String getDesc();
    
    public String getDarkAqua() {
        return EnumChatFormatting.DARK_AQUA.toString();
    }

    public String getGreen() {
        return EnumChatFormatting.GREEN.toString();
    }
    
    public String getWhite() {
    	return EnumChatFormatting.WHITE.toString();
    }
    
    public String getRed() {
        return EnumChatFormatting.RED.toString();
    }

    public String getGold() {
        return EnumChatFormatting.GOLD.toString();
    }
    
    public String getGray() {
    	return EnumChatFormatting.GRAY.toString();
    }
    
    public String getYellow() {
    	return EnumChatFormatting.YELLOW.toString();
    }

    public String getBlue() {
        return EnumChatFormatting.BLUE.toString();
    }

}