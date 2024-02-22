package cc.unknown.module.impl.visuals;

import java.awt.Color;

import cc.unknown.event.impl.api.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.impl.other.AntiBot;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.RenderUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;

public class ESP extends Module {
	private ModeValue mode = new ModeValue("ESP Types", "2D", "2D", "Health", "Box");
	private SliderValue color = new SliderValue("Color [H/S/B]", 0, 0, 350, 10);
	private BooleanValue chestESP = new BooleanValue("ChestESP", false);
	private BooleanValue gay = new BooleanValue("Rainbow", false);
	private BooleanValue invi = new BooleanValue("Show invis", true);
	private BooleanValue tim = new BooleanValue("Color team", true);
	private BooleanValue dmg = new BooleanValue("Red on damage", true);

	public ESP() {
		super("ESP", ModuleCategory.Visuals);
		this.registerSetting(mode, color, chestESP, gay, invi, dmg, tim);
	}

	@EventLink
	public void onRender(Render3DEvent e) {
		if (PlayerUtil.inGame()) {
			int rgb = gay.isToggled() ? Color.getHSBColor((float)(System.currentTimeMillis() % (15000L / 3)) / (15000.0F / (float)3), 1.0F, 1.0F).getRGB() : (Color.getHSBColor((float)(color.getInput() % 360) / 360.0f, 1.0f, 1.0f)).getRGB();

	        for (EntityPlayer en : mc.theWorld.playerEntities) {
	            if ((en == mc.thePlayer || en.deathTime != 0) || (!invi.isToggled() && en.isInvisible()) || AntiBot.bot(en)) {
	                continue;
	            }

	            if (tim.isToggled() && getColor(en.getCurrentArmor(2)) > 0) {
	                int armorColor = new Color(getColor(en.getCurrentArmor(2))).getRGB();
	                renderPlayer(en, armorColor);
	            } else {
	            	renderPlayer(en, rgb);
	            }
	        }
	        
	        if (chestESP.isToggled()) {
			    for (TileEntity te : mc.theWorld.loadedTileEntityList) {
			        if (te instanceof TileEntityChest || te instanceof TileEntityEnderChest) {
			            RenderUtil.drawChestBox(te.getPos(), rgb, true);
			        }
			    }
	        }
	    }
	}
	
	public int getColor(ItemStack x) {
	    if (x == null) { return -1; }
	    NBTTagCompound nbt = x.getTagCompound();
	    if (nbt != null) {
	        NBTTagCompound displayTag = nbt.getCompoundTag("display");
	        if (displayTag != null && displayTag.hasKey("color", 3)) {
	            return displayTag.getInteger("color");
	        }
	    }

	    return -2;
	}

	private void renderPlayer(Entity en, int rgb) {
		switch (mode.getMode()) {
		case "Box":
			RenderUtil.drawBoxAroundEntity(en, 1, 0.0D, 0.0D, rgb, dmg.isToggled());
			break;
		case "2D":
			RenderUtil.drawBoxAroundEntity(en, 2, 0.0D, 0.0D, rgb, dmg.isToggled());
			break;
		case "Health":
			RenderUtil.drawBoxAroundEntity(en, 3, 0.0D, 0.0D, rgb, dmg.isToggled());
			break;
		}
	}
}