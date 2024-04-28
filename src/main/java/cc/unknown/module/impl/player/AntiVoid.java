package cc.unknown.module.impl.player;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.Vec3;

@Register(name = "AntiVoid", category = Category.Player)
public class AntiVoid extends Module {

	private int overVoidTicks;
	private Vec3 position;
	private Vec3 motion;
	private boolean wasVoid;
	private boolean setBack;
	boolean shouldStuck;
	double x;
	double y;
	double z;
	boolean wait;

	private SliderValue fall = new SliderValue("Min fall distance", 5, 0, 10, 1);
	
	public AntiVoid() {
		this.registerSetting(fall);
	}

	@Override
	public void onDisable() {
		mc.thePlayer.isDead = false;
	}

	@EventLink
	public void onPacket(final PacketEvent e) {
		if (e.isSend()) {
			if (!mc.thePlayer.onGround && shouldStuck && e.getPacket() instanceof C03PacketPlayer
					&& !(e.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook)
					&& !(e.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook)) {
				e.setCancelled(true);
			}
			if (e.getPacket() instanceof C08PacketPlayerBlockPlacement && wait) {
				shouldStuck = false;
				mc.timer.timerSpeed = 0.2f;
				wait = false;
			}
		}
		
		if (e.isReceive()) {
			if (e.getPacket() instanceof S08PacketPlayerPosLook) {
				final S08PacketPlayerPosLook s08 = (S08PacketPlayerPosLook) e.getPacket();
				x = s08.getX();
				y = s08.getY();
				z = s08.getZ();
				mc.timer.timerSpeed = 0.2f;
			}
		}
	}

	@EventLink
	public void onUpdate(final MotionEvent event) {
		if (event.isPost()) {
			return;
		}
		if (mc.thePlayer.getHeldItem() == null) {
			mc.timer.timerSpeed = 1.0f;
		}
		if (mc.thePlayer.getHeldItem().getItem() instanceof ItemEnderPearl) {
			wait = true;
		}
		if (shouldStuck && !mc.thePlayer.onGround) {
			mc.thePlayer.motionX = 0.0;
			mc.thePlayer.motionY = 0.0;
			mc.thePlayer.motionZ = 0.0;
			mc.thePlayer.setPositionAndRotation(x, y, z, mc.thePlayer.rotationYaw,
					mc.thePlayer.rotationPitch);
		}
		final boolean overVoid = !mc.thePlayer.onGround && !PlayerUtil.isBlockUnder(30);
		if (!overVoid) {
			shouldStuck = false;
			x = mc.thePlayer.posX;
			y = mc.thePlayer.posY;
			z = mc.thePlayer.posZ;
			mc.timer.timerSpeed = 1.0f;
		}
		if (overVoid) {
			++overVoidTicks;
		} else if (mc.thePlayer.onGround) {
			overVoidTicks = 0;
		}
		if (overVoid && position != null && motion != null
				&& overVoidTicks < 30.0 + fall.getInput() * 20.0) {
			if (!setBack) {
				wasVoid = true;
				if (mc.thePlayer.fallDistance > fall.getInput() || setBack) {
					mc.thePlayer.fallDistance = 0.0f;
					setBack = true;
					shouldStuck = true;
					x = mc.thePlayer.posX;
					y = mc.thePlayer.posY;
					z = mc.thePlayer.posZ;
				}
			}
		} else {
			if (shouldStuck) {
				toggle();
			}
			shouldStuck = false;
			mc.timer.timerSpeed = 1.0f;
			setBack = false;
			if (wasVoid) {
				wasVoid = false;
			}
			motion = new Vec3(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ);
			position = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
		}
	}

}
