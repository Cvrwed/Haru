package cc.unknown.utils.client;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

public class ColorUtil {

	public static Color blend(Color color, Color color1, double d0) {
		float f = (float) d0;
		float f1 = 1.0F - f;
		float[] afloat = new float[3];
		float[] afloat1 = new float[3];
		color.getColorComponents(afloat);
		color1.getColorComponents(afloat1);
		return new Color(afloat[0] * f + afloat1[0] * f1, afloat[1] * f + afloat1[1] * f1,
				afloat[2] * f + afloat1[2] * f1);
	}
	
    public static Color gradientDraw(Color color1, Color color2, int yLocation) {
        double angle = System.currentTimeMillis() / 600.0D - yLocation * 0.06D;
        double normalizedSin = Math.cos(angle) * 0.5 + 0.5;
        
        int red = interpolate(color1.getRed(), color2.getRed(), normalizedSin);
        int green = interpolate(color2.getGreen(), color1.getGreen(), normalizedSin);
        int blue = interpolate(color1.getBlue(), color2.getBlue(), normalizedSin);
        
        return new Color(red, green, blue);
    }

    private static int interpolate(int start, int end, double percent) {
        return (int) (start + (end - start) * percent);
    }

	public static Color reverseGradientDraw(Color color1, Color color2, int yLocation) {
		final double percent = Math.sin(System.currentTimeMillis() / 600.0D - yLocation * 0.06D) * 0.5D + 0.5D;
		return new Color((int) (color1.getRed() + (color2.getRed() - color1.getRed()) * percent),
				(int) (color1.getGreen() + (color2.getGreen() - color1.getGreen()) * percent),
				(int) (color1.getBlue() + (color2.getBlue() - color1.getBlue()) * percent));
	}

	public static Color reverseGradientDraw(Color color1, Color color2, Color color3, int yLocation) {
		final double percent = Math.sin(System.currentTimeMillis() / 600.0D - yLocation * 0.06D) * 0.5D + 0.5D;
		return new Color((int) (color1.getRed() + (color2.getRed() - color1.getRed()) * percent),
				(int) (color1.getGreen() + (color2.getGreen() - color1.getGreen()) * percent),
				(int) (color3.getBlue() + (color3.getBlue() - color3.getBlue()) * percent));
	}

	public static void setColor(final int color) {
		final float alpha = 0.8f;
		final float red = ((color >> 16) & 0xFF) / 255.0f;
		final float green = ((color >> 8) & 0xFF) / 255.0f;
		final float blue = (color & 0xFF) / 255.0f;
		GL11.glColor4f(red, green, blue, alpha);
	}
	
	public static int rainbowDraw(long speed, long... delay) {
        long time = System.currentTimeMillis() + (delay.length > 0 ? delay[0] : 0L);
        return Color.getHSBColor((float) (time % (15000L / speed)) / (15000.0F / (float) speed), 1.0F, 1.0F)
                .getRGB();
    }

}
