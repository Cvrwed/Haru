package cc.unknown.module.impl.combat;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.Cold;
import cc.unknown.utils.player.PlayerUtil;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

@Register(name = "BlockHit", category = Category.Combat)
public class BlockHit extends Module {
	private ModeValue mode = new ModeValue("Mode", "Pre", "Pre", "Post");
	private BooleanValue onRightMBHold = new BooleanValue("When holding down rmb", true);
	private SliderValue range = new SliderValue("Combo Range", 3.5, 1.0, 6.0, 0.5);
	private SliderValue chance = new SliderValue("Block Chance", 100, 0, 100, 1);
	private DoubleSliderValue hits = new DoubleSliderValue("Hits per Block", 1, 1, 1, 10, 1);
	private DoubleSliderValue preDelay = new DoubleSliderValue("Pre-Block Delay", 25, 55, 1, 500, 1);
	private DoubleSliderValue postDelay = new DoubleSliderValue("Post-Block Delay", 25, 55, 1, 500, 1);
	
	private boolean executingAction, hitCoolDown, alreadyHit, safeGuard;
	private int hitTimeout, hitsWaited;
	private Cold actionTimer = new Cold(0), postDelayTimer = new Cold(0);
	private boolean waitingForPostDelay;

	public BlockHit() {
		this.registerSetting(mode, onRightMBHold, range, chance, preDelay, hits, postDelay);
	}

	@EventLink
	public void onTick(Render3DEvent e) {
		if (!PlayerUtil.inGame())
			return;

		if (onRightMBHold.isToggled() && !PlayerUtil.tryingToCombo()) {
			if (!safeGuard || PlayerUtil.isHoldingWeapon() && Mouse.isButtonDown(0)) {
				safeGuard = true;
				finishCombo();
			}
			return;
		}
		if (waitingForPostDelay) {
			if (postDelayTimer.hasFinished()) {
				executingAction = true;
				startCombo();
				waitingForPostDelay = false;
				if (safeGuard)
					safeGuard = false;
				actionTimer.start();
			}
			return;
		}

		if (executingAction) {
			if (actionTimer.hasFinished()) {
				executingAction = false;
				finishCombo();
				return;
			} else {
				return;
			}
		}

		if (onRightMBHold.isToggled() && PlayerUtil.tryingToCombo()) {
			if (mc.objectMouseOver == null || mc.objectMouseOver.entityHit == null) {
				if (!safeGuard || PlayerUtil.isHoldingWeapon() && Mouse.isButtonDown(0)) {
					safeGuard = true;
					finishCombo();
				}
				return;
			} else {
				Entity target = mc.objectMouseOver.entityHit;
				if (target.isDead) {
					if (!safeGuard || PlayerUtil.isHoldingWeapon() && Mouse.isButtonDown(0)) {
						safeGuard = true;
						finishCombo();
					}
					return;
				}
			}
		}

		if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit instanceof Entity && Mouse.isButtonDown(0)) {
			Entity target = mc.objectMouseOver.entityHit;
			if (target.isDead) {
				if (onRightMBHold.isToggled() && Mouse.isButtonDown(1) && Mouse.isButtonDown(0)) {
					if (!safeGuard || PlayerUtil.isHoldingWeapon() && Mouse.isButtonDown(0)) {
						safeGuard = true;
						finishCombo();
					}
				}
				return;
			}

			if (mc.thePlayer.getDistanceToEntity(target) <= range.getInput()) {
				if ((target.hurtResistantTime >= 10 && mode.is("Post"))
						|| (target.hurtResistantTime <= 10 && mode.is("Pre"))) {

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

						actionTimer.setCooldown((long) ThreadLocalRandom.current().nextDouble(preDelay.getInputMin(),
								preDelay.getInputMax() + 0.01));
						if (postDelay.getInputMax() != 0) {
							postDelayTimer.setCooldown((long) ThreadLocalRandom.current()
									.nextDouble(postDelay.getInputMin(), postDelay.getInputMax() + 0.01));
							postDelayTimer.start();
							waitingForPostDelay = true;
						} else {
							executingAction = true;
							startCombo();
							actionTimer.start();
							alreadyHit = true;
							if (safeGuard)
								safeGuard = false;
						}
						alreadyHit = true;
					}
				} else {
					if (alreadyHit) {
						alreadyHit = false;
					}

					if (safeGuard)
						safeGuard = false;
				}
			}
		}
	}

	private void finishCombo() {
		KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
	}

	private void startCombo() {
		if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
			KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
		}
	}
}
