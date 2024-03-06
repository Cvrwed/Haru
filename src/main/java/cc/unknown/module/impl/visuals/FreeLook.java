package cc.unknown.module.impl.visuals;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import cc.unknown.event.impl.api.EventLink;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;

public class FreeLook extends Module {

	private static boolean perspectiveToggled = false;
	private static float cameraYaw = 0f;
	private static float cameraPitch = 0f;
	private int previousPerspective = 0;

	public FreeLook() {
		super("FreeLook", ModuleCategory.Visuals);

	}

	@EventLink
	public void onTick(TickEvent e) {
		perspectiveToggled = !perspectiveToggled;
		cameraYaw = mc.thePlayer.rotationYaw;
		cameraPitch = mc.thePlayer.rotationPitch;

		if (Keyboard.isKeyDown(this.getKey())) {
			if (perspectiveToggled) {
				previousPerspective = mc.gameSettings.thirdPersonView;
				mc.gameSettings.thirdPersonView = 1;
			} else if (mc.currentScreen == null && this.isEnabled()) {
				mc.gameSettings.thirdPersonView = previousPerspective;
				perspectiveToggled = false;
				mc.gameSettings.thirdPersonView = previousPerspective;
			}
		}
	}

	/*@Override
	public void onDisable() {
		resetPerspective();
	}*/

	public boolean overrideMouse() {
		if (mc.inGameHasFocus && Display.isActive()) {
			if (!perspectiveToggled) {
				return true;
			}
			mc.mouseHelper.mouseXYChange();
			float f1 = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
			float f2 = f1 * f1 * f1 * 8.0f;
			float f3 = mc.mouseHelper.deltaX * f2;
			float f4 = mc.mouseHelper.deltaY * f2;
			cameraYaw += f3 * 0.15f;
			cameraPitch -= f4 * 0.15f;
			if (cameraPitch > 90)
				cameraPitch = 90f;
			if (cameraPitch < -90)
				cameraPitch = -90f;
		}
		return false;
	}

	@SuppressWarnings("unused")
	private void resetPerspective() {
		perspectiveToggled = false;
		mc.gameSettings.thirdPersonView = previousPerspective;
	}

	public static boolean isPerspectiveToggled() {
		return perspectiveToggled;
	}

	public static float getCameraYaw() {
		return cameraYaw;
	}

	public static float getCameraPitch() {
		return cameraPitch;
	}

}
