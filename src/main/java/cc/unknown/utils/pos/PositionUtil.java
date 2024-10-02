package cc.unknown.utils.pos;

import static cc.unknown.ui.clickgui.EditHudPositionScreen.arrayListX;
import static cc.unknown.ui.clickgui.EditHudPositionScreen.arrayListY;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PositionUtil {
	protected Position currentPosition;
	
	public Position getCustomPosition(int marginX, int marginY, double height, double width) {
		int halfHeight = (int) (height / 4);
		int halfWidth = (int) width;
		Position positionMode = null;

		if (marginY < halfHeight) {
			if (marginX < halfWidth) {
				positionMode = Position.UPLEFT;
			}
			if (marginX > halfWidth) {
				positionMode = Position.UPRIGHT;
			}
		}

		if (marginY > halfHeight) {
			if (marginX < halfWidth) {
				positionMode = Position.DOWNLEFT;
			}
			if (marginX > halfWidth) {
				positionMode = Position.DOWNRIGHT;
			}
		}

		return positionMode;
	}
	
	public void setArrayListX(int x) {
	    arrayListX.set(x);
	}
	
	public void setArrayListY(int x) {
		arrayListY.set(x);
	}

	public int getArrayListX() {
		return arrayListX.get();
	}

	public int getArrayListY() {
		return arrayListY.get();
	}
	
    public Position getPositionMode() {
        return currentPosition;
    }

    public void setPositionMode(Position positionMode) {
        currentPosition = positionMode;
    }
}
