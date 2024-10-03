package cc.unknown.utils.client;

import cc.unknown.utils.Loona;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class BackgroundUtil implements Loona {
	public static void renderBackground(GuiScreen gui) {
		final int width = gui.width;
	    final int height = gui.height;
	    RenderUtil.drawImage(new ResourceLocation("haru/images/background.jpg"), 0, 0, width, height);
	}
}