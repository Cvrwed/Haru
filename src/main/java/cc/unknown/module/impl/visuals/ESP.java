package cc.unknown.module.impl.visuals;

import java.awt.Color;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.render.RenderEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.ui.clickgui.raven.impl.api.Theme;
import cc.unknown.utils.client.RenderUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;

@Register(name = "ESP", category = Category.Visuals)
public class ESP extends Module {

    private ModeValue boxMode = new ModeValue("Box Mode", "2D", "2D", "3D", "Health");
    private ModeValue renderMode = new ModeValue("Render Mode", "Player", "Player", "Chest", "Both");
    private BooleanValue enablePlayerColor = new BooleanValue("Enable Player Color", false);
    private SliderValue playerColorHSB = new SliderValue("Player Color [H/S/B]", 0, 0, 350, 10);
    private BooleanValue enableChestColor = new BooleanValue("Enable Chest Color", false);
    private SliderValue chestColorHSB = new SliderValue("Chest Color [H/S/B]", 0, 0, 350, 10);
    private BooleanValue checkInvisibility = new BooleanValue("Check Invisibility", true);
    private BooleanValue checkTeams = new BooleanValue("Check Teams", true);
    private BooleanValue disableIfChestOpened = new BooleanValue("Disable if Chest Opened", false);

    public ESP() {
        this.registerSetting(boxMode, renderMode, enablePlayerColor, playerColorHSB, enableChestColor, chestColorHSB, 
                checkInvisibility, checkTeams, disableIfChestOpened);
    }
    
    @EventLink
    public void onRender(RenderEvent e) {
        if (PlayerUtil.inGame() && e.is3D()) {
            int playerColorRGB = enablePlayerColor.isToggled() ? Color.getHSBColor((playerColorHSB.getInputToFloat() % 360) / 360.0f, 1.0f, 1.0f).getRGB() : Theme.instance.getMainColor().getRGB();
            int chestColorRGB = enableChestColor.isToggled() ? Color.getHSBColor((chestColorHSB.getInputToFloat() % 360) / 360.0f, 1.0f, 1.0f).getRGB() : Theme.instance.getMainColor().getRGB();
            
            if (renderMode.is("Player") || renderMode.is("Both")) {
                for (EntityPlayer player : mc.theWorld.playerEntities) {
                    if (player != mc.thePlayer && player.deathTime == 0 && (checkInvisibility.isToggled() || !player.isInvisible())) {
                        int renderColor = playerColorRGB;
                    	if (checkTeams.isToggled()) {
                            int nameColor = getNameColor(player);
                            int armorColor = getColor(player.getCurrentArmor(2));
                            if (nameColor > 0) {
                                renderColor = nameColor;
                            } else if (armorColor > 0) {
                                renderColor = armorColor;
                            }
                        }
                        renderPlayer(player, renderColor);
                    }
                }
            }

            if (renderMode.is("Chest") || renderMode.is("Both")) {
                for (TileEntity te : mc.theWorld.loadedTileEntityList) {
                    if (te instanceof TileEntityChest || te instanceof TileEntityEnderChest) {
                        if (disableIfChestOpened.isToggled() && ((TileEntityChest) te).lidAngle > 0.0f) continue;
                        
                        RenderUtil.drawChestBox(te.getPos(), chestColorRGB, true);
                    }
                }
            }
        }
    }

    private int getColorFromCode(char code) {
        switch (code) {
            case '0': return Color.BLACK.getRGB();
            case '1': return new Color(0, 0, 170).getRGB(); // Dark Blue
            case '2': return new Color(0, 170, 0).getRGB(); // Dark Green
            case '3': return new Color(0, 170, 170).getRGB(); // Dark Aqua
            case '4': return new Color(170, 0, 0).getRGB(); // Dark Red
            case '5': return new Color(170, 0, 170).getRGB(); // Dark Purple
            case '6': return new Color(255, 170, 0).getRGB(); // Gold
            case '7': return Color.GRAY.getRGB();
            case '8': return Color.DARK_GRAY.getRGB();
            case '9': return new Color(85, 85, 255).getRGB(); // Blue
            case 'a': return new Color(85, 255, 85).getRGB(); // Green
            case 'b': return new Color(85, 255, 255).getRGB(); // Aqua
            case 'c': return new Color(255, 85, 85).getRGB(); // Red
            case 'd': return new Color(255, 85, 255).getRGB(); // Light Purple
            case 'e': return new Color(255, 255, 85).getRGB(); // Yellow
            case 'f': return Color.WHITE.getRGB();
            default: return -1;
        }
    }

    private int getColor(ItemStack stack) {
        if (stack == null) return -1;
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null) {
            NBTTagCompound nbt = tag.getCompoundTag("display");
            if (nbt != null && nbt.hasKey("color", 3)) {
                return nbt.getInteger("color");
            }
        }
        return -2;
    }
	
	private int getNameColor(EntityPlayer player) {
	    if (player.getTeam() != null) {
	        String displayName = player.getDisplayName().getFormattedText();
	        if (displayName.length() > 1 && displayName.charAt(0) == '§') {
	            char colorCode = displayName.charAt(1);
	            return getColorFromCode(colorCode);
	        }
	    }
	    return -1;
	}

	private void renderPlayer(Entity target, int rgb) {
		switch (boxMode.getMode()) {
		case "3D":
			RenderUtil.drawBoxAroundEntity(target, 1, 0.0D, 0.0D, rgb, false);
			break;
		case "2D":
			RenderUtil.drawBoxAroundEntity(target, 3, 0.0D, 0.0D, rgb, false);
			break;
		case "Health":
			RenderUtil.drawBoxAroundEntity(target, 4, 0.0D, 0.0D, rgb, false);
			break;
		}
	}
}