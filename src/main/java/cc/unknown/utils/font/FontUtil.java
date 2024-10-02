package cc.unknown.utils.font;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cc.unknown.module.impl.visuals.HUD;

public class FontUtil {
    private static final int FONT_LOAD_COUNT = 2;

    public static FontRenderer light;

    private static Font lightFont;

    private static Font getFont(Map<String, Font> locationMap, String location, int size, int fontType) {
        Font font = null;

        try {
            if (locationMap.containsKey(location)) {
                font = locationMap.get(location).deriveFont(fontType, size);
            } else {
                try (InputStream is = HUD.class.getResourceAsStream("/assets/minecraft/haru/fonts/" + location)) {
                    if (is == null) {
                        throw new IllegalArgumentException("Font resource not found: " + location);
                    }
                    font = Font.createFont(Font.TRUETYPE_FONT, is);
                    locationMap.put(location, font);
                }
                font = font.deriveFont(fontType, size);
            }
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
        CountDownLatch latch = new CountDownLatch(FONT_LOAD_COUNT);
        ExecutorService executorService = Executors.newFixedThreadPool(FONT_LOAD_COUNT);

        Map<String, Font> locationMap = new HashMap<>();	

        executorService.submit(() -> {
            lightFont = getFont(locationMap, "SF-Pro-Rounded-Light.otf", 19, Font.PLAIN);
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executorService.shutdown();
        }

        light = new FontRenderer(lightFont, true, true);
    }
}