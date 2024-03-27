package cc.unknown.utils.hook;

import java.awt.image.BufferedImage;
import java.lang.ref.WeakReference;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.util.ResourceLocation;

public class CapeImageBuffer implements IImageBuffer {
	public ImageBufferDownload imageBufferDownload;

	public final WeakReference<AbstractClientPlayer> playerRef;

	public final ResourceLocation resourceLocation;

	public CapeImageBuffer(AbstractClientPlayer player, ResourceLocation resourceLocation) {
		this.playerRef = new WeakReference<>(player);
		this.resourceLocation = resourceLocation;
		this.imageBufferDownload = new ImageBufferDownload();
	}

	@Override
	public BufferedImage parseUserSkin(BufferedImage image) {
		return parseUserSkin(image);
	}

	@Override
	public void skinAvailable() {
		AbstractClientPlayer player = this.playerRef.get();
		if (player != null)
			setLocationOfCape(player, this.resourceLocation);
	}

	private static void setLocationOfCape(AbstractClientPlayer player, ResourceLocation resourceLocation) {
	}
}