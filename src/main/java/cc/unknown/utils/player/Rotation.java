package cc.unknown.utils.player;

import cc.unknown.utils.interfaces.Loona;
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

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getPrevHeadPitch() {
		return prevHeadPitch;
	}

	public void setPrevHeadPitch(float prevHeadPitch) {
		this.prevHeadPitch = prevHeadPitch;
	}

	public float getHeadPitch() {
		return headPitch;
	}

	public void setHeadPitch(float headPitch) {
		this.headPitch = headPitch;
	}

}
