package cc.unknown.utils.player;

import org.lwjgl.input.Keyboard;

import cc.unknown.utils.Loona;
import net.minecraft.util.MathHelper;

public class MoveUtil implements Loona {
	public static float getPlayerDirection() {
		float direction = 0f;
		final boolean forward = Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode());
		final boolean back = Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode());
		final boolean left = Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode());
		final boolean right = Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode());
		if (forward && !back) {
			if (left && !right) {
				direction -= 45.0f;
			} else if (right && !left) {
				direction += 45.0f;
			}
		} else if (back && !forward) {
			if (left && !right) {
				direction -= 135.0f;
			} else if (right && !left) {
				direction += 135.0f;
			} else {
				direction -= 180.0f;
			}
		} else if (left && !right) {
			direction -= 90.0f;
		} else if (right && !left) {
			direction += 90.0f;
		}
		return MathHelper.wrapAngleTo180_float(direction);
	}
}
