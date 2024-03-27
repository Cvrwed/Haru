package cc.unknown;

import java.util.ConcurrentModificationException;

import cc.unknown.module.Module;
import cc.unknown.utils.Loona;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@Mod(modid = Loader.MODID, version = Loader.VERSION, acceptedMinecraftVersions = "[1.8.9]")
public class Loader implements Loona {

    public static final String MODID = "haru";
    public static final String VERSION = "3.6";

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        Haru.instance.startClient();
    }

	@SubscribeEvent
	public void onTick(ClientTickEvent e) {
		if (e.phase == Phase.END) {
			try {
				if (PlayerUtil.inGame()) {
					for (Module module : Haru.instance.getModuleManager().getModule()) {
						if (Minecraft.getMinecraft().currentScreen == null) {
							module.keybind();
						}
					}
				}
				} catch (ConcurrentModificationException ignore) { }
		}
	}
}