package cc.unknown.utils.font;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.InputStream;

import cc.unknown.module.impl.visuals.HUD;

public class FontUtil {
    public static FontRenderer light;
    private static Font lightFont;

    private static Font loadFont(String location, int size, int fontType) {
        Font font = null;

        try (InputStream is = HUD.class.getResourceAsStream("/assets/minecraft/haru/fonts/" + location)) {
            if (is == null) {
                throw new IllegalArgumentException("Font resource not found: " + location);
            }
            font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(fontType, size);
        } catch (FontFormatException | IllegalArgumentException e) {
            System.err.println("Error loading font: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return null;
        }

        return font;
    }

    public static void bootstrap() {
        lightFont = loadFont("SF-Pro-Rounded-Light.otf", 19, Font.PLAIN);
        light = new FontRenderer(lightFont, true, true);
    }
}