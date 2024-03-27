package cc.unknown.utils.hook;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ResourcePackImageScaler {
	public static final int SIZE = 64;

	public static BufferedImage scalePackImage(BufferedImage image) throws IOException {
		if (image == null)
			return null;
		System.out.println("Scaling resource pack icon from " + image.getWidth() + " to " + '@');
		BufferedImage smallImage = new BufferedImage(64, 64, 2);
		Graphics graphics = smallImage.getGraphics();
		graphics.drawImage(image, 0, 0, 64, 64, null);
		graphics.dispose();
		return smallImage;
	}
}
