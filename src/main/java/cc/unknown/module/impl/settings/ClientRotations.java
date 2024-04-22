package cc.unknown.module.impl.settings;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.LivingEvent;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.network.PacketEvent.Type;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.utils.player.RotationUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

@Register(name = "ClientRotations", category = Category.Settings)
public class ClientRotations extends Module {
	public ModeValue rotationMode = new ModeValue("Mode", "Smooth", "Smooth", "Lock");
	private float playerYaw = 0f;
	private float prevHeadPitch = 0f;
	private float headPitch = 0f;
	public static ClientRotations instance;

	public ClientRotations() {
		instance = this;
		this.registerSetting(rotationMode);
	}

	@EventLink
	public void onPacket(PacketEvent e) {
		if (mc.thePlayer == null) {
			playerYaw = 0f;
			return;
		}

		Packet<?> packet = e.getPacket();

		if (e.getType() == Type.SEND) {

			if (packet instanceof C03PacketPlayer.C06PacketPlayerPosLook
					|| packet instanceof C03PacketPlayer.C05PacketPlayerLook) {
				C03PacketPlayer wrapper = (C03PacketPlayer) packet;

				playerYaw = wrapper.yaw;

				mc.thePlayer.rotationYawHead = wrapper.yaw;
			} else {
				mc.thePlayer.rotationYawHead = playerYaw;
			}
		}

	}

	@EventLink
	public void onLiving(LivingEvent e) {
		prevHeadPitch = headPitch;
		headPitch = RotationUtil.instance.getServerRotation().pitch;
		mc.thePlayer.rotationYawHead = RotationUtil.instance.getServerRotation().yaw;
	}

	public float lerp(float tickDelta, float old, float nnew) {
		return old + (nnew - old) * tickDelta;
	}

	public float getPrevHeadPitch() {
		return prevHeadPitch;
	}

	public float getHeadPitch() {
		return headPitch;
	}

	public Float getPlayerYaw() {
		return playerYaw;
	}
}
