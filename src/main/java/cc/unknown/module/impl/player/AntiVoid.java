package cc.unknown.module.impl.player;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.event.impl.network.DisconnectionEvent;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.network.Packet;
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

	private ModeValue mode = new ModeValue("Mode", "Grim", "Grim", "Polar");
	private SliderValue fall = new SliderValue("Min fall distance", 5, 0, 10, 1);

	public AntiVoid() {
		this.registerSetting(mode, fall);
	}

	@EventLink
	public void onGui(ClickGuiEvent e) {
		this.setSuffix("- [" + mode.getMode() + "]");
	}

	@Override
	public void onDisable() {
		mc.timer.timerSpeed = 1.0f;
		mc.thePlayer.isDead = false;
	}

	@EventLink
	public void onPacket(final PacketEvent e) {
		Packet<?> p = e.getPacket();

		if (mode.is("Grim")) {
			if (e.isSend()) {
				if (!mc.thePlayer.onGround && shouldStuck && p instanceof C03PacketPlayer
						&& !(p instanceof C03PacketPlayer.C05PacketPlayerLook)
						&& !(p instanceof C03PacketPlayer.C06PacketPlayerPosLook)) {
					e.setCancelled(true);
				}
				if (p instanceof C08PacketPlayerBlockPlacement && wait) {
					shouldStuck = false;
					mc.timer.timerSpeed = 0.2f;
					wait = false;
				}
			}

			if (e.isReceive()) {
				if (p instanceof S08PacketPlayerPosLook) {
					final S08PacketPlayerPosLook wrapper = (S08PacketPlayerPosLook) p;
					x = wrapper.getX();
					y = wrapper.getY();
					z = wrapper.getZ();
					mc.timer.timerSpeed = 0.2f;
				}
			}
		}
	}

	@EventLink
	public void onMotion(final MotionEvent e) {
		try {
			if (e.isPre()) {
				if (mode.is("Grim")) {

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
			
			if (mode.is("Polar")) {
				e.setCancelled(true);
			}
			
		} catch (NullPointerException ex) {

		}
	}

	@EventLink
	public void onDisconnect(final DisconnectionEvent e) {
		this.disable();
	}
}
