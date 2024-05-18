package cc.unknown.module.impl.combat;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.event.impl.network.KnockBackEvent;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.helpers.MathHelper;
import cc.unknown.utils.network.PacketUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

@Register(name = "Velocity", category = Category.Combat)
public class Velocity extends Module {

	public ModeValue mode = new ModeValue("Mode", "S12Packet", "S12Packet", "Verus", "Ground Grim", "Polar", "Minemen");
	public SliderValue horizontal = new SliderValue("Horizontal", 90, -100, 100, 1);
	public SliderValue vertical = new SliderValue("Vertical", 100, -100, 100, 1);
	public SliderValue chance = new SliderValue("Chance", 100, 0, 100, 1);
	private BooleanValue onlyCombat = new BooleanValue("Only During Combat", false);
	private BooleanValue onlyGround = new BooleanValue("Only While on Ground", false);
	private boolean reset;
	private int timerTicks = 0;

	public Velocity() {
		this.registerSetting(mode, horizontal, vertical, chance, onlyCombat, onlyGround);
	}

	@EventLink
	public void onGui(ClickGuiEvent e) {
		this.setSuffix("- [" + mode.getMode() + "]");
	}

	@Override
	public void onDisable() {
		mc.timer.timerSpeed = 1.0f;
		timerTicks = 0;
		reset = false;
	}

	@EventLink
	public void onKnockBack(KnockBackEvent e) {
		if (chance.getInput() != 100.0D) {
			if (Math.random() >= chance.getInput() / 100.0D) {
				return;
			}
		}
		
		if (mode.is("S12Packet")) {
			e.setX(e.getX() * horizontal.getInput() / 100.0);
			e.setY(e.getY() * vertical.getInput() / 100.0);
			e.setZ(e.getZ() * horizontal.getInput() / 100.0);
		}

		if (mode.is("Ground Grim") && PlayerUtil.isMoving() && mc.thePlayer.onGround) {
			e.setCancelled(true);
			reset = true;
		}
	}
	
	@EventLink
	public void onTick(TickEvent e) {
		if (mode.is("Ground Grim")) {

			if (timerTicks > 0 && mc.timer.timerSpeed <= 1) {
				float timerSpeed = 0.8f + (0.2f * (20 - timerTicks) / 20);
				mc.timer.timerSpeed = Math.min(timerSpeed, 1f);
				timerTicks--;
			} else if (mc.timer.timerSpeed <= 1) {
				mc.timer.timerSpeed = 1f;
			}

			if (reset) {
				BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
				if (checkAir(pos)) {
					reset = false;
				}
			}
		}
	}

	@EventLink
	public void onPre(MotionEvent e) {
		if (PlayerUtil.inGame() && e.isPre()) {
			if (mode.is("Verus") && mc.thePlayer.hurtTime == 10 - MathHelper.randomInt(3, 4)) {
				mc.thePlayer.motionX = 0.0D;
				mc.thePlayer.motionY = 0.0D;
				mc.thePlayer.motionZ = 0.0D;
			}
		}
	}

	private boolean checkAir(BlockPos blockPos) {
		World world = mc.theWorld;
		if (world == null)
			return false;

		if (!world.isAirBlock(blockPos))
			return false;

		timerTicks = 20;

		EntityPlayerSP player = mc.thePlayer;
		if (player != null) {
			PacketUtil.sendPacketSilent(new C03PacketPlayer(true));
			PacketUtil.sendPacketSilent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
					blockPos, EnumFacing.DOWN));
		}

		world.setBlockToAir(blockPos);

		return true;
	}
}
