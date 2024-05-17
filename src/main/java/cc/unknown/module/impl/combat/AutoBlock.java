package cc.unknown.module.impl.combat;

import org.lwjgl.input.Mouse;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.render.RenderEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.Cold;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.settings.KeyBinding;

@Register(name = "AutoBlock", category = Category.Combat)
public class AutoBlock extends Module {
	public static DoubleSliderValue duration = new DoubleSliderValue("Block duration", 20, 100, 1, 500, 1);
	public static DoubleSliderValue distance = new DoubleSliderValue("Distance to player", 0, 3, 0, 6, 0.01);
	public static SliderValue chance = new SliderValue("Chance", 100, 0, 100, 1);
	private boolean block;
	private final Cold blockTime = new Cold(0);

	public AutoBlock() {
		this.registerSetting(duration, distance, chance);
	}

	@EventLink
	public void onPacket(RenderEvent e) {
		if (e.is3D()) {
			if (!PlayerUtil.inGame() || !PlayerUtil.isHoldingWeapon())
				return;

			if (block) {
				if ((blockTime.hasFinished() || !Mouse.isButtonDown(0)) && duration.getInputMin() <= blockTime.getTime()) {
					block = false;
					release();
				}
				return;
			}

			if (Mouse.isButtonDown(0) && mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null
					&& mc.thePlayer.getDistanceToEntity(mc.objectMouseOver.entityHit) >= distance.getInputMin()
					&& mc.objectMouseOver.entityHit != null
					&& mc.thePlayer.getDistanceToEntity(mc.objectMouseOver.entityHit) <= distance.getInputMax()
					&& (chance.getInput() == 100 || Math.random() <= chance.getInput() / 100)) {
				block = true;
				blockTime.setCooldown(duration.getInputMaxToLong());
				blockTime.start();
				press();
			}
		}

	}

	private void release() {
		KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
	}

	private void press() {
		KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
		KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
	}
}