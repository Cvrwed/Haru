package cc.unknown.module.impl.visuals;

import java.awt.Color;
import java.util.Iterator;

import cc.unknown.event.impl.api.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.impl.other.AntiBot;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.ui.clickgui.theme.Theme;
import cc.unknown.utils.client.FuckUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ESP extends Module {
	private ModeValue mode = new ModeValue("ESP Types", "2D", "2D", "Health", "Box");
	private BooleanValue invi = new BooleanValue("Show invis", true);
	private BooleanValue tim = new BooleanValue("Color team", true);
	private BooleanValue dmg = new BooleanValue("Red on damage", true);

	public ESP() {
		super("ESP", ModuleCategory.Visuals);
		this.registerSetting(mode, invi, dmg, tim);
	}

	@EventLink
	public void r1(Render3DEvent e) {
		if (PlayerUtil.inGame()) {
			int rgb = Theme.getMainColor().getRGB();
			Iterator<EntityPlayer> var3 = mc.theWorld.playerEntities.iterator();

			while (true) {
				EntityPlayer en;
				do {
					do {
						do {
							if (!var3.hasNext()) {
								return;
							}
							en = (EntityPlayer) var3.next();
						} while (en == mc.thePlayer);
					} while (en.deathTime != 0);
				} while (!invi.isToggled() && en.isInvisible());

				if (!AntiBot.bot(en)) {
					if (tim.isToggled() && getColor(en.getCurrentArmor(2)) > 0) {
						int E = new Color(getColor(en.getCurrentArmor(2))).getRGB();
						this.r(en, E);
					} else {
						this.r(en, rgb);
					}
				}
			}
		}
	}

	public int getColor(ItemStack x) {
		if (x == null)
			return -1;
		NBTTagCompound nbt = x.getTagCompound();
		if (nbt != null) {
			NBTTagCompound nbt1 = nbt.getCompoundTag("display");
			if (nbt1 != null && nbt1.hasKey("color", 3)) {
				return nbt1.getInteger("color");
			}
		}
		return -2;
	}

	private void r(Entity en, int rgb) {
		switch (mode.getMode()) {
		case "Box":
			FuckUtil.drawBoxAroundEntity(en, 1, 0.0D, 0.0D, rgb, dmg.isToggled());
			break;
		case "2D":
			FuckUtil.drawBoxAroundEntity(en, 2, 0.0D, 0.0D, rgb, dmg.isToggled());
			break;
		case "Health":
			FuckUtil.drawBoxAroundEntity(en, 3, 0.0D, 0.0D, rgb, dmg.isToggled());
			break;
		}
	}
}