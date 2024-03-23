package cc.unknown.module.impl.combat;

import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.Cold;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class WTap extends Module {

	private ModeValue mode = new ModeValue("Mode", "Pre", "Pre", "Post");
	private SliderValue range = new SliderValue("Combo range", 3.5, 1.0, 6.0, 0.5);
	private SliderValue chance = new SliderValue("Tap chance", 100, 0, 100, 1);
	private DoubleSliderValue hits = new DoubleSliderValue("Once every hits", 1, 1, 1, 10, 1);
	private DoubleSliderValue preDelay = new DoubleSliderValue("Pre tap delay", 25, 55, 1, 500, 1);
	private DoubleSliderValue postDelay = new DoubleSliderValue("Post tap delay", 25, 55, 1, 500, 1);

	public static boolean comboing, hitCoolDown, alreadyHit, waitingForPostDelay;
	public static int hitTimeout, hitsWaited;
	public static Cold actionTimer = new Cold(), postDelayTimer = new Cold();

	public WTap() {
		super("WTap", ModuleCategory.Combat);
		this.registerSetting(mode, range, chance, hits, preDelay, postDelay);
	}

	@EventLink
	public void onRender(Render3DEvent e) {
		if (!PlayerUtil.inGame())
			return;

		if (waitingForPostDelay) {
			if (postDelayTimer.elapsed(System.currentTimeMillis(), true)) {
				waitingForPostDelay = false;
				comboing = true;
				startCombo();
			}
			return;
		}

		if (comboing) {
			if (actionTimer.elapsed(System.currentTimeMillis(), true)) {
				comboing = false;
				finishCombo();
				return;
			} else {
				return;
			}
		}

		if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit instanceof Entity && Mouse.isButtonDown(0)) {
			Entity target = mc.objectMouseOver.entityHit;
			if (target.isDead) {
				return;
			}

			if (mc.thePlayer.getDistanceToEntity(target) <= range.getInput()) {
				if ((target.hurtResistantTime >= 10 && mode.is("Post")) || (target.hurtResistantTime <= 10 && mode.is("Pre"))) {

					if (!(target instanceof EntityPlayer)) {
						return;
					}

					if (hitCoolDown && !alreadyHit) {
						hitsWaited++;
						if (hitsWaited >= hitTimeout) {
							hitCoolDown = false;
							hitsWaited = 0;
						} else {
							alreadyHit = true;
							return;
						}
					}

					if (!(chance.getInput() == 100 || Math.random() <= chance.getInput() / 100))
						return;

					if (!alreadyHit) {
						if (hits.getInputMin() == hits.getInputMax()) {
							hitTimeout = (int) hits.getInputMin();
						} else {

							hitTimeout = ThreadLocalRandom.current().nextInt((int) hits.getInputMin(),
									(int) hits.getInputMax());
						}
						hitCoolDown = true;
						hitsWaited = 0;

						actionTimer.elapsed((long) ThreadLocalRandom.current().nextDouble(preDelay.getInputMin(),
								preDelay.getInputMax() + 0.01), true);

						if (postDelay.getInputMax() != 0) {
							postDelayTimer.elapsed((long) ThreadLocalRandom.current()
									.nextDouble(postDelay.getInputMin(), postDelay.getInputMax() + 0.01), true);
							waitingForPostDelay = true;
						} else {
							comboing = true;
							startCombo();
							alreadyHit = true;
						}

						alreadyHit = true;
					}
				} else {
					if (alreadyHit) {
						alreadyHit = false;
					}
				}
			}
		}
	}

	private static void finishCombo() {
		if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
		}
	}

	private static void startCombo() {
		if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
			KeyBinding.onTick(mc.gameSettings.keyBindForward.getKeyCode());
		}
	}
}

