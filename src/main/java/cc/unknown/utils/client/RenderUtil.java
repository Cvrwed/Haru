package cc.unknown.utils.client;

import org.lwjgl.opengl.GL11;

import cc.unknown.utils.interfaces.Loona;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class RenderUtil implements Loona {

	protected static float zLevel;

	public static void drawMenu(int mouseX, int mouseY) {
	}

	public static void drawRect(double left, double top, double right, double bottom, int color) {
		if (left < right) {
			double i = left;
			left = right;
			right = i;
		}
		if (top < bottom) {
			double j = top;
			top = bottom;
			bottom = j;
		}
		float f3 = (color >> 24 & 0xFF) / 255.0F;
		float f = (color >> 16 & 0xFF) / 255.0F;
		float f1 = (color >> 8 & 0xFF) / 255.0F;
		float f2 = (color & 0xFF) / 255.0F;
		Tessellator t = Tessellator.getInstance();
		WorldRenderer w = t.getWorldRenderer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color(f, f1, f2, f3);
		w.begin(7, DefaultVertexFormats.POSITION);
		w.pos(left, bottom, 0.0D).endVertex();
		w.pos(right, bottom, 0.0D).endVertex();
		w.pos(right, top, 0.0D).endVertex();
		w.pos(left, top, 0.0D).endVertex();
		t.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	public static void drawBorderedRoundedRect1(float x, float y, float x1, float y1, float radius, float borderSize,
			int borderC, int insideC) {
		drawRoundedRect(x, y, x1, y1, radius, insideC);
		drawRoundedOutline(x, y, x1, y1, radius, borderSize, borderC);
	}

	public static void drawRoundedRect(float x, float y, float x1, float y1, final float radius, final int color) {
		drawRoundedRect(x, y, x1, y1, radius, color, new boolean[] { true, true, true, true });
	}

	public static void drawRoundedRect(float x, float y, float x1, float y1, final float radius, final int color,
			boolean[] round) {
		GL11.glPushAttrib(0);
		GL11.glScaled(0.5, 0.5, 0.5);
		x *= 2.0;
		y *= 2.0;
		x1 *= 2.0;
		y1 *= 2.0;
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glEnable(2848);
		ColorUtil.setColor(color);
		GL11.glEnable(2848);
		GL11.glBegin(GL11.GL_POLYGON);
		round(x, y, x1, y1, radius, round);
		GL11.glEnd();
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glDisable(2848);
		GL11.glDisable(3042);
		GL11.glDisable(2848);
		GL11.glScaled(2.0, 2.0, 2.0);
		GL11.glEnable(3042);
		GL11.glPopAttrib();
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public static void roundHelper(float x, float y, float radius, int pn, int pn2, int originalRotation,
			int finalRotation) {
		for (int i = originalRotation; i <= finalRotation; i += 3)
			GL11.glVertex2d(x + (radius * -pn) + (Math.sin((i * 3.141592653589793) / 180.0) * radius * pn),
					y + (radius * pn2) + (Math.cos((i * 3.141592653589793) / 180.0) * radius * pn));
	}

	public static void drawRoundedOutline(float x, float y, float x1, float y1, final float radius,
			final float borderSize, final int color) {
		drawRoundedOutline(x, y, x1, y1, radius, borderSize, color, new boolean[] { true, true, true, true });
	}

	public static void round(float x, float y, float x1, float y1, float radius, final boolean[] round) {
		if (round[0])
			roundHelper(x, y, radius, -1, 1, 0, 90);
		else
			GL11.glVertex2d(x, y);

		if (round[1])
			roundHelper(x, y1, radius, -1, -1, 90, 180);
		else
			GL11.glVertex2d(x, y1);

		if (round[2])
			roundHelper(x1, y1, radius, 1, -1, 0, 90);
		else
			GL11.glVertex2d(x1, y1);

		if (round[3])
			roundHelper(x1, y, radius, 1, 1, 90, 180);
		else
			GL11.glVertex2d(x1, y);
	}

	public static void drawRoundedOutline(float x, float y, float x1, float y1, final float radius,
			final float borderSize, final int color, boolean[] drawCorner) {
		GL11.glPushAttrib(0);
		GL11.glScaled(0.5, 0.5, 0.5);
		x *= 2.0;
		y *= 2.0;
		x1 *= 2.0;
		y1 *= 2.0;
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		ColorUtil.setColor(color);
		GL11.glEnable(2848);
		GL11.glLineWidth(borderSize);
		GL11.glBegin(2);
		round(x, y, x1, y1, radius, drawCorner);
		GL11.glEnd();
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glDisable(2848);
		GL11.glDisable(3042);
		GL11.glEnable(3553);
		GL11.glScaled(2.0, 2.0, 2.0);
		GL11.glPopAttrib();
		GL11.glLineWidth(1.0f);
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public static void rect(float x1, float y1, float x2, float y2, int fill) {
		GlStateManager.color(0, 0, 0);
		GL11.glColor4f(0, 0, 0, 0);
		float f = (fill >> 24 & 0xFF) / 255.0F;
		float f1 = (fill >> 16 & 0xFF) / 255.0F;
		float f2 = (fill >> 8 & 0xFF) / 255.0F;
		float f3 = (fill & 0xFF) / 255.0F;
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(2848);
		GL11.glPushMatrix();
		GL11.glColor4f(f1, f2, f3, f);
		GL11.glBegin(7);
		GL11.glVertex2d(x2, y1);
		GL11.glVertex2d(x1, y1);
		GL11.glVertex2d(x1, y2);
		GL11.glVertex2d(x2, y2);
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glDisable(2848);
	}

	public static void drawBorderedRect(float f, float f1, float f2, float f3, float f4, int i, int j) {
		drawRect(f, f1, f2, f3, j);
		float f5 = (float) (i >> 24 & 255) / 255.0F;
		float f6 = (float) (i >> 16 & 255) / 255.0F;
		float f7 = (float) (i >> 8 & 255) / 255.0F;
		float f8 = (float) (i & 255) / 255.0F;

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glPushMatrix();
		GL11.glColor4f(f6, f7, f8, f5);
		GL11.glLineWidth(f4);
		GL11.glBegin(1);
		GL11.glVertex2d(f, f1);
		GL11.glVertex2d(f, f3);
		GL11.glVertex2d(f2, f3);
		GL11.glVertex2d(f2, f1);
		GL11.glVertex2d(f, f1);
		GL11.glVertex2d(f2, f1);
		GL11.glVertex2d(f, f3);
		GL11.glVertex2d(f2, f3);
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}

	public static void stopDrawing() {
		GL11.glDisable(3042);
		GL11.glEnable(3553);
		GL11.glDisable(2848);
		GL11.glDisable(3042);
		GL11.glEnable(2929);
	}

	public static void startDrawing() {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 0);
	}

	public static void drawImage(ResourceLocation resourceLocation, float x, float y, float imgWidth, float imgHeight) {
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		mc.getTextureManager().bindTexture(resourceLocation);
		Gui.drawModalRectWithCustomSizedTexture((int) x, (int) y, 0, 0, (int) imgWidth, (int) imgHeight, imgWidth,
				imgHeight);
		GlStateManager.disableBlend();
	}

	public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
		float f = (float) (startColor >> 24 & 255) / 255.0F;
		float f1 = (float) (startColor >> 16 & 255) / 255.0F;
		float f2 = (float) (startColor >> 8 & 255) / 255.0F;
		float f3 = (float) (startColor & 255) / 255.0F;
		float f4 = (float) (endColor >> 24 & 255) / 255.0F;
		float f5 = (float) (endColor >> 16 & 255) / 255.0F;
		float f6 = (float) (endColor >> 8 & 255) / 255.0F;
		float f7 = (float) (endColor & 255) / 255.0F;
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos((double) right, (double) top, (double) zLevel).color(f1, f2, f3, f).endVertex();
		worldrenderer.pos((double) left, (double) top, (double) zLevel).color(f1, f2, f3, f).endVertex();
		worldrenderer.pos((double) left, (double) bottom, (double) zLevel).color(f5, f6, f7, f4).endVertex();
		worldrenderer.pos((double) right, (double) bottom, (double) zLevel).color(f5, f6, f7, f4).endVertex();
		tessellator.draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}

}
