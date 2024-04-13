package cc.unknown.module.impl.visuals;

import java.awt.Color;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
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
	private ModeValue mode = new ModeValue("Mode", "Player", "Player", "Chest", "Both");
	private BooleanValue playerColor = new BooleanValue("Player Color", false);
	private SliderValue pColor = new SliderValue("Player Color [H/S/B]", 0, 0, 350, 10);
	private BooleanValue chest = new BooleanValue("Chest Color", false);
	private SliderValue cChest = new SliderValue("Chest Color [H/S/B]", 0, 0, 350, 10);
	private BooleanValue checkInvi = new BooleanValue("Check Invisible", true);
	private BooleanValue checkTeams = new BooleanValue("Check Teams", true);

	public ESP() {
		this.registerSetting(boxMode, mode, playerColor, pColor, checkInvi, checkTeams, chest, cChest);
	}

	@EventLink
	public void onRender(Render3DEvent fe) {
		if (PlayerUtil.inGame()) {
			int rgb = playerColor.isToggled()
					? Color.getHSBColor((pColor.getInputToFloat() % 360) / 360.0f, 1.0f, 1.0f).getRGB()
					: Theme.instance.getMainColor().getRGB();
			int chestColor = chest.isToggled()
					? Color.getHSBColor((cChest.getInputToFloat() % 360) / 360.0f, 1.0f, 1.0f).getRGB()
					: Theme.instance.getMainColor().getRGB();

			if (mode.is("Player") || mode.is("Both")) {
				for (EntityPlayer en : mc.theWorld.playerEntities) {
					if (en != mc.thePlayer && en.deathTime == 0 && (checkInvi.isToggled() || !en.isInvisible())) {
						if (checkTeams.isToggled() && getColor(en.getCurrentArmor(2)) > 0) {
							int teams = new Color(getColor(en.getCurrentArmor(2))).getRGB();
							renderPlayer(en, teams);
						} else {
							renderPlayer(en, rgb);
						}
					}
				}
			}

			if (mode.is("Chest") || mode.is("Both")) {
				for (TileEntity te : mc.theWorld.loadedTileEntityList) {
					if (te instanceof TileEntityChest || te instanceof TileEntityEnderChest) {
						RenderUtil.drawChestBox(te.getPos(), chestColor, true);
					}
				}
			}
		}
	}

	private int getColor(ItemStack stack) {
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