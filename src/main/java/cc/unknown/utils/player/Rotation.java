package cc.unknown.utils.player;

import cc.unknown.utils.Loona;

public class Rotation implements Loona {
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

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public void setPrevHeadPitch(float prevHeadPitch) {
		this.prevHeadPitch = prevHeadPitch;
	}

	public void setHeadPitch(float headPitch) {
		this.headPitch = headPitch;
	}
}
