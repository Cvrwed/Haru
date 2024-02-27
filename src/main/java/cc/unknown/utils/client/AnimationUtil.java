package cc.unknown.utils.client;

public class AnimationUtil {

	public static double delta = 1000 / 60d;

	public static float getAnimationState(float animation, float finalState, float speed) {
		final float add = (float) (delta * (speed / 1000f));
		if (animation < finalState) {
			if (animation + add < finalState) {
				animation += add;
			} else {
				animation = finalState;
			}
		} else if (animation - add > finalState) {
			animation -= add;
		} else {
			animation = finalState;
		}
		return animation;
	}

	public static float smoothAnimation(float ani, float finalState, float speed, float scale) {
		return getAnimationState(ani, finalState, Math.max(10, (Math.abs(ani - finalState)) * speed) * scale);
	}
}
