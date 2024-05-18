package cc.unknown.module.impl.other;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.network.play.client.C15PacketClientSettings;

@Register(name = "Misc", category = Category.Other)
public class Misc extends Module {
	
	public BooleanValue noScoreboard = new BooleanValue("No Scoreboard", false);
	private BooleanValue cancelC15 = new BooleanValue("Cancel C15", true);

	public Misc() {
		this.registerSetting(noScoreboard, cancelC15);
	}

	@EventLink
	public void onCancelC15(PacketEvent e) {
		if (cancelC15.isToggled() && PlayerUtil.inGame() && this.isEnabled()) {
			if (e.isSend()) {
				if (e.getPacket() instanceof C15PacketClientSettings) {
					e.setCancelled(true);
				}
			}
		}
	}
}
