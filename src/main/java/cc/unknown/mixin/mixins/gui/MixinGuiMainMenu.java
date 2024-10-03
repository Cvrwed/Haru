package cc.unknown.mixin.mixins.gui;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import cc.unknown.ui.AltLoginScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonLanguage;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.client.GuiModList;

@Mixin(GuiMainMenu.class)
public abstract class MixinGuiMainMenu extends GuiScreen {
	@Shadow
	private DynamicTexture viewportTexture;
	@Shadow
	private ResourceLocation backgroundTexture;
	@Shadow
	private String splashText;
	@Final
	@Shadow
	private static final ResourceLocation minecraftTitleTextures = new ResourceLocation("textures/gui/title/minecraft.png");
	@Shadow
	private float updateCounter;
	@Shadow
	public abstract void renderSkybox(int p_73971_1_, int p_73971_2_, float p_73971_3_);

	@Overwrite
	public void initGui() {
		this.viewportTexture = new DynamicTexture(256, 256);
		this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background",
				this.viewportTexture);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24) {
			this.splashText = "Merry X-mas!";
		} else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1) {
			this.splashText = "Happy new year!";
		} else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
			this.splashText = "OOoooOOOoooo! Spooky!";
		}

		int j = this.height / 4 + 48;

		this.addSingleplayerMultiplayerButtons(j, 24);

		this.buttonList.add(new GuiButton(0, this.width / 2 - 100, j + 72 + 12, 98, 20, I18n.format("menu.options")));
		this.buttonList.add(new GuiButton(4, this.width / 2 + 2, j + 72 + 12, 98, 20, I18n.format("menu.quit")));
		this.buttonList.add(new GuiButtonLanguage(5, this.width / 2 - 124, j + 72 + 12));
	}

	@Overwrite
	private void addSingleplayerMultiplayerButtons(int p_73969_1_, int p_73969_2_) {
		this.buttonList.add(
				new GuiButton(1, this.width / 2 - 100, p_73969_1_, I18n.format("menu.singleplayer")));
		this.buttonList.add(new GuiButton(2, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 1,
				I18n.format("menu.multiplayer")));
		this.buttonList.add(new GuiButton(14, this.width / 2 + 2, p_73969_1_ + p_73969_2_ * 2, 98, 20, "Alt Manager"));
		this.buttonList.add(new GuiButton(6, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 2, 98, 20,
				I18n.format("fml.menu.mods")));
	}

	@Overwrite
	public void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0) {
			this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
		}

		if (button.id == 5) {
			this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
		}

		if (button.id == 1) {
			this.mc.displayGuiScreen(new GuiSelectWorld(this));
		}

		if (button.id == 2) {
			this.mc.displayGuiScreen(new GuiMultiplayer(this));
		}

		if (button.id == 14) {
			this.mc.displayGuiScreen(new AltLoginScreen());
		}

		if (button.id == 4) {
			this.mc.shutdown();
		}

		if (button.id == 6) {
			this.mc.displayGuiScreen(new GuiModList(this));
		}
	}

	@Overwrite
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.renderSkybox(mouseX, mouseY, partialTicks);
		GlStateManager.enableAlpha();
		int i = 274;
		int j = this.width / 2 - i / 2;
		int k = 30;

		this.mc.getTextureManager().bindTexture(minecraftTitleTextures);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		if ((double) this.updateCounter < 1.0E-4D) {
			this.drawTexturedModalRect(j + 0, k + 0, 0, 0, 99, 44);
			this.drawTexturedModalRect(j + 99, k + 0, 129, 0, 27, 44);
			this.drawTexturedModalRect(j + 99 + 26, k + 0, 126, 0, 3, 44);
			this.drawTexturedModalRect(j + 99 + 26 + 3, k + 0, 99, 0, 26, 44);
			this.drawTexturedModalRect(j + 155, k + 0, 0, 45, 155, 44);
		} else {
			this.drawTexturedModalRect(j + 0, k + 0, 0, 0, 155, 44);
			this.drawTexturedModalRect(j + 155, k + 0, 0, 45, 155, 44);
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate((float) (this.width / 2 + 90), 70.0F, 0.0F);
		GlStateManager.rotate(-20.0F, 0.0F, 0.0F, 1.0F);
		float f = 1.8F - MathHelper
				.abs(MathHelper.sin((float) (Minecraft.getSystemTime() % 1000L) / 1000.0F * 3.1415927F * 2.0F) * 0.1F);
		f = f * 100.0F / (float) (this.fontRendererObj.getStringWidth(this.splashText) + 32);
		GlStateManager.scale(f, f, f);
		this.drawCenteredString(this.fontRendererObj, this.splashText, 0, -8, -256);
		GlStateManager.popMatrix();

		ForgeHooksClient.renderMainMenu((GuiMainMenu) (Object) this, this.fontRendererObj, this.width, this.height);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}