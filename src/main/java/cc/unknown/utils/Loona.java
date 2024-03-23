package cc.unknown.utils;

import cc.unknown.module.impl.visuals.HUD;
import net.minecraft.client.Minecraft;

public interface Loona {
	static Minecraft mc = Minecraft.getMinecraft();
	static HUD hud = new HUD();
}