package cc.unknown.module.impl.visuals;

import java.awt.Color;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.ui.clickgui.raven.theme.Theme;
import cc.unknown.utils.client.RenderUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class ESP extends Module {
	
	private ModeValue mode = new ModeValue("Mode", "2D", "2D", "Box", "Health", "Box Teams");
	
	private BooleanValue playerColor = new BooleanValue("Player Color", false);
	private SliderValue pColor = new SliderValue("Color [H/S/B]", 0, 0, 350, 10);
	
	private BooleanValue checkInvi = new BooleanValue("Check invisible", true);
	private BooleanValue checkTeams = new BooleanValue("Check teams", true);
	
	private BooleanValue hit = new BooleanValue("Hit color", false);
	public SliderValue hitColor = new SliderValue("Hit color [H/S/B]", 0, 0, 350, 10);
	
	private BooleanValue chestESP = new BooleanValue("Chest Esp", false);
    private BooleanValue ignoreOpenedChests = new BooleanValue("Ignore opened chests", false);
	private BooleanValue chest = new BooleanValue("Chest Color", false);
	private SliderValue cChest = new SliderValue("Chest color [H/S/B]", 0, 0, 350, 10);


	public ESP() {
		super("ESP", ModuleCategory.Visuals);
	    this.registerSetting(mode, playerColor, pColor, checkInvi, checkTeams, hit, hitColor, chestESP, ignoreOpenedChests, chest, cChest);
	}

	@EventLink
	public void onRender(Render3DEvent fe) {
	    if (PlayerUtil.inGame()) {
	        int rgb = playerColor.isToggled() ? Color.getHSBColor((pColor.getInputToFloat() % 360) / 360.0f, 1.0f, 1.0f).getRGB() : Theme.getMainColor().getRGB();
	        int chestColor = chest.isToggled() ? Color.getHSBColor((cChest.getInputToFloat() % 360) / 360.0f, 1.0f, 1.0f).getRGB() : Theme.getMainColor().getRGB();

	        
	        for (EntityPlayer en : mc.theWorld.playerEntities) {
	            if (en != mc.thePlayer && en.deathTime == 0 && (checkInvi.isToggled() || !en.isInvisible())) {
	                if (checkTeams.isToggled() && getColor(en.getCurrentArmor(2)) > 0) {
	                    int E = new Color(getColor(en.getCurrentArmor(2))).getRGB();
	                    renderPlayer(en, E);
	                } else {
	                    renderPlayer(en, rgb);
	                }
	            }
	        }
	        
	        if (chestESP.isToggled()) {
	            for (TileEntity te : mc.theWorld.loadedTileEntityList) {
	                if (te instanceof TileEntityChest || te instanceof TileEntityEnderChest) {
	                    if (!ignoreOpenedChests.isToggled() || !isChestOpen(te)) {
	                        RenderUtil.drawChestBox(te.getPos(), chestColor, true);
	                    }
	                }
	            }
	        }
	    }
	}

	public int getColor(ItemStack stack) {
		if (stack == null)
			return -1;
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null) {
			NBTTagCompound nbt = tag.getCompoundTag("display");
			if (nbt != null && nbt.hasKey("color", 3)) {
				return nbt.getInteger("color");
			}
		}
		return -2;
	}

	private void renderPlayer(Entity en, int rgb) {
		switch (mode.getMode()) {
		case "Box":
			RenderUtil.drawBoxAroundEntity(en, 1, 0.0D, 0.0D, rgb, hit.isToggled());
			break;
		case "2D":
			RenderUtil.drawBoxAroundEntity(en, 3, 0.0D, 0.0D, rgb, hit.isToggled());
			break;
		case "Health":
			RenderUtil.drawBoxAroundEntity(en, 4, 0.0D, 0.0D, rgb, hit.isToggled());
			break;
		case "Box Teams":
			RenderUtil.drawBoxAroundEntity(en, 7, 0.0D, 0.0D, rgb, hit.isToggled());
			break;
		}
	}
	
    private boolean isChestOpen(TileEntity te) {
        if (te instanceof TileEntityChest) {
            BlockPos pos = te.getPos();
            IBlockState state = mc.theWorld.getBlockState(pos);
            Block block = state.getBlock();
            if (block instanceof BlockChest) {
                EnumFacing facing = state.getValue(BlockChest.FACING);
                BlockPos otherPos = pos.offset(facing);
                IBlockState otherState = mc.theWorld.getBlockState(otherPos);
                if (otherState.getBlock() == block) {
                    return true;
                }
            }
        }
        return false;
    }
}