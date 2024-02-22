package cc.unknown.module.impl.visuals;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import cc.unknown.event.impl.api.EventLink;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.event.impl.render.RenderLabelEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.ColorUtil;
import cc.unknown.utils.client.RenderUtil;
import cc.unknown.utils.player.CombatUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

@SuppressWarnings("all")
public class Nametags extends Module {

	private boolean armor, dura;
	private float _x, _y, _z;
	private ModeValue mode = new ModeValue("Mode", "Health", "Health", "Percentage");
	private SliderValue rangeSetting = new SliderValue("Range", 0.0D, 0.0D, 512.0D, 1.0D);
	private BooleanValue armorSetting = new BooleanValue("Armor", true);
	private BooleanValue durabilitySetting = new BooleanValue("Durability", false);
	private BooleanValue distanceSetting = new BooleanValue("Distance", false);
	private BooleanValue showInvi = new BooleanValue("Show invis", false);

	public Nametags() {
		super("NameTags", ModuleCategory.Visuals);
		this.registerSetting(mode, rangeSetting, armorSetting, durabilitySetting, distanceSetting, showInvi);
	}

	@EventLink
	public void guiUpdate(ClickGuiEvent e) {
		this.armor = this.armorSetting.isToggled();
		this.dura = this.durabilitySetting.isToggled();
	}

	@EventLink
	public void nameTag(RenderLabelEvent e) {
		if (e.getTarget() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) e.getTarget();
			boolean nameNotNull = player.getDisplayName().getFormattedText() != null;
			boolean nameNotEmpty = !player.getDisplayName().getFormattedText().equals("");
			boolean canTarget = CombatUtil.canTarget(player, true);
			boolean withinRange = ((double) mc.thePlayer.getDistanceToEntity(player) <= rangeSetting.getInput()
					|| rangeSetting.getInput() == 0.0D);
			if (nameNotNull && nameNotEmpty && canTarget && withinRange) {
				e.setCancelled(true);
			}
		}
	}

	@EventLink
	public void onRender3D(Render3DEvent e) {

		ArrayList<EntityLivingBase> players = new ArrayList<>();
		Iterator<EntityPlayer> playerIterator = mc.theWorld.playerEntities.iterator();

		while (playerIterator.hasNext()) {
			EntityLivingBase entity = playerIterator.next();
			if ((double) mc.thePlayer.getDistanceToEntity(entity) > rangeSetting.getInput()
					&& rangeSetting.getInput() != 0.0D) {
				continue;
			}

			String name = entity.getName();
			if (name.contains("-") || name.contains("/") || name.contains("|") || name.contains("<")
					|| name.contains(">") || name.contains("\u0e22\u0e07") || name.isEmpty()) {
				continue;
			}

			if (!showInvi.isToggled() && entity.isInvisible())
				continue;

			players.add(entity);
			if (players.size() >= 100) {
				break;
			}
		}

		float _x = 0.0F, _y = 0.0F, _z = 0.0F;
		RenderManager renderManager = mc.getRenderManager();

		for (EntityLivingBase player : players) {
			if (CombatUtil.canTarget(player, true)) {
				player.setAlwaysRenderNameTag(false);
				_x = (float) (player.lastTickPosX + (player.posX - player.lastTickPosX) * mc.timer.renderPartialTicks
						- renderManager.viewerPosX);
				_y = (float) (player.lastTickPosY + (player.posY - player.lastTickPosY) * mc.timer.renderPartialTicks
						- renderManager.viewerPosY);
				_z = (float) (player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * mc.timer.renderPartialTicks
						- renderManager.viewerPosZ);
				this.renderNametag((EntityPlayer) player, _x, _y, _z);

			}
		}
	}

	private String getHealth(EntityPlayer player) {
		DecimalFormat decimalFormat = new DecimalFormat("0.#");
		return mode.is("Percentage")
				? decimalFormat.format(player.getHealth() * 5.0F + player.getAbsorptionAmount() * 5.0F)
				: decimalFormat.format(player.getHealth() / 2.0F + player.getAbsorptionAmount() / 2.0F);
	}

	private void drawNames(EntityPlayer player) {
		float neim = (float) getWidth(getPlayerName(player)) / 2.0F + 2.2F;
		float nia;
		neim = nia = (float) ((double) neim + (getWidth(" " + getHealth(player)) / 2) + 2.5D);
		float onichan = -neim - 2.2F;
		float yameteKudasai = (float) (getWidth(getPlayerName(player)) + 4);
		if (mode.is("Percentage")) {
			RenderUtil.drawBorderedRect(onichan, -3.0F, neim, 10.0F, 1.0F, (new Color(20, 20, 20, 180)).getRGB(),
					(new Color(10, 10, 10, 200)).getRGB());
		} else {
			RenderUtil.drawBorderedRect(onichan + 5.0F, -3.0F, neim, 10.0F, 1.0F, (new Color(20, 20, 20, 180)).getRGB(),
					(new Color(10, 10, 10, 200)).getRGB());
		}

		GlStateManager.disableDepth();
		if (mode.is("Percentage")) {
			yameteKudasai += (float) (getWidth(getHealth(player)) + getWidth(" %") - 1);
		} else {
			yameteKudasai += (float) (getWidth(getHealth(player)) + getWidth(" ") - 1);
		}

		this.drawString(getPlayerName(player), nia - yameteKudasai, 0.0F, 16777215);

		int blendColor;
		if (player.getHealth() > 10.0F) {
			blendColor = ColorUtil.blend(new Color(-16711936), new Color(-256),
					(1.0F / player.getHealth() / 2.0F * (player.getHealth() - 10.0F))).getRGB();
		} else {
			blendColor = ColorUtil.blend(new Color(-256), new Color(-65536), 0.1F * player.getHealth()).getRGB();
		}

		if (mode.is("Percentage")) {
			this.drawString(getHealth(player) + "%", nia - (float) getWidth(getHealth(player) + " %"), 0.0F,
					blendColor);
		} else {
			this.drawString(getHealth(player), nia - (float) getWidth(getHealth(player) + " "), 0.0F, blendColor);
		}

		GlStateManager.enableDepth();
	}

	private void drawString(String string, float x, float y, int z) {
		mc.fontRendererObj.drawStringWithShadow(string, x, y, z);
	}

	private int getWidth(String string) {
		return mc.fontRendererObj.getStringWidth(string);
	}

	private void startDrawing(float x, float y, float z, EntityPlayer player) {
		float rotateX = mc.gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F;
		double scaleRatio = (double) (getSize(player) / 10.0F * 4.5F) * 1.5D;
		GL11.glPushMatrix();
		RenderUtil.startDrawing();
		GL11.glTranslatef(x, y, z);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(mc.getRenderManager().playerViewX, rotateX, 0.0F, 0.0F);
		GL11.glScaled(-0.01666666753590107D * scaleRatio, -0.01666666753590107D * scaleRatio,
				0.01666666753590107D * scaleRatio);
	}

	private void stopDrawing() {
		RenderUtil.stopDrawing();
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}

	private void renderNametag(EntityPlayer player, float x, float y, float z) {
		y += (float) (1.55D + (player.isSneaking() ? 0.5D : 0.7D));
		this.startDrawing(x, y, z, player);
		this.drawNames(player);
		GL11.glColor4d(1.0D, 1.0D, 1.0D, 1.0D);
		if (armor) {
			this.renderArmor(player);
		}

		this.stopDrawing();
	}

	private void renderArmor(EntityPlayer player) {
		ItemStack[] armor = player.inventory.armorInventory;
		int pos = 0;

		for (ItemStack is : armor) {
			if (is != null) {
				pos -= 8;
			}
		}

		if (player.getHeldItem() != null) {
			pos -= 8;
			ItemStack var10 = player.getHeldItem().copy();
			if (var10.hasEffect() && (var10.getItem() instanceof ItemTool || var10.getItem() instanceof ItemArmor)) {
				var10.stackSize = 1;
			}

			this.renderItemStack(var10, pos, -20);
			pos += 16;
		}

		armor = player.inventory.armorInventory;

		for (int i = 3; i >= 0; --i) {
			ItemStack var11 = armor[i];
			if (var11 != null) {
				this.renderItemStack(var11, pos, -20);
				pos += 16;
			}
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private String getPlayerName(EntityPlayer player) {
		boolean isDistanceSettingToggled = distanceSetting.isToggled();
		return (isDistanceSettingToggled
				? (new DecimalFormat("#.##")).format(mc.thePlayer.getDistanceToEntity(player)) + "m "
				: "") + player.getDisplayName().getFormattedText();
	}

	private float getSize(EntityPlayer player) {
		return Math.max(mc.thePlayer.getDistanceToEntity(player) / 4.0F, 2.0F);
	}

	private void renderItemStack(ItemStack is, int xPos, int yPos) {
		GlStateManager.pushMatrix();
		GlStateManager.depthMask(true);
		GlStateManager.clear(256);
		RenderHelper.enableStandardItemLighting();
		mc.getRenderItem().zLevel = -150.0F;
		GlStateManager.disableDepth();
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		mc.getRenderItem().renderItemAndEffectIntoGUI(is, xPos, yPos);
		mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, is, xPos, yPos);
		mc.getRenderItem().zLevel = 0.0F;
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableCull();
		GlStateManager.enableAlpha();
		GlStateManager.disableBlend();
		GlStateManager.disableLighting();
		GlStateManager.scale(0.5D, 0.5D, 0.5D);
		GlStateManager.disableDepth();
		this.renderEnchantText(is, xPos, yPos);
		GlStateManager.enableDepth();
		GlStateManager.scale(2.0F, 2.0F, 2.0F);
		GlStateManager.popMatrix();
	}

	private void renderEnchantText(ItemStack is, int xPos, int yPos) {
		int newYPos = yPos - 24;
		if (is.getEnchantmentTagList() != null && is.getEnchantmentTagList().tagCount() >= 6) {
			mc.fontRendererObj.drawStringWithShadow("god", (float) (xPos * 2), (float) newYPos, 16711680);
		} else {
			if (is.getItem() instanceof ItemArmor) {
				int protection = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, is);
				int projectileProtection = EnchantmentHelper
						.getEnchantmentLevel(Enchantment.projectileProtection.effectId, is);
				int blastProtectionLvL = EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId,
						is);
				int fireProtection = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, is);
				int thornsLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, is);
				int unbreakingLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, is);
				int remainingDurability = is.getMaxDamage() - is.getItemDamage();
				if (dura) {
					mc.fontRendererObj.drawStringWithShadow(String.valueOf(remainingDurability), (float) (xPos * 2),
							(float) yPos, 16777215);
				}

				if (protection > 0) {
					mc.fontRendererObj.drawStringWithShadow("prot" + protection, (float) (xPos * 2), (float) newYPos,
							-1);
					newYPos += 8;
				}

				if (projectileProtection > 0) {
					mc.fontRendererObj.drawStringWithShadow("proj" + projectileProtection, (float) (xPos * 2),
							(float) newYPos, -1);
					newYPos += 8;
				}

				if (blastProtectionLvL > 0) {
					mc.fontRendererObj.drawStringWithShadow("bp" + blastProtectionLvL, (float) (xPos * 2),
							(float) newYPos, -1);
					newYPos += 8;
				}

				if (fireProtection > 0) {
					mc.fontRendererObj.drawStringWithShadow("frp" + fireProtection, (float) (xPos * 2), (float) newYPos,
							-1);
					newYPos += 8;
				}

				if (thornsLvl > 0) {
					mc.fontRendererObj.drawStringWithShadow("th" + thornsLvl, (float) (xPos * 2), (float) newYPos, -1);
					newYPos += 8;
				}

				if (unbreakingLvl > 0) {
					mc.fontRendererObj.drawStringWithShadow("unb" + unbreakingLvl, (float) (xPos * 2), (float) newYPos,
							-1);
					newYPos += 8;
				}
			}

			if (is.getItem() instanceof ItemBow) {
				int powerLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, is);
				int punchLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, is);
				int flameLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, is);
				int unbreakingLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, is);
				if (powerLvl > 0) {
					mc.fontRendererObj.drawStringWithShadow("pow" + powerLvl, (float) (xPos * 2), (float) newYPos, -1);
					newYPos += 8;
				}

				if (punchLvl > 0) {
					mc.fontRendererObj.drawStringWithShadow("pun" + punchLvl, (float) (xPos * 2), (float) newYPos, -1);
					newYPos += 8;
				}

				if (flameLvl > 0) {
					mc.fontRendererObj.drawStringWithShadow("flame" + flameLvl, (float) (xPos * 2), (float) newYPos,
							-1);
					newYPos += 8;
				}

				if (unbreakingLvl > 0) {
					mc.fontRendererObj.drawStringWithShadow("unb" + unbreakingLvl, (float) (xPos * 2), (float) newYPos,
							-1);
					newYPos += 8;
				}
			}

			if (is.getItem() instanceof ItemSword) {
				int sharpnessLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, is);
				int knockbackLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, is);
				int fireAspectLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, is);
				int unbreakingLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, is);
				if (sharpnessLvl > 0) {
					mc.fontRendererObj.drawStringWithShadow("sh" + sharpnessLvl, (float) (xPos * 2), (float) newYPos,
							-1);
					newYPos += 8;
				}

				if (knockbackLvl > 0) {
					mc.fontRendererObj.drawStringWithShadow("kb" + knockbackLvl, (float) (xPos * 2), (float) newYPos,
							-1);
					newYPos += 8;
				}

				if (fireAspectLvl > 0) {
					mc.fontRendererObj.drawStringWithShadow("fire" + fireAspectLvl, (float) (xPos * 2), (float) newYPos,
							-1);
					newYPos += 8;
				}

				if (unbreakingLvl > 0) {
					mc.fontRendererObj.drawStringWithShadow("unb" + unbreakingLvl, (float) (xPos * 2), (float) newYPos,
							-1);
				}
			}

			if (is.getItem() instanceof ItemTool) {
				int unbreakingLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, is);
				int efficiencyLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, is);
				int fortuneLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, is);
				int silkTouchLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, is);
				if (efficiencyLvl > 0) {
					mc.fontRendererObj.drawStringWithShadow("eff" + efficiencyLvl, (float) (xPos * 2), (float) newYPos,
							-1);
					newYPos += 8;
				}

				if (fortuneLvl > 0) {
					mc.fontRendererObj.drawStringWithShadow("fo" + fortuneLvl, (float) (xPos * 2), (float) newYPos, -1);
					newYPos += 8;
				}

				if (silkTouchLvl > 0) {
					mc.fontRendererObj.drawStringWithShadow("silk" + silkTouchLvl, (float) (xPos * 2), (float) newYPos,
							-1);
					newYPos += 8;
				}

				if (unbreakingLvl > 0) {
					mc.fontRendererObj.drawStringWithShadow("ub" + unbreakingLvl, (float) (xPos * 2), (float) newYPos,
							-1);
				}
			}

			if (is.getItem() == Items.golden_apple && is.hasEffect()) {
				mc.fontRendererObj.drawStringWithShadow("god", (float) (xPos * 2), (float) newYPos, -1);
			}

		}
	}

}