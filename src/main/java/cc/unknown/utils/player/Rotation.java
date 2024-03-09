package cc.unknown.utils.player;

import cc.unknown.utils.Loona;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Rotation implements Loona {
    private float yaw;
    private float pitch;
    
    private float prevHeadPitch = 0f;
    private float headPitch = 0f;

    public Rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

}
