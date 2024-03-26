package cc.unknown.module.impl.visuals;

import java.awt.Color;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.UpdateEvent;
import cc.unknown.event.impl.move.UpdateEvent.Action;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.network.PacketEvent.Type;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.ui.clickgui.raven.impl.api.Theme;
import cc.unknown.utils.font.FontUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TargetHUD extends Module {

	private SliderValue posX = new SliderValue("Position X", 500, 10, 1920, 10);
	private SliderValue posY = new SliderValue("Position Y", 0, 10, 1080, 10);

	private EntityPlayer player;
	private int ticksSinceAttack;

	public TargetHUD() {
		super("TargetHUD", ModuleCategory.Visuals);
		this.registerSetting(posX, posY);
	}

	@Override
	public void onEnable() {
		player = null;
	}

	@EventLink
	public void onPre(UpdateEvent e) {
		if (e.getAction() == Action.PRE) {
			ticksSinceAttack++;

			if (ticksSinceAttack > 20) {
				player = null;
			}
		}
	}

	@EventLink
	public void onPacket(PacketEvent e) {
		if (e.getType() == Type.SEND) {
			if (e.getPacket() instanceof C02PacketUseEntity) {
				C02PacketUseEntity wrapper = (C02PacketUseEntity) e.getPacket();
				if (wrapper.getEntityFromWorld(mc.theWorld) instanceof EntityPlayer
						&& wrapper.getAction() == C02PacketUseEntity.Action.ATTACK) {
					ticksSinceAttack = 0;
					player = (EntityPlayer) wrapper.getEntityFromWorld(mc.theWorld);
				}
			}
		}
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent ev) {
		ScaledResolution sr = new ScaledResolution(mc);
		int x = (sr.getScaledWidth() / 2) + posX.getInputToInt(), y = (sr.getScaledHeight() / 2) + posY.getInputToInt();
		if (player == null)
			return;
		drawRect(x, y, 120, 40, new Color(0, 0, 0, 120).getRGB());
		FontUtil.two.drawString(player.getName(), x + 45, y + 8, -1);
		double offset = -(player.hurtTime * 20);
		Color color = new Color(255, (int) (255 + offset), (int) (255 + offset));
		GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F,
				color.getAlpha() / 255F);
		mc.getTextureManager().bindTexture(((AbstractClientPlayer) player).getLocationSkin());
		Gui.drawScaledCustomSizeModalRect(x + 5, y + 5, 3, 3, 3, 3, 30, 30, 24, 24);
		GlStateManager.color(1, 1, 1, 1);

		drawRect(x + 45, y + 20, 70, 15, new Color(255, 255, 255, 120).getRGB());

		drawRect(x + 45, y + 20, (int) (70 * (player.getHealth() / player.getMaxHealth())), 15,
				Theme.getMainColor().darker().getRGB());

		String s = (int) ((player.getHealth() / player.getMaxHealth()) * 100) + "%";
		FontUtil.two.drawString(s, x + 45 + (70 / 2) - (FontUtil.two.getStringWidth(s) / 2),
				y + 20 + (15 / 2) - (FontUtil.two.getHeight() / 2) + 1, -1);
	}

	private void drawRect(int x, int y, int width, int height, int color) {
		Gui.drawRect(x, y, x + width, y + height, color);
	}
}
