package cc.unknown.utils.misc;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.input.Mouse;

import cc.unknown.Haru;
import cc.unknown.module.impl.combat.AutoClick;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.interfaces.Loona;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;

public enum ClickUtil implements Loona {
	instance;
	
	private long leftk;
	private long leftl;
	private double leftm;
	private boolean leftn;
	private boolean breakHeld;
	private long lastLeftClick;
	private long leftHold;
	private boolean leftDown;
	private long righti;
	private long rightj;
	private long rightk;
	private long rightl;
	private double rightm;
	private boolean rightn;
	private long lastRightClick;
	private long rightHold;
	private boolean rightDown;
	private long leftDownTime;
	private long leftUpTime;
	private Random rand = null;
	
	public void megumiLeftClick() {
		AutoClick clicker = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);
		
		double speedLeft1 = 1.0 / ThreadLocalRandom.current().nextGaussian() * clicker.getLeftCPS().getInputMax()
				+ clicker.getLeftCPS().getInputMax();
		double leftHoldLength = speedLeft1 / ThreadLocalRandom.current().nextGaussian() * clicker.getLeftCPS().getInputMin()
				+ clicker.getLeftCPS().getInputMin();
		Mouse.poll();

		if (mc.currentScreen != null || !mc.inGameHasFocus) {
			return;
		}

		if (Mouse.isButtonDown(0)) {
			if (breakBlockLogic() || (clicker.getWeaponOnly().isToggled() && !PlayerUtil.isHoldingWeapon())) {
				return;
			}

			double speedLeft = 1.0
					/ ThreadLocalRandom.current().nextDouble(clicker.getLeftCPS().getInputMin() - 0.2D, clicker.getLeftCPS().getInputMax());
			if (System.currentTimeMillis() - lastLeftClick > speedLeft * 1000) {
				lastLeftClick = System.currentTimeMillis();
				if (leftHold < lastLeftClick) {
					leftHold = lastLeftClick;
				}
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), true);
				KeyBinding.onTick(mc.gameSettings.keyBindAttack.getKeyCode());

			} else if (leftHold > leftHoldLength * 1000) {
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
			}
		}
	}

	public void kuruLeftClick() {
		AutoClick clicker = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);

		double speedLeft1 = 1.0
				/ ThreadLocalRandom.current().nextDouble(clicker.getLeftCPS().getInputMin() - 0.2D, clicker.getLeftCPS().getInputMax());
		double leftHoldLength = speedLeft1
				/ ThreadLocalRandom.current().nextDouble(clicker.getLeftCPS().getInputMin() - 0.02D, clicker.getLeftCPS().getInputMax());

		Mouse.poll();

		if (mc.currentScreen != null || !mc.inGameHasFocus) {
			return;
		}

		if (Mouse.isButtonDown(0)) {
			if (breakBlockLogic() || (clicker.getWeaponOnly().isToggled() && !PlayerUtil.isHoldingWeapon())) {
				return;
			}

			double speedLeft = 1.0
					/ ThreadLocalRandom.current().nextDouble(clicker.getLeftCPS().getInputMin() - 0.2, clicker.getLeftCPS().getInputMax());
			if (System.currentTimeMillis() - lastLeftClick > speedLeft * 1000) {
				lastLeftClick = System.currentTimeMillis();
				if (leftHold < lastLeftClick) {
					leftHold = lastLeftClick;
				}
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), true);
				KeyBinding.onTick(mc.gameSettings.keyBindAttack.getKeyCode());

			} else if (System.currentTimeMillis() - leftHold > leftHoldLength * 1000) {
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);

			}
		}
	}

	public void ravenLeftClick() {
		AutoClick clicker = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);

		if (mc.currentScreen != null || !mc.inGameHasFocus) {
			return;
		}

		Mouse.poll();
		if (!Mouse.isButtonDown(0) && !leftDown) {
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);

		}
		if (Mouse.isButtonDown(0) || leftDown) {
			if (clicker.getWeaponOnly().isToggled() && !PlayerUtil.isHoldingWeapon()) {
				return;
			}
			this.leftClickExecute(mc.gameSettings.keyBindAttack.getKeyCode());
		}
	}

	public void leftClickExecute(int key) {

		if (breakBlockLogic())
			return;

		if (this.leftUpTime > 0L && this.leftDownTime > 0L) {
			if (System.currentTimeMillis() > this.leftUpTime && leftDown) {
				KeyBinding.setKeyBindState(key, true);
				KeyBinding.onTick(key);
				this.genLeftTimings();
				leftDown = false;
			} else if (System.currentTimeMillis() > this.leftDownTime) {
				KeyBinding.setKeyBindState(key, false);
				leftDown = true;
			}
		} else {
			this.genLeftTimings();
		}

	}

	public void genLeftTimings() {
		AutoClick clicker = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);

		double clickSpeed = ranModuleVal(clicker.getLeftCPS(), this.rand) + 0.4D * this.rand.nextDouble(); // 0.4D
		long delay = (int) Math.round(1000.0D / clickSpeed);
		if (System.currentTimeMillis() > this.leftk) {
			if (!this.leftn && this.rand.nextInt(100) >= 85) { // 85
				this.leftn = true;
				this.leftm = 1.1D + this.rand.nextDouble() * 0.15D; // 1.1 | 0.15 
			} else {
				this.leftn = false;
			}

			this.leftk = System.currentTimeMillis() + 500L + (long) this.rand.nextInt(1500); // 1500
		}

		if (this.leftn) {
			delay = (long) ((double) delay * this.leftm);
		}

		if (System.currentTimeMillis() > this.leftl) {
			if (this.rand.nextInt(100) >= 80) { // 80
				delay += 50L + (long) this.rand.nextInt(100); // 100
			}

			this.leftl = System.currentTimeMillis() + 500L + (long) this.rand.nextInt(1500);
		}

		this.leftUpTime = System.currentTimeMillis() + delay;
		this.leftDownTime = delay / 2L - (long) this.rand.nextInt(10); // 10
	}

	public boolean hitSelectLogic() {
		AutoClick clicker = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);

		if (!clicker.getHitSelect().isToggled())
			return false;
		if (mc.objectMouseOver != null
				&& mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
			Entity target = mc.objectMouseOver.entityHit;
			if (target instanceof EntityPlayer) {
				EntityPlayer targetPlayer = (EntityPlayer) target;
				return clicker.getHitSelect().isToggled()
						&& PlayerUtil.lookingAtPlayer(mc.thePlayer, targetPlayer, clicker.getHitSelectDistance().getInput());
			}
		}
		return false;
	}

	public boolean breakBlockLogic() {
		AutoClick clicker = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);

		if (clicker.getBreakBlocks().isToggled() && mc.objectMouseOver != null) {
			BlockPos p = mc.objectMouseOver.getBlockPos();

			if (p != null) {
				Block bl = mc.theWorld.getBlockState(p).getBlock();
				if (bl != Blocks.air && !(bl instanceof BlockLiquid)) {
					if (!breakHeld) {
						int e = mc.gameSettings.keyBindAttack.getKeyCode();
						KeyBinding.setKeyBindState(e, true);
						KeyBinding.onTick(e);
						breakHeld = true;
					}
					return true;
				}
				if (breakHeld) {
					breakHeld = false;
				}
			}
		}
		return false;
	}
	
	public void kuruRightClick() {
		AutoClick right = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);

		if (mc.currentScreen != null || !mc.inGameHasFocus)
			return;
		
		double speedRight = 1.0 / ThreadLocalRandom.current().nextDouble(right.getRightCPS().getInputMin() - 0.2D, right.getRightCPS().getInputMax());
		double rightHoldLength = speedRight / ThreadLocalRandom.current().nextDouble(right.getRightCPS().getInputMin() - 0.02D, right.getRightCPS().getInputMax());

		if(!Mouse.isButtonDown(1) && !rightDown) {
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
		}
		
		if (Mouse.isButtonDown(1) || rightDown) {
			if (!this.rightClickAllowed())
				return;

			if (System.currentTimeMillis() - lastRightClick > speedRight * 1000) {
				lastRightClick = System.currentTimeMillis();
				if (rightHold < lastRightClick){
					rightHold = lastRightClick;
				}

				KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
				KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
				rightDown = false;
			} else if (System.currentTimeMillis() - rightHold > rightHoldLength * 1000) {
				rightDown = true;
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
			}
		}
	}
	
	public void megumiRightClick() {
		AutoClick right = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);
		
		if (mc.currentScreen != null || !mc.inGameHasFocus)
			return;
		
		double speedRight = 1.0 / ThreadLocalRandom.current().nextGaussian() * right.getRightCPS().getInputMin() - 0.2D + right.getRightCPS().getInputMax();
		double rightHoldLength = speedRight / ThreadLocalRandom.current().nextGaussian() * right.getRightCPS().getInputMin() - 0.02D + right.getRightCPS().getInputMax();
		
		if(!Mouse.isButtonDown(1) && !rightDown) {
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
		}
		
		if (Mouse.isButtonDown(1) || rightDown) {
			if (!this.rightClickAllowed())
				return;
			
			if (System.currentTimeMillis() - lastRightClick > speedRight * 1000) {
				lastRightClick = System.currentTimeMillis();
				if (rightHold < lastRightClick){
					rightHold = lastRightClick;
				}
				
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
				KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
				rightDown = false;
			} else if (System.currentTimeMillis() - rightHold > rightHoldLength * 1000) {
				rightDown = true;
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
			}
		}
	}

	public void ravenRightClick() {
		if (mc.currentScreen != null || !mc.inGameHasFocus)
			return;

		Mouse.poll();
		if (Mouse.isButtonDown(1)) {
			this.rightClickExecute(mc.gameSettings.keyBindUseItem.getKeyCode());
		} else if (!Mouse.isButtonDown(1)) {
			this.righti = 0L;
			this.rightj = 0L;
		}
	}

	public boolean rightClickAllowed() {
		AutoClick right = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);

		ItemStack item = mc.thePlayer.getHeldItem();
		if (item != null) {
			
			if (item.getItem() instanceof ItemSword) {
				return false;
			} else if(item.getItem() instanceof ItemFishingRod) {
				return false;	
			} else if (item.getItem() instanceof ItemBow) {
				return false;	
			}
			
			if (right.getAllowEat().isToggled()) {
				if ((item.getItem() instanceof ItemFood) || item.getItem() instanceof ItemPotion || item.getItem() instanceof ItemBucketMilk) {
					return false;
				}
			}

			if (right.getOnlyBlocks().isToggled()) {
				if (!(item.getItem() instanceof ItemBlock)) {
					return false;
				}
			}		
		}

		return true;
	}	

	private void rightClickExecute(int key) {
		if (!this.rightClickAllowed())
			return;

		if (this.rightj > 0L && this.righti > 0L) {
			if (System.currentTimeMillis() > this.rightj) {
				KeyBinding.setKeyBindState(key, true);
				KeyBinding.onTick(key);
				this.genRightTimings();
			} else if (System.currentTimeMillis() > this.righti) {
				KeyBinding.setKeyBindState(key, false);
			}
		} else {
			this.genRightTimings();
		}
	}

	public void genRightTimings() {
		AutoClick right = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);

		double clickSpeed = ranModuleVal(right.getRightCPS(), this.rand) + 0.4D * this.rand.nextDouble();
		long delay = (int)Math.round(1000.0D / clickSpeed);
		if (System.currentTimeMillis() > this.rightk) {
			if (!this.rightn && this.rand.nextInt(100) >= 85) {
				this.rightn = true;
				this.rightm = 1.1D + this.rand.nextDouble() * 0.15D;
			} else {
				this.rightn = false;
			}
			
			this.rightk = System.currentTimeMillis() + 500L + (long)this.rand.nextInt(1500);
		}

		if (this.rightn) {
			delay = (long)((double)delay * this.rightm);
		}

		if (System.currentTimeMillis() > this.rightl) {
			if (this.rand.nextInt(100) >= 80) {
				delay += 50L + (long)this.rand.nextInt(100);
			}

			this.rightl = System.currentTimeMillis() + 500L + (long)this.rand.nextInt(1500);
		}

		this.rightj = System.currentTimeMillis() + delay;
		this.righti = System.currentTimeMillis() + delay / 2L - (long)this.rand.nextInt(10);
	}
	
    public boolean isClicking() {
   	 AutoClick clicker = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);
   	 if (clicker != null && clicker.isEnabled()) {
           return clicker.isEnabled() && Mouse.isButtonDown(0);
        }
   	 return false;
    }
    
    public double ranModuleVal(SliderValue a, SliderValue b, Random r) {
       return a.getInput() == b.getInput() ? a.getInput() : a.getInput() + r.nextDouble() * (b.getInput() - a.getInput());
    }

    public double ranModuleVal(DoubleSliderValue a, Random r) {
       return a.getInputMin() == a.getInputMax() ? a.getInputMin() : a.getInputMin() + r.nextDouble() * (a.getInputMax() - a.getInputMin());
    }

	public void setLeftDownTime(long leftDownTime) {
		this.leftDownTime = leftDownTime;
	}

	public void setLeftUpTime(long leftUpTime) {
		this.leftUpTime = leftUpTime;
	}

	public void setRand(Random rand) {
		this.rand = rand;
	}
}
