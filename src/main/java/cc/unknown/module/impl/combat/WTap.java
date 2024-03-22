package cc.unknown.module.impl.combat;

import org.lwjgl.input.Mouse;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.UpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.Cold;
import cc.unknown.utils.helpers.MathHelper;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

public class WTap extends Module {

	private ModeValue mode = new ModeValue("Mode", "Post", "Pre", "Post");
	private SliderValue range = new SliderValue("Combo range", 4, 0, 6, 0.1);
	private SliderValue chance = new SliderValue("Tap chance", 50, 0, 100, 1);
	private DoubleSliderValue delay = new DoubleSliderValue("Tap delay", 50, 100, 0, 300, 5);
	private Cold timer = new Cold();

	public WTap() {
		super("WTap", ModuleCategory.Combat);
		this.registerSetting(mode, range, chance, delay);
	}

	@EventLink
	public void onUpdate(UpdateEvent e) {
		if (PlayerUtil.inGame() && isLookingAtPlayer() && Mouse.isButtonDown(0) && mc.thePlayer.moveForward > 0 && mc.currentScreen == null) {
			if (mc.thePlayer.hurtResistantTime >= 10 && mode.is("Post") || (mc.thePlayer.hurtResistantTime <= 10 && mode.is("Pre"))) {

				if (chance.getInput() != 100.0D) {
					double ch = Math.random() * 100;
					if (ch >= chance.getInput()) {
						return;
					}
				}

				if (timer.elapsed(MathHelper.randomInt(delay.getInputMinToInt(), delay.getInputMaxToInt()))) {
					KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
					KeyBinding.onTick(mc.gameSettings.keyBindForward.getKeyCode());
					timer.reset();
					rePress();
				}
			}
		}
	}

	private void rePress() {
		if (mc.thePlayer.moveForward > 0) {
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
			KeyBinding.onTick(mc.gameSettings.keyBindForward.getKeyCode());
		}
	}

	private boolean isLookingAtPlayer() {
		MovingObjectPosition result = mc.objectMouseOver;
		if (result != null && result.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY
				&& result.entityHit instanceof EntityPlayer) {
			EntityPlayer targetPlayer = (EntityPlayer) result.entityHit;
			return PlayerUtil.lookingAtPlayer(mc.thePlayer, targetPlayer, range.getInput());
		}
		return false;
	}
}
