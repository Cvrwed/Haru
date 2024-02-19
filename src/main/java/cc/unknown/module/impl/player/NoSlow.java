package cc.unknown.module.impl.player;

import cc.unknown.event.impl.api.EventLink;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.C07PacketPlayerDigging;

public class NoSlow extends Module {
	public ModeValue mode = new ModeValue("Mode", "Grim", "Grim", "C16", "Vanilla", "No Item Release");
	public SliderValue vForward = new SliderValue("Vanilla forward", 1.0, 0.2, 1.0, 0.1);
	public SliderValue vStrafe = new SliderValue("Vanilla strafe", 1.0, 0.2, 1.0, 0.1);

	public NoSlow() {
		super("NoSlow", ModuleCategory.Player);
		this.registerSetting(mode, vForward, vStrafe);
	}
	
	@EventLink
	public void onPacket(PacketEvent e) {
		if (e.isReceive()) {
			if (mc.thePlayer.isUsingItem() && PlayerUtil.isMoving()) {
				switch (mode.getMode()) {
				case "No Item Release": {
					if (e.getPacket() instanceof C07PacketPlayerDigging) {
						C07PacketPlayerDigging c07 = (C07PacketPlayerDigging) e.getPacket();
						if (c07.getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) {
							if (!(mc.thePlayer.getHeldItem().getItem() instanceof ItemBow)) {
								e.setCancelled(true);
							}
						}
					}
				}
				break;
				}
			}
		}
	}
}
