package cc.unknown.module.impl.combat;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.netty.PostVelocityEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;

@Register(name = "JumpReset", category = Category.Combat)
public class JumpReset extends Module {

	@EventLink
	public void onPostVelocity(PostVelocityEvent event) {
		if (mc.thePlayer.onGround && mc.thePlayer.hurtTime > 0) mc.thePlayer.jump();
	}
}
