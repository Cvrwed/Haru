package cc.unknown.module.impl.combat;

import java.awt.Color;

import org.lwjgl.input.Mouse;

import cc.unknown.event.impl.api.EventLink;
import cc.unknown.event.impl.move.PreUpdateEvent;
import cc.unknown.event.impl.player.GameLoopEvent;
import cc.unknown.event.impl.player.LookEvent;
import cc.unknown.event.impl.player.StrafeEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.utils.client.AdvancedTimer;
import cc.unknown.utils.client.RenderUtil;
import cc.unknown.utils.helpers.MathHelper;
import cc.unknown.utils.misc.ClickUtil;
import cc.unknown.utils.player.CombatUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

public class KillAura extends Module {

	private EntityPlayer target;

	private DoubleSliderValue cps = new DoubleSliderValue("CPS", 9, 13, 1, 60, 0.5);
	private BooleanValue fixMovement = new BooleanValue("Move Fix", true);
	private ModeValue rotationMode = new ModeValue("RotationMode", "Default", "None", "Default", "Optimo");
	private AdvancedTimer coolDown = new AdvancedTimer(1);
	private boolean leftDown, leftn, locked;
	private long leftDownTime, leftUpTime, leftk, leftl;
	private float yaw, pitch, prevYaw, prevPitch;
	private double leftm;

	public KillAura() {
		super("KillAura", ModuleCategory.Combat);
		this.registerSetting(cps, fixMovement, rotationMode);
	}

	@EventLink
	public void onGameLoop(GameLoopEvent e) {
		if (!PlayerUtil.inGame())
			return;
		Mouse.poll();
		EntityPlayer pTarget = CombatUtil.instance.getTarget();
		if (pTarget == null || mc.currentScreen != null || !coolDown.hasFinished()) {
			target = null;
			rotate(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
			return;
		}
		target = pTarget;
		this.leftClickExecute(mc.gameSettings.keyBindAttack.getKeyCode());
		float[] rotations;
		if (rotationMode.is("Default"))
			rotations = getTargetRotations(target, 0);
		else if (rotationMode.is("Optimo")) {
			Vec3 pos = new Vec3(0, 0, 0);
			rotations = getRotations(pos.xCoord, pos.yCoord, pos.zCoord);
		} else {
			rotations = new float[] { mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch };
		}
		locked = false;
		rotate(rotations[0], rotations[1]);
	}

	@EventLink
	public void onPre(PreUpdateEvent e) {
		if (!PlayerUtil.inGame() || locked) {
			return;
		}

		float[] currentRots = new float[] { yaw, pitch };
		float[] prevRots = new float[] { prevYaw, prevPitch };
		float[] gcdPatch = getPatchedRots(currentRots, prevRots);

		e.setYaw(gcdPatch[0]);
		e.setPitch(gcdPatch[1]);

		mc.thePlayer.renderYawOffset = gcdPatch[0];
		mc.thePlayer.rotationYawHead = gcdPatch[0];

		prevYaw = e.getYaw();
		prevPitch = e.getPitch();

	}

	@EventLink
	public void onRender(Render3DEvent e) {
		if (target == null || !PlayerUtil.inGame())
			return;
		int red = (int) (((20 - target.getHealth()) * 13) > 255 ? 255 : (20 - target.getHealth()) * 13);
		int green = 255 - red;
		final int rgb = new Color(red, green, 0).getRGB();
		RenderUtil.drawBoxAroundEntity(target, 2, 0, 0, rgb, false);
	}

	private void rotate(float yaw, float pitch) {
		this.yaw = yaw;
		this.pitch = pitch;
	}

	private double MouseSens() {
		final float sens = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
		final float pow = sens * sens * sens * 8.0F;
		return pow * 0.15D;
	}

	private float[] getPatchedRots(final float[] currentRots, final float[] prevRots) {
		final float yawDif = currentRots[0] - prevRots[0];
		final float pitchDif = currentRots[1] - prevRots[1];
		final double gcd = MouseSens();

		currentRots[0] -= (float) (yawDif % gcd);
		currentRots[1] -= (float) (pitchDif % gcd);
		return currentRots;
	}

	@EventLink
	public void onStrafe(StrafeEvent e) {
		if (!fixMovement.isToggled() || locked || !PlayerUtil.inGame())
			return;
		e.setYaw(yaw);
	}

	@EventLink
	public void lookEvent(LookEvent e) {
		if (locked || !PlayerUtil.inGame())
			return;
		e.setPrevYaw(prevYaw);
		e.setPrevPitch(prevPitch);
		e.setYaw(yaw);
		e.setPitch(pitch);
	}

	private void leftClickExecute(int key) {
		if (!PlayerUtil.inGame())
			return;
		if ((this.leftUpTime > 0L) && (this.leftDownTime > 0L)) {
			if ((System.currentTimeMillis() > this.leftUpTime) && leftDown) {
				if (mc.thePlayer.isUsingItem())
					mc.thePlayer.stopUsingItem();
				KeyBinding.onTick(key);
				this.genLeftTimings();
				leftDown = false;
			} else if (System.currentTimeMillis() > this.leftDownTime) {
				if (Mouse.isButtonDown(1))
					KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
				leftDown = true;
			}
		} else
			this.genLeftTimings();

	}

	private void genLeftTimings() {
		double clickSpeed = ClickUtil.instance.ranModuleVal(cps, MathHelper.rand())
				+ (0.4D * MathHelper.rand().nextDouble());
		long delay = (int) Math.round(1000.0D / clickSpeed);
		if (System.currentTimeMillis() > this.leftk) {
			if (!this.leftn && (MathHelper.rand().nextInt(100) >= 85)) {
				this.leftn = true;
				this.leftm = 1.1D + (MathHelper.rand().nextDouble() * 0.15D);
			} else
				this.leftn = false;

			this.leftk = System.currentTimeMillis() + 500L + MathHelper.rand().nextInt(1500);
		}

		if (this.leftn)
			delay = (long) (delay * this.leftm);

		if (System.currentTimeMillis() > this.leftl) {
			if (MathHelper.rand().nextInt(100) >= 80)
				delay += 50L + MathHelper.rand().nextInt(100);

			this.leftl = System.currentTimeMillis() + 500L + MathHelper.rand().nextInt(1500);
		}

		this.leftUpTime = System.currentTimeMillis() + delay;
		this.leftDownTime = (System.currentTimeMillis() + (delay / 2L)) - MathHelper.rand().nextInt(10);
	}

	private float[] getTargetRotations(Entity entityIn, float ps) {
		if (entityIn == null)
			return null;
		double diffX = entityIn.posX - mc.thePlayer.posX;
		double diffY;
		if (entityIn instanceof EntityLivingBase) {
			EntityLivingBase en = (EntityLivingBase) entityIn;
			diffY = (en.posY + ((double) en.getEyeHeight() * 0.9D))
					- (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());
		} else
			diffY = (((entityIn.getEntityBoundingBox().minY + entityIn.getEntityBoundingBox().maxY) / 2.0D) + ps)
					- (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());

		double diffZ = entityIn.posZ - mc.thePlayer.posZ;
		double dist = MathHelper.sqrt_double((diffX * diffX) + (diffZ * diffZ));
		float yaw = (float) ((Math.atan2(diffZ, diffX) * 180.0D) / 3.141592653589793D) - 90.0F;
		float pitch = (float) (-((Math.atan2(diffY, dist) * 180.0D) / 3.141592653589793D));
		return new float[] { mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw),
				mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch) };
	}

	private float[] getRotations(double x, double y, double z) {
		double diffX = x - mc.thePlayer.posX;
		double diffY = y - mc.thePlayer.posY;

		double diffZ = z - mc.thePlayer.posZ;

		double dist = MathHelper.sqrt_double((diffX * diffX) + (diffZ * diffZ));
		float yaw = (float) ((Math.atan2(diffZ, diffX) * 180.0D) / 3.141592653589793D) - 90.0F;
		float pitch = (float) (-((Math.atan2(diffY, dist) * 180.0D) / 3.141592653589793D));
		return new float[] { mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw),
				mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch) };
	}
}