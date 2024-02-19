package cc.unknown.utils.player;

public class Rotation {
    private float yaw;
    private float pitch;
    
    private float prevHeadPitch = 0f;
    private float headPitch = 0f;

    public Rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public float getPrevHeadPitch() {
		return prevHeadPitch;
	}

	public float getHeadPitch() {
		return headPitch;
	}
}
