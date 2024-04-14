package cc.unknown.module.impl.combat;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.PreUpdateEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;

@Register(name = "Test", category = Category.Combat)
public class Test extends Module {
	
	@EventLink
	public void onPre(PreUpdateEvent e) {
		if (mc.thePlayer.fallDistance >= 2) {
			e.setCancelled(true);
		}
	}
}
