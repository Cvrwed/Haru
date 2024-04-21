package cc.unknown.module.impl.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.PostMotionEvent;
import cc.unknown.event.impl.move.PreMotionEvent;
import cc.unknown.event.impl.move.PreUpdateEvent;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.network.PacketEvent.Type;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.event.impl.other.MouseEvent;
import cc.unknown.event.impl.player.JumpEvent;
import cc.unknown.event.impl.player.StrafeEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.player.CombatUtil;
import cc.unknown.utils.player.FriendUtil;
import cc.unknown.utils.player.PlayerUtil;
import cc.unknown.utils.player.RotationUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@SuppressWarnings("unused")
@Register(name = "KillAura", category = Category.Combat, key = Keyboard.KEY_R)
public class KillAura extends Module {
	private List<EntityLivingBase> availableTargets = new ArrayList<>();
	private AtomicBoolean block = new AtomicBoolean();
	private boolean switchTargets, swing, n, attack, blocking;
	private byte entityIndex;
	private long i, j, k, l, lastSwitched = System.currentTimeMillis(), lastAttacked = System.currentTimeMillis();
	private Random rand;
	private EntityLivingBase target;
	private double m;
	private SliderValue aps = new SliderValue("Attacks Per Second [Aps]", 16.0, 1.0, 20.0, 0.5);
	private ModeValue autoBlockMode = new ModeValue("Autoblock", "Manual", "Vanilla", "Post", "Fake", "Legit",
			"Manual");
	private SliderValue fov = new SliderValue("Field of View [Fov]", 360.0, 30.0, 360.0, 4.0);
	private SliderValue attackRange = new SliderValue("Attack Range", 3.0, 3.0, 6.0, 0.05);
	private SliderValue swingRange = new SliderValue("Swing Range", 3.3, 3.0, 8.0, 0.05);
	private SliderValue blockRange = new SliderValue("Block Range", 6.0, 3.0, 12.0, 0.05);
	private ModeValue rotationMode = new ModeValue("Rotation mode", "Silent", "Silent", "Lock view", "None");
	private ModeValue sortMode = new ModeValue("Sort mode", "Health", "HurtTime", "Distance", "Yaw", "Health");
	private SliderValue switchDelay = new SliderValue("Switch Delay", 200.0, 50.0, 1000.0, 25.0);
	private SliderValue targets = new SliderValue("Targets", 3.0, 1.0, 10.0, 1.0);
	private BooleanValue targetInvis = new BooleanValue("Target invis", true);
	private BooleanValue disableInInventory = new BooleanValue("Disable in inventory", true);
	private BooleanValue disableWhileBlocking = new BooleanValue("Disable while blocking", false);
	private BooleanValue movefix = new BooleanValue("Movement Fix", false);
	private BooleanValue fixSlotReset = new BooleanValue("Fix slot reset", false);
	private BooleanValue hitThroughBlocks = new BooleanValue("Hit through blocks", true);
	private BooleanValue ignoreTeammates = new BooleanValue("Ignore teammates", true);

	public KillAura() {
		this.registerSetting(aps, autoBlockMode, fov, attackRange, swingRange, blockRange, rotationMode, sortMode,
				switchDelay, targets, targetInvis, disableInInventory, disableWhileBlocking, movefix, fixSlotReset,
				hitThroughBlocks, ignoreTeammates);

	}

	@Override
	public void onEnable() {
		this.rand = new Random();
	}

	@Override
	public void onDisable() {
		resetVariables();
	}
	
	@EventLink
    public void onStrafe(final StrafeEvent e) {
        if (mc.currentScreen == null && this.target != null && !this.movefix.isToggled()) {
            e.setYaw(0.0f);
        }
    }
	
	@EventLink
    public void onJump(final JumpEvent e) {
        if (mc.currentScreen == null && this.target != null && !this.movefix.isToggled()) {
            e.setYaw(0.0f);
        }
    }

	@EventLink
	public void onRender(Render3DEvent e) {
		if (!PlayerUtil.inGame()) {
			return;
		}

		if (canAttack()) {
			attack = true;
		}

		if (target != null && rotationMode.is("Lock view")) {
			float[] rotations = RotationUtil.instance.getRotations(target, mc.thePlayer.rotationYaw,
					mc.thePlayer.rotationPitch);
			mc.thePlayer.rotationYaw = rotations[0];
			mc.thePlayer.rotationPitch = rotations[1];
		}
	}

	@EventLink
	public void onPreUpdate(PreUpdateEvent e) {
		if (!basicCondition()) {
			resetVariables();
			return;
		}

		block();

		if (settingCondition()) {
			if (mc.thePlayer.isBlocking() && disableWhileBlocking.isToggled()) {
				return;
			}
			if (swing && attack) {
				mc.thePlayer.swingItem();
			}
			if (target == null) {
				return;
			}
			if (attack) {
				attack = false;
				switchTargets = true;
				attackEntity(target, !swing);
				lastAttacked = System.currentTimeMillis();
			}
		}
	}

	@EventLink
	public void onPreMotion(PreMotionEvent e) {
		if (!basicCondition() || !settingCondition()) {
			resetVariables();
			return;
		}

		setTarget();
        if (target != null && rotationMode.is("Silent")) {
            float[] rotations = RotationUtil.instance.getRotations(target, e.getYaw(), e.getPitch());
            e.setYaw(rotations[0]);
            e.setPitch(rotations[1]);
        }
        
		if (autoBlockMode.is("Post") && block.get() && PlayerUtil.isHoldingWeapon()) {
			mc.thePlayer.sendQueue
					.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
			mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
		}
	}

	@EventLink
	public void onPostMotion(PostMotionEvent e) {
		if (autoBlockMode.is("Post") && block.get() && PlayerUtil.isHoldingWeapon()) {
			mc.thePlayer.sendQueue
					.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
			mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
		}
	}

	@EventLink
	public void onPacket(PacketEvent e) {
		if (!basicCondition() || !fixSlotReset.isToggled()) {
			return;
		}

		if (e.getType() == Type.RECEIVE) {

			if (PlayerUtil.isHoldingWeapon() && mc.thePlayer.isBlocking()) {
				if (e.getPacket() instanceof S2FPacketSetSlot) {
					if (mc.thePlayer.inventory.currentItem == ((S2FPacketSetSlot) e.getPacket()).func_149173_d() - 36
							&& mc.currentScreen == null) {
						if (((S2FPacketSetSlot) e.getPacket()).func_149174_e() == null
								|| (((S2FPacketSetSlot) e.getPacket()).func_149174_e().getItem() != mc.thePlayer
										.getHeldItem().getItem())) {
							return;
						}
						e.setCancelled(true);
					}
				}
			}
		}
	}

	@EventLink
	public void onMouse(final MouseEvent e) {
		if (e.getButton() == 0) {
			if (target != null || swing) {
				e.setCancelled(true);
			}
		} else if (e.getButton() == 1 && autoBlockMode.is("Vanilla") && PlayerUtil.isHoldingWeapon() && block.get()) {
			if (target == null && mc.objectMouseOver != null) {
				if (mc.objectMouseOver.entityHit != null) {
					return;
				}
				final BlockPos getBlockPos = mc.objectMouseOver.getBlockPos();
				if (getBlockPos != null) {
					return;
				}
			}
			e.setCancelled(true);
		}
	}

	@EventLink
	public void onGui(ClickGuiEvent e) {
		this.setSuffix(rotationMode.getMode());
	}

	private void resetVariables() {
		target = null;
		availableTargets.clear();
		block.set(false);
		swing = false;
		attack = false;
		this.i = 0L;
		this.j = 0L;
		block();
	}

	private void setTarget() {
		availableTargets.clear();
		block.set(false);
		swing = false;
		for (EntityPlayer entity : mc.theWorld.playerEntities) {
			if (availableTargets.size() > targets.getInput()) {
				continue;
			}
			if (entity == null) {
				continue;
			}
			if (entity == mc.thePlayer) {
				continue;
			}
			if (FriendUtil.instance.isAFriend(entity)) {
				continue;
			}
			if (entity.deathTime != 0) {
				continue;
			}
			if (CombatUtil.instance.isATeamMate(entity) && ignoreTeammates.isToggled()) {
				continue;
			}
			if (entity.isInvisible() && !targetInvis.isToggled()) {
				continue;
			}
			if (!mc.thePlayer.canEntityBeSeen(entity) && !hitThroughBlocks.isToggled()) {
				continue;
			}
			final float n = (float) fov.getInput();
			if (n != 360.0f && !PlayerUtil.fov(entity, n)) {
				continue;
			}
			double distance = mc.thePlayer.getDistanceSqToEntity(entity);

			if (distance <= blockRange.getInput() * blockRange.getInput()) {
				block.set(true);
			}

			if (distance <= swingRange.getInput() * swingRange.getInput()) {
				swing = true;
			}

			if (distance > attackRange.getInput() * swingRange.getInput()) {
				continue;
			}
			availableTargets.add(entity);
		}
		if (Math.abs(System.currentTimeMillis() - lastSwitched) > switchDelay.getInput() && switchTargets) {
			switchTargets = false;
			if (entityIndex < availableTargets.size() - 1) {
				entityIndex++;
			} else {
				entityIndex = 0;
			}
			lastSwitched = System.currentTimeMillis();
		}
		if (!availableTargets.isEmpty()) {
			Comparator<EntityLivingBase> comparator = null;
			switch (sortMode.getMode()) {
			case "Health":
				comparator = Comparator.comparingDouble(entityPlayer -> (double) entityPlayer.getHealth());
				break;
			case "HurtTime":
				comparator = Comparator.comparingDouble(entityPlayer2 -> (double) entityPlayer2.hurtTime);
				break;
			case "Distance":
				comparator = Comparator.comparingDouble(entity -> mc.thePlayer.getDistanceSqToEntity(entity));
				break;
			case "Yaw":
				comparator = Comparator
						.comparingDouble(entity2 -> RotationUtil.instance.distanceFromYaw(entity2, false));
				break;
			}
			Collections.sort(availableTargets, comparator);
			if (entityIndex > availableTargets.size() - 1) {
				entityIndex = 0;
			}
			target = availableTargets.get(entityIndex);
		} else {
			target = null;
		}
	}

	private boolean basicCondition() {
		if (!PlayerUtil.inGame()) {
			return false;
		}
		if (mc.thePlayer.isDead) {
			return false;
		}
		return true;
	}

	private boolean settingCondition() {
		if (mc.currentScreen != null && disableInInventory.isToggled()) {
			return false;
		}
		return true;
	}

	private boolean canAttack() {
		if (this.j > 0L && this.i > 0L) {
			if (System.currentTimeMillis() > this.j) {
				this.gd();
				return true;
			} else if (System.currentTimeMillis() > this.i) {
				return false;
			}
		} else {
			this.gd();
		}
		return false;
	}

	public void gd() {
		double c = aps.getInput() + 0.4D * this.rand.nextDouble();
		long d = (long) ((int) Math.round(1000.0D / c));
		if (System.currentTimeMillis() > this.k) {
			if (!this.n && this.rand.nextInt(100) >= 85) {
				this.n = true;
				this.m = 1.1D + this.rand.nextDouble() * 0.15D;
			} else {
				this.n = false;
			}

			this.k = System.currentTimeMillis() + 500L + (long) this.rand.nextInt(1500);
		}

		if (this.n) {
			d = (long) ((double) d * this.m);
		}

		if (System.currentTimeMillis() > this.l) {
			if (this.rand.nextInt(100) >= 80) {
				d += 50L + (long) this.rand.nextInt(100);
			}

			this.l = System.currentTimeMillis() + 500L + (long) this.rand.nextInt(1500);
		}

		this.j = System.currentTimeMillis() + d;
		this.i = System.currentTimeMillis() + d / 2L - (long) this.rand.nextInt(10);
	}

	private void block() {
		if (!block.get() && !blocking) {
			return;
		}

		if (!PlayerUtil.isHoldingWeapon()) {
			block.set(false);
		}
		switch (autoBlockMode.getMode()) {
		case "Vanilla":
			setBlockState(block.get(), true, true);
			break;
		case "Post":
			setBlockState(block.get(), false, true);
			break;
		case "Fake":
			setBlockState(block.get(), false, false);
			break;
		case "Legit":
			boolean down = (target == null || target.hurtTime >= 5) && block.get();
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), down);
			mc.gameSettings.keyBindUseItem.pressed = down;
			blocking = down;
		}
	}

	private void setBlockState(boolean state, boolean sendBlock, boolean sendUnBlock) {
		if (PlayerUtil.inGame()) {
			if (sendBlock && !blocking && state && PlayerUtil.isHoldingWeapon()) {
				mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
			} else if (sendUnBlock && blocking && !state) {
				unBlock();
			}
		}
		blocking = setBlocking(state);
	}

	private void unBlock() {
		if (!PlayerUtil.isHoldingWeapon()) {
			return;
		}
		mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
				BlockPos.ORIGIN, EnumFacing.DOWN));
	}

	private void attackEntity(Entity e, boolean clientSwing) {
		if (clientSwing) {
			mc.thePlayer.swingItem();
		}
		mc.playerController.attackEntity(mc.thePlayer, e);
	}

	private boolean setBlocking(boolean blocking) {
		mc.thePlayer.itemInUseCount = blocking ? 1 : 0;
		return blocking;
	}
}
