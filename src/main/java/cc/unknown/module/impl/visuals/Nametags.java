package cc.unknown.module.impl.visuals;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import cc.unknown.event.impl.api.EventLink;
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

public class Nametags extends Module {

	private ModeValue mode = new ModeValue("Mode", "Health", "Health", "Percentage");
	private SliderValue range = new SliderValue("Range", 0.0, 0.0, 512.0, 1.0);
	private SliderValue scale = new SliderValue("Scale",  4.5, 0.1, 10.0, 0.1);
	private SliderValue opacity = new SliderValue("Opacity", 185, 5, 200, 5);
	private BooleanValue armor = new BooleanValue("Armor", true);
	private BooleanValue durability = new BooleanValue("Durability", false);
	private BooleanValue distanceSetting = new BooleanValue("Distance", false);
	private float _x,_y,_z;

	public Nametags() {
		super("NameTags", ModuleCategory.Visuals);
		this.registerSetting(mode, range, scale, opacity, armor, durability, distanceSetting);
	}
	
	@EventLink
	public void onRender(RenderLabelEvent e) {
	    if (!(e.getTarget() instanceof EntityPlayer)) {
	        return;
	    }

	    EntityPlayer player = (EntityPlayer) e.getTarget();
	    String playerName = player.getDisplayName().getFormattedText();

	    if (playerName == null || playerName.isEmpty()) {
	        return;
	    }

	    if (!CombatUtil.instance.canTarget(player, true)) {
	        return;
	    }

	    double playerDistance = mc.thePlayer.getDistanceToEntity(player);
	    if (!(playerDistance <= range.getInput() || range.getInput() == 0.0D)) {
	        return;
	    }

	    e.setCancelled(true);
	}

	@EventLink
	public void onRender3D(Render3DEvent e) {
        ArrayList<EntityLivingBase> playersArr = new ArrayList<>();

        Iterator<?> playerIterator = mc.theWorld.playerEntities.iterator();

        while (playerIterator.hasNext()) {
            EntityLivingBase entityLivingBase = (EntityLivingBase) playerIterator.next();
            if ((double) mc.thePlayer.getDistanceToEntity(entityLivingBase) > range.getInput() && range.getInput() != 0.0D) {
                playersArr.remove(entityLivingBase);
            } else if (entityLivingBase.getName().contains("[NPC]")) {
                playersArr.remove(entityLivingBase);
            } else if (entityLivingBase.isEntityAlive()) {
                if (entityLivingBase.isInvisible()) {
                    playersArr.remove(entityLivingBase);
                } else if (entityLivingBase == mc.thePlayer) {
                    playersArr.remove(entityLivingBase);
                } else {
                    if (playersArr.size() > 100) {
                        break;
                    }

                    if (!playersArr.contains(entityLivingBase)) {
                        playersArr.add(entityLivingBase);
                    }
                }
            } else playersArr.remove(entityLivingBase);
        }

        _x = 0.0F;
        _y = 0.0F;
        _z = 0.0F;
        playerIterator = playersArr.iterator();

        while (playerIterator.hasNext()) {
            EntityPlayer player = (EntityPlayer) playerIterator.next();
            if (CombatUtil.instance.canTarget(player, true)) {
                player.setAlwaysRenderNameTag(false);
                _x = (float) (player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosX);
                _y = (float) (player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosY);
                _z = (float) (player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosZ);
                renderNametag(player, _x, _y, _z);
            }
        }

    }

    private String getHealth(EntityPlayer player) {
        DecimalFormat decimalFormat = new DecimalFormat("0.#");
        return mode.is("Percentage") ? decimalFormat.format(player.getHealth() * 5.0F + player.getAbsorptionAmount() * 5.0F) : decimalFormat.format(player.getHealth() / 2.0F + player.getAbsorptionAmount() / 2.0F);
    }

    private void drawNames(EntityPlayer player) {
        float e = (float) getWidth(getPlayerName(player)) / 2.0F + 2.2F;
        float i;
        e = i = (float) ((double) e + (getWidth(" " + getHealth(player)) / 2) + 2.5D);
        float x = -e - 2.2F;
        float z = (float) (getWidth(getPlayerName(player)) + 4);
        if (mode.is("Percentage")) {
            RenderUtil.drawBorderedRect(x, -3.0F, e, 10.0F, 1.0F, (new Color(20, 20, 20, opacity.getInputToInt())).getRGB(), (new Color(10, 10, 10, opacity.getInputToInt())).getRGB());
        } else {
            RenderUtil.drawBorderedRect(x + 5.0F, -3.0F, e, 10.0F, 1.0F, (new Color(20, 20, 20, opacity.getInputToInt())).getRGB(), (new Color(10, 10, 10, opacity.getInputToInt())).getRGB());
        }

        GlStateManager.disableDepth();
        if (mode.is("Percentage")) {
            z += (float) (getWidth(getHealth(player)) + getWidth(" %") - 1);
        } else {
            z += (float) (getWidth(getHealth(player)) + getWidth(" ") - 1);
        }

        drawString(getPlayerName(player), i - z, 0.0F, 16777215);

        int blendColor;
        if (player.getHealth() > 10.0F) {
            blendColor = ColorUtil.blend(new Color(-16711936), new Color(-256), (1.0F / player.getHealth() / 2.0F * (player.getHealth() - 10.0F))).getRGB();
        } else {
            blendColor = ColorUtil.blend(new Color(-256), new Color(-65536), 0.1F * player.getHealth()).getRGB();
        }

        if (mode.is("Percentage")) {
            drawString(getHealth(player) + "%", i - (float) getWidth(getHealth(player) + " %"), 0.0F, blendColor);
        } else {
            drawString(getHealth(player), i - (float) getWidth(getHealth(player) + " "), 0.0F, blendColor);
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
        double scaleRatio = (double) (getSize(player) / 10.0F * scale.getInput()) * 1.5D;
        GL11.glPushMatrix();
        RenderUtil.startDrawing();
        GL11.glTranslatef(x, y, z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(mc.getRenderManager().playerViewX, rotateX, 0.0F, 0.0F);
        GL11.glScaled(-0.01666666753590107D * scaleRatio, -0.01666666753590107D * scaleRatio, 0.01666666753590107D * scaleRatio);
    }

    private void stopDrawing() {
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private void renderNametag(EntityPlayer player, float x, float y, float z) {
        y += (float) (1.55D + (player.isSneaking() ? 0.5D : 0.7D));
        startDrawing(x, y, z, player);
        drawNames(player);
        GL11.glColor4d(1.0D, 1.0D, 1.0D, 1.0D);
        if (armor.isToggled()) {
            renderArmor(player);
        }

        stopDrawing();
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

            renderItemStack(var10, pos, -20);
            pos += 16;
        }

        armor = player.inventory.armorInventory;

        for (int i = 3; i >= 0; --i) {
            ItemStack var11 = armor[i];
            if (var11 != null) {
                renderItemStack(var11, pos, -20);
                pos += 16;
            }
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private String getPlayerName(EntityPlayer player) {
        boolean isDistanceSettingToggled = distanceSetting.isToggled();
        return (isDistanceSettingToggled ? (new DecimalFormat("#.##")).format(mc.thePlayer.getDistanceToEntity(player)) + "m " : "") + player.getDisplayName().getFormattedText();
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
        renderEnchantText(is, xPos, yPos);
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
                int projectileProtection = EnchantmentHelper.getEnchantmentLevel(Enchantment.projectileProtection.effectId, is);
                int blastProtectionLvL = EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, is);
                int fireProtection = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, is);
                int thornsLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, is);
                int unbreakingLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, is);
                int remainingDurability = is.getMaxDamage() - is.getItemDamage();
                if (durability.isToggled()) {
                    mc.fontRendererObj.drawStringWithShadow(String.valueOf(remainingDurability), (float) (xPos * 2), (float) yPos, 16777215);
                }

                if (protection > 0) {
                    mc.fontRendererObj.drawStringWithShadow("prot" + protection, (float) (xPos * 2), (float) newYPos, -1);
                    newYPos += 8;
                }

                if (projectileProtection > 0) {
                    mc.fontRendererObj.drawStringWithShadow("proj" + projectileProtection, (float) (xPos * 2), (float) newYPos, -1);
                    newYPos += 8;
                }

                if (blastProtectionLvL > 0) {
                    mc.fontRendererObj.drawStringWithShadow("bp" + blastProtectionLvL, (float) (xPos * 2), (float) newYPos, -1);
                    newYPos += 8;
                }

                if (fireProtection > 0) {
                    mc.fontRendererObj.drawStringWithShadow("frp" + fireProtection, (float) (xPos * 2), (float) newYPos, -1);
                    newYPos += 8;
                }

                if (thornsLvl > 0) {
                    mc.fontRendererObj.drawStringWithShadow("th" + thornsLvl, (float) (xPos * 2), (float) newYPos, -1);
                    newYPos += 8;
                }

                if (unbreakingLvl > 0) {
                    mc.fontRendererObj.drawStringWithShadow("unb" + unbreakingLvl, (float) (xPos * 2), (float) newYPos, -1);
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
                    mc.fontRendererObj.drawStringWithShadow("flame" + flameLvl, (float) (xPos * 2), (float) newYPos, -1);
                    newYPos += 8;
                }

                if (unbreakingLvl > 0) {
                    mc.fontRendererObj.drawStringWithShadow("unb" + unbreakingLvl, (float) (xPos * 2), (float) newYPos, -1);
                    newYPos += 8;
                }
            }

            if (is.getItem() instanceof ItemSword) {
                int sharpnessLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, is);
                int knockbackLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, is);
                int fireAspectLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, is);
                int unbreakingLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, is);
                if (sharpnessLvl > 0) {
                    mc.fontRendererObj.drawStringWithShadow("sh" + sharpnessLvl, (float) (xPos * 2), (float) newYPos, -1);
                    newYPos += 8;
                }

                if (knockbackLvl > 0) {
                    mc.fontRendererObj.drawStringWithShadow("kb" + knockbackLvl, (float) (xPos * 2), (float) newYPos, -1);
                    newYPos += 8;
                }

                if (fireAspectLvl > 0) {
                    mc.fontRendererObj.drawStringWithShadow("fire" + fireAspectLvl, (float) (xPos * 2), (float) newYPos, -1);
                    newYPos += 8;
                }

                if (unbreakingLvl > 0) {
                    mc.fontRendererObj.drawStringWithShadow("unb" + unbreakingLvl, (float) (xPos * 2), (float) newYPos, -1);
                }
            }

            if (is.getItem() instanceof ItemTool) {
                int unbreakingLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, is);
                int efficiencyLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, is);
                int fortuneLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, is);
                int silkTouchLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, is);
                if (efficiencyLvl > 0) {
                    mc.fontRendererObj.drawStringWithShadow("eff" + efficiencyLvl, (float) (xPos * 2), (float) newYPos, -1);
                    newYPos += 8;
                }

                if (fortuneLvl > 0) {
                    mc.fontRendererObj.drawStringWithShadow("fo" + fortuneLvl, (float) (xPos * 2), (float) newYPos, -1);
                    newYPos += 8;
                }

                if (silkTouchLvl > 0) {
                    mc.fontRendererObj.drawStringWithShadow("silk" + silkTouchLvl, (float) (xPos * 2), (float) newYPos, -1);
                    newYPos += 8;
                }

                if (unbreakingLvl > 0) {
                    mc.fontRendererObj.drawStringWithShadow("ub" + unbreakingLvl, (float) (xPos * 2), (float) newYPos, -1);
                }
            }

            if (is.getItem() == Items.golden_apple && is.hasEffect()) {
                mc.fontRendererObj.drawStringWithShadow("god", (float) (xPos * 2), (float) newYPos, -1);
            }
        }
    }
}