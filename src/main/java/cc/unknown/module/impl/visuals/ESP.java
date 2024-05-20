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
                            int armorColor = getColor(player.getCurrentArmor(2));
                            if (armorColor > 0) {
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