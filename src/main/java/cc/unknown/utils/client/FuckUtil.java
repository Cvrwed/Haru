package cc.unknown.utils.client;

import static cc.unknown.ui.EditHudPositionScreen.arrayListX;
import static cc.unknown.ui.EditHudPositionScreen.arrayListY;

import cc.unknown.ui.clickgui.raven.ClickGui;
import cc.unknown.utils.Loona;

public class FuckUtil implements Loona {

	private static PositionMode positionMode;

	public static final String WaifuX = "WaifuX:";
	public static final String WaifuY = "WaifuY:";

	public static PositionMode getPostitionMode(int marginX, int marginY, double height, double width) {
		int halfHeight = (int) (height / 4);
		int halfWidth = (int) width;
		PositionMode positionMode = null;

		if (marginY < halfHeight) {
			if (marginX < halfWidth) {
				positionMode = PositionMode.UPLEFT;
			}
			if (marginX > halfWidth) {
				positionMode = PositionMode.UPRIGHT;
			}
		}

		if (marginY > halfHeight) {
			if (marginX < halfWidth) {
				positionMode = PositionMode.DOWNLEFT;
			}
			if (marginX > halfWidth) {
				positionMode = PositionMode.DOWNRIGHT;
			}
		}

		return positionMode;
	}

	public enum PositionMode {
		UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT
	}
	
	public static void setArrayListX(int x) {
	    arrayListX.set(x);
	}
	
	public static void setArrayListY(int x) {
		arrayListY.set(x);
	}

	public static int getArrayListX() {
		return arrayListX.get();
	}

	public static int getArrayListY() {
		return arrayListY.get();
	}

	public static void setWaifuX(int x) {
		ClickGui.waifuX = x;
	}

	public static void setWaifuY(int y) {
		ClickGui.waifuY = y;
	}

	public static int getWaifuX() {
		return ClickGui.waifuX;
	}

	public static int getWaifuY() {
		return ClickGui.waifuY;
	}

	public static PositionMode getPositionMode() {
		return positionMode;
	}

	public static void setPositionMode(PositionMode x) {
		positionMode = x;
	}

}
