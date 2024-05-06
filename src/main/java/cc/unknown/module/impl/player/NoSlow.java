package cc.unknown.module.impl.player;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.network.PacketUtil;
import net.minecraft.item.ItemBow;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

@Register(name = "NoSlow", category = Category.Player)
public class NoSlow extends Module {
	public ModeValue mode = new ModeValue("Mode", "Old Grim", "Old Grim", "Vanilla", "No Item Release", "C08 Tick");
	public SliderValue vForward = new SliderValue("Vanilla forward", 1.0, 0.2, 1.0, 0.1);
	public SliderValue vStrafe = new SliderValue("Vanilla strafe", 1.0, 0.2, 1.0, 0.1);

	public NoSlow() {
		this.registerSetting(mode, vForward, vStrafe);
	}
	
	@EventLink
	public void onGui(ClickGuiEvent e) {
	    this.setSuffix("- [" + mode.getMode() + "]");
	}
	
	public void slow() {
		switch (mode.getMode()) {
		case "Old Grim":
			int slot = mc.thePlayer.inventory.currentItem;
			PacketUtil.sendPacketSilent(new C09PacketHeldItemChange(slot < 8 ? slot + 1 : 0));
			PacketUtil.sendPacketSilent(new C09PacketHeldItemChange(slot));
			break;
		case "Vanilla":
			mc.thePlayer.movementInput.moveForward *= vForward.getInputToFloat();
			mc.thePlayer.movementInput.moveStrafe *= vStrafe.getInputToFloat();
			break;
		case "C08 Tick":
			if (mc.thePlayer.ticksExisted % 3 == 0) {
				PacketUtil.sendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
			}
			break;
		}
	}

	@EventLink
	public void onPacket(PacketEvent e) {
		if (e.isSend()) {
			final Packet<INetHandlerPlayServer> p = (Packet<INetHandlerPlayServer>) e.getPacket();
			if (mode.is("No Item Release")) {
				if (p instanceof C07PacketPlayerDigging) {
					C07PacketPlayerDigging wrapper = (C07PacketPlayerDigging) p;
					if (wrapper.getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) {
						if (!(mc.thePlayer.getHeldItem().getItem() instanceof ItemBow)) {
							e.setCancelled(true);
						}
					}
				}
			}
		}
	}
}
