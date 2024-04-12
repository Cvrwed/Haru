package cc.unknown.module.impl.combat;

import java.util.function.Supplier;
import java.util.stream.Stream;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.PreUpdateEvent;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.network.PacketEvent.Type;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.helpers.MathHelper;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

@Register(name = "Velocity", category = Category.Combat)
public class Velocity extends Module {

	private ModeValue mode = new ModeValue("Mode", "S12Packet", "S12Packet", "Verus");
	public SliderValue horizontal = new SliderValue("Horizontal", 90, -100, 100, 1);
	public SliderValue vertical = new SliderValue("Vertical", 100, -100, 100, 1);
	public SliderValue chance = new SliderValue("Chance", 100, 0, 100, 1);
	private BooleanValue onlyCombat = new BooleanValue("Only During Combat", false);
	private BooleanValue onlyGround = new BooleanValue("Only While on Ground", false);

	public Velocity() {
		this.registerSetting(mode, horizontal, vertical, chance, onlyCombat, onlyGround);
	}
	
	@EventLink
	public void onGui(ClickGuiEvent e) {
	    this.setSuffix(mode.getMode());
	}

	@EventLink
	public void onPacket(PacketEvent e) {
		if (shouldIgnoreVelocity() || applyChance())
			return;
		Packet<?> p = e.getPacket();

		if (e.getType() == Type.RECEIVE) {
			if (mode.is("S12Packet")) {
				if (p instanceof S12PacketEntityVelocity) {
					final S12PacketEntityVelocity s12 = (S12PacketEntityVelocity) p;
					if (s12.getEntityID() == mc.thePlayer.getEntityId()) {

						if (horizontal.getInput() == 0) {
							e.setCancelled(true);

							if (vertical.getInput() != 0) {
								mc.thePlayer.motionY = s12.getMotionY() / 8000.0D;
							}
							return;
						}

						s12.motionX *= horizontal.getInput() / 100;
						s12.motionY *= vertical.getInput() / 100;
						s12.motionZ *= horizontal.getInput() / 100;

						e.setPacket(s12);
					}
				}

				if (p instanceof S27PacketExplosion) {
					final S27PacketExplosion s27 = (S27PacketExplosion) p;
					if (horizontal.getInput() != 0f && vertical.getInput() != 0f) {
						s27.field_149152_f = 0f;
						s27.field_149153_g = 0f;
						s27.field_149159_h = 0f;
						return;
					}

					s27.field_149152_f *= horizontal.getInput();
					s27.field_149153_g *= vertical.getInput();
					s27.field_149159_h *= horizontal.getInput();
				}
			}
		}
	}

	@EventLink
	public void onPre(PreUpdateEvent e) {
		if (PlayerUtil.inGame()) {
			if (mode.is("Verus") && mc.thePlayer.hurtTime == 10 - MathHelper.randomInt(3, 4)) {
				mc.thePlayer.motionX = 0.0D;
				mc.thePlayer.motionY = 0.0D;
				mc.thePlayer.motionZ = 0.0D;

			}
		}
	}

	private boolean shouldIgnoreVelocity() {
		return Stream
				.<Supplier<Boolean>>of(
						() -> onlyCombat.isToggled()
								&& (mc.objectMouseOver == null || mc.objectMouseOver.entityHit == null),
						() -> onlyGround.isToggled() && mc.thePlayer.onGround)
				.map(Supplier::get).anyMatch(Boolean.TRUE::equals);
	}

	private boolean applyChance() {
		Supplier<Boolean> chanceCheck = () -> {
			return chance.getInput() != 100.0D && Math.random() >= chance.getInput() / 100.0D;
		};

		return Stream.of(chanceCheck).map(Supplier::get).anyMatch(Boolean.TRUE::equals);
	}
}
