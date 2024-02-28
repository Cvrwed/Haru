package cc.unknown.module.impl.visuals;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
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

public class Nametags extends Module {

	private ModeValue mode = new ModeValue("Mode", "Health", "Health", "Percentage");
	private SliderValue range = new SliderValue("Range", 0.0, 0.0, 512.0, 1.0);
	private BooleanValue armor = new BooleanValue("Armor", true);
	private BooleanValue durability = new BooleanValue("Durability", false);
	private BooleanValue distanceSetting = new BooleanValue("Distance", false);

	public Nametags() {
		super("NameTags", ModuleCategory.Visuals);
		this.registerSetting(mode, range, armor, durability, distanceSetting);
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
	    WorldClient world = mc.theWorld;
	    EntityPlayerSP player = mc.thePlayer;
	    RenderManager renderManager = mc.getRenderManager();
	    float partialTicks = mc.timer.renderPartialTicks;

	    List<EntityLivingBase> players = StreamSupport.stream(world.playerEntities.spliterator(), false)
	            .filter(entity -> {
	                double distance = player.getDistanceToEntity(entity);
	                return distance <= range.getInput() || range.getInput() == 0.0D;
	            })
	            .filter(entity -> {
	                String name = entity.getName();
	                return !name.contains("-") && !name.contains("/") && !name.contains("|")
	                        && !name.contains("<") && !name.contains(">") && !name.contains("\u0e22\u0e07")
	                        && !name.isEmpty();
	            })
	            .limit(100)
	            .collect(Collectors.toList());

	    players.stream().filter(targetPlayer -> CombatUtil.instance.canTarget(targetPlayer)).forEach(targetPlayer -> {
            targetPlayer.setAlwaysRenderNameTag(false);
            float x = (float) (targetPlayer.lastTickPosX + (targetPlayer.posX - targetPlayer.lastTickPosX) * partialTicks
                    - renderManager.viewerPosX);
            float y = (float) (targetPlayer.lastTickPosY + (targetPlayer.posY - targetPlayer.lastTickPosY) * partialTicks
                    - renderManager.viewerPosY);
            float z = (float) (targetPlayer.lastTickPosZ + (targetPlayer.posZ - targetPlayer.lastTickPosZ) * partialTicks
                    - renderManager.viewerPosZ);
            this.renderNametag((EntityPlayer) targetPlayer, x, y, z);
        });
	}
	
	private String getHealth(EntityPlayer player) {
	    DecimalFormat decimalFormat = new DecimalFormat("0.#");
	    Function<EntityPlayer, Float> healthFormatter = p ->
	    mode.is("Percentage") ? p.getHealth() * 5.0F + p.getAbsorptionAmount() * 5.0F
	                             : p.getHealth() / 2.0F + p.getAbsorptionAmount() / 2.0F;
	    return decimalFormat.format(healthFormatter.apply(player));
	}

	private void drawNames(EntityPlayer player) {
	    String playerName = getPlayerName(player);
	    String health = getHealth(player);

	    float nameWidth = mc.fontRendererObj.getStringWidth(playerName) / 2.0F + 2.2F;
	    float healthWidth = mc.fontRendererObj.getStringWidth(health);
	    float totalWidth = nameWidth + healthWidth / 2 + 2.5F;

	    float rectX = -nameWidth - 2.2F;
	    float rectWidth = totalWidth;

	    if (mode.is("Percentage")) {
	        rectWidth += mc.fontRendererObj.getStringWidth(" %") - 1;
	    } else {
	        rectWidth += mc.fontRendererObj.getStringWidth(" ") - 1;
	    }

	    float textX = totalWidth - rectWidth;

	    if (mode.is("Percentage")) {
	        RenderUtil.drawBorderedRect(rectX, -3.0F, totalWidth, 10.0F, 1.0F, new Color(20, 20, 20, 180).getRGB(), new Color(10, 10, 10, 200).getRGB());
	    } else {
	        RenderUtil.drawBorderedRect(rectX + 5.0F, -3.0F, totalWidth, 10.0F, 1.0F, new Color(20, 20, 20, 180).getRGB(), new Color(10, 10, 10, 200).getRGB());
	    }

	    mc.fontRendererObj.drawStringWithShadow(playerName, textX, 0.0F, 16777215);

	    int blendColor;
	    if (player.getHealth() > 10.0F) {
	        blendColor = ColorUtil.blend(new Color(-16711936), new Color(-256), (1.0F / (player.getHealth() / 2.0F) * (player.getHealth() - 10.0F))).getRGB();
	    } else {
	        blendColor = ColorUtil.blend(new Color(-256), new Color(-65536), 0.1F * player.getHealth()).getRGB();
	    }

	    if (mode.is("Percentage")) {
	        mc.fontRendererObj.drawStringWithShadow(health + "%", textX - mc.fontRendererObj.getStringWidth(health + " %"), 0.0F, blendColor);
	    } else {
	        mc.fontRendererObj.drawStringWithShadow(health, textX - mc.fontRendererObj.getStringWidth(health + " "), 0.0F, blendColor);
	    }
	}
	
	private void startDrawing(float x, float y, float z, EntityPlayer player) {
	    float rotateX = mc.gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F;
	    double scaleRatio = getSize(player) / 6.666666666666667F * 1.5D;
	    GL11.glPushMatrix();
	    RenderUtil.startDrawing();
	    GL11.glTranslatef(x, y, z);
	    GL11.glRotatef(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
	    GL11.glRotatef(mc.getRenderManager().playerViewX, rotateX, 0.0F, 0.0F);
	    GL11.glScaled(-0.01666666753590107D * scaleRatio, -0.01666666753590107D * scaleRatio, 0.01666666753590107D * scaleRatio);
	}

	private void renderNametag(EntityPlayer player, float x, float y, float z) {
	    float yOffset = (float) (1.55D + (player.isSneaking() ? 0.5D : 0.7D));
	    y += yOffset;
	    this.startDrawing(x, y, z, player);
	    this.drawNames(player);

	    if (armor.isToggled()) {
	        this.renderArmor(player);
	    }

	    GL11.glDisable(GL11.GL_BLEND);
	    GL11.glDisable(GL11.GL_ALPHA_TEST);
	    GL11.glEnable(GL11.GL_DEPTH_TEST);
	    GL11.glEnable(GL11.GL_TEXTURE_2D);
	    GL11.glDisable(GL11.GL_LIGHTING);
	    GL11.glEnable(GL11.GL_CULL_FACE);
	    GlStateManager.color(1.0F, 1.0F, 1.0F);
	    GlStateManager.popMatrix();
	}

	private void renderArmor(EntityPlayer player) {
	    ItemStack[] armor = player.inventory.armorInventory;
	    int pos = 0;

	    ItemStack heldItem = player.getHeldItem();
	    if (heldItem != null) {
	        pos -= 8;
	        ItemStack heldItemCopy = heldItem.copy();
	        if (heldItemCopy.hasEffect() && (heldItemCopy.getItem() instanceof ItemTool || heldItemCopy.getItem() instanceof ItemArmor)) {
	            heldItemCopy.stackSize = 1;
	        }
	        this.renderItemStack(heldItemCopy, pos, -20);
	        pos += 16;
	    }

	    for (ItemStack armorPiece : armor) {
	        if (armorPiece != null) {
	            this.renderItemStack(armorPiece, pos, -20);
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
	    RenderHelper.enableStandardItemLighting();
	    mc.getRenderItem().zLevel = -150.0F;
	    GlStateManager.disableDepth();

	    mc.getRenderItem().renderItemAndEffectIntoGUI(is, xPos, yPos);
	    mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, is, xPos, yPos);
	    mc.getRenderItem().zLevel = 0.0F;
	    RenderHelper.disableStandardItemLighting();
	    GlStateManager.enableDepth();

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
	        return;
	    }

	    if (is.getItem() instanceof ItemArmor) {
	        renderEnchantments("prot", Enchantment.protection, is, xPos, newYPos);
	        renderEnchantments("proj", Enchantment.projectileProtection, is, xPos, newYPos);
	        renderEnchantments("bp", Enchantment.blastProtection, is, xPos, newYPos);
	        renderEnchantments("frp", Enchantment.fireProtection, is, xPos, newYPos);
	        renderEnchantments("th", Enchantment.thorns, is, xPos, newYPos);
	        renderEnchantments("unb", Enchantment.unbreaking, is, xPos, newYPos);
	    } else if (is.getItem() instanceof ItemBow) {
	        renderEnchantments("pow", Enchantment.power, is, xPos, newYPos);
	        renderEnchantments("pun", Enchantment.punch, is, xPos, newYPos);
	        renderEnchantments("flame", Enchantment.flame, is, xPos, newYPos);
	        renderEnchantments("unb", Enchantment.unbreaking, is, xPos, newYPos);
	    } else if (is.getItem() instanceof ItemSword) {
	        renderEnchantments("sh", Enchantment.sharpness, is, xPos, newYPos);
	        renderEnchantments("kb", Enchantment.knockback, is, xPos, newYPos);
	        renderEnchantments("fire", Enchantment.fireAspect, is, xPos, newYPos);
	        renderEnchantments("unb", Enchantment.unbreaking, is, xPos, newYPos);
	    } else if (is.getItem() instanceof ItemTool) {
	        renderEnchantments("eff", Enchantment.efficiency, is, xPos, newYPos);
	        renderEnchantments("fo", Enchantment.fortune, is, xPos, newYPos);
	        renderEnchantments("silk", Enchantment.silkTouch, is, xPos, newYPos);
	        renderEnchantments("ub", Enchantment.unbreaking, is, xPos, newYPos);
	    } else if (is.getItem() == Items.golden_apple && is.hasEffect()) {
	        mc.fontRendererObj.drawStringWithShadow("god", (float) (xPos * 2), (float) newYPos, -1);
	    }

	    if (durability.isToggled()) {
	        int remainingDurability = is.getMaxDamage() - is.getItemDamage();
	        mc.fontRendererObj.drawStringWithShadow(String.valueOf(remainingDurability), (float) (xPos * 2), (float) yPos, 16777215);
	    }
	}

	private void renderEnchantments(String prefix, Enchantment enchantment, ItemStack is, int xPos, int yPos) {
	    int lvl = EnchantmentHelper.getEnchantmentLevel(enchantment.effectId, is);
	    if (lvl > 0) {
	        mc.fontRendererObj.drawStringWithShadow(prefix + lvl, (float) (xPos * 2), (float) yPos, -1);
	        yPos += 8;
	    }
	}

}