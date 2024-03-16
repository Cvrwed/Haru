package cc.unknown.module.impl.visuals;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.ui.clickgui.raven.theme.Theme;
import cc.unknown.utils.client.RenderUtil;
import cc.unknown.utils.player.CombatUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;

public class ESP extends Module {
	private ModeValue mode = new ModeValue("ESP Types", "2D", "2D", "Health", "Box");
	private BooleanValue chestESP = new BooleanValue("ChestESP", false);
	private BooleanValue invi = new BooleanValue("Show invis", true);
	private BooleanValue tim = new BooleanValue("Color team", true);
	private BooleanValue dmg = new BooleanValue("Red on damage", true);

	public ESP() {
		super("ESP", ModuleCategory.Visuals);
		this.registerSetting(mode,  chestESP, invi, dmg, tim);
	}

	@EventLink
	public void onRender(Render3DEvent e) {
	    if (!PlayerUtil.inGame()) {
	        return;
	    }

	    int rgb = tim.isToggled() ? 0 : calculatePlayerColor();

	    for (EntityPlayer player : mc.theWorld.playerEntities) {
	        if (shouldRenderPlayer(player)) {
	            int armorColor = getColorFromArmor(player);
	            int renderColor = tim.isToggled() && armorColor > 0 ? armorColor : rgb;
	            renderPlayer(player, renderColor);
	        }
	    }

	    if (chestESP.isToggled()) {
	        mc.theWorld.loadedTileEntityList.forEach(te -> {
	            if (te instanceof TileEntityChest || te instanceof TileEntityEnderChest) {
	                RenderUtil.drawChestBox(te.getPos(), rgb, true);
	            }
	        });
	    }
	}

	private int calculatePlayerColor() {
		return Theme.getMainColor().getRGB();
	}

	private boolean shouldRenderPlayer(EntityPlayer player) {
		return player != mc.thePlayer && player.deathTime == 0 && (!invi.isToggled() || !player.isInvisible())
				&& !CombatUtil.instance.bot(player);
	}

	private int getColorFromArmor(EntityPlayer player) {
		ItemStack armor = player.getCurrentArmor(2);
		return armor != null ? getColor(armor) : 0;
	}

	public int getColor(ItemStack stack) {
		if (stack == null)
			return -1;
		NBTTagCompound nbttagcompound = stack.getTagCompound();
		if (nbttagcompound != null) {
			NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");
			if (nbttagcompound1 != null && nbttagcompound1.hasKey("color", 3)) {
				return nbttagcompound1.getInteger("color");
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