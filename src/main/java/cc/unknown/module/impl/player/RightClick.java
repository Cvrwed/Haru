package cc.unknown.module.impl.player;

import java.util.Random;

import org.lwjgl.input.Mouse;

import cc.unknown.event.impl.api.EventLink;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.utils.client.ClientUtil;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class RightClick extends Module {
	private DoubleSliderValue rightCPS = new DoubleSliderValue("Right CPS", 12, 16, 1, 60, 0.5);
	private BooleanValue onlyBlocks = new BooleanValue("Only blocks", false);
	private BooleanValue allowEat = new BooleanValue("Allow eat & drink", true);
	private BooleanValue allowBow = new BooleanValue("Allow bow", true);
	private ModeValue clickStyle = new ModeValue("Click Style", "Raven", "Raven", "Kuru", "Megumi");
   
	private Random rand = null;
	private long righti;
	private long rightj;
	private long rightk;
	private long rightl;
	private double rightm;
	private boolean rightn;
	private long lastClick;
	private long rightHold;
	private boolean rightDown;

	public RightClick() {
		super("RightClick", ModuleCategory.Player);
		this.registerSetting(rightCPS, onlyBlocks, allowEat, allowBow, clickStyle);
	}
	
	@Override
	public void onEnable() {
		this.rand = new Random();
	}
	
	@EventLink
	public void onRenderTick(Render2DEvent ev) {
		if(!(mc.currentScreen == null && !(mc.currentScreen instanceof GuiInventory) && !(mc.currentScreen instanceof GuiChest)))
			return;

		switch (clickStyle.getMode()) {
		case "Raven": {
			ravenClick();
		}
			break;
		case "Kuru": {
			kuruClick();
		}
			break;
		case "Megumi": {
			megumiClick();
		}
			break;
		}
	}
    
	private void kuruClick() {
		if (mc.currentScreen != null || !mc.inGameHasFocus)
			return;
		
		double speedRight = 1.0 / ThreadLocalRandom.current().nextDouble(rightCPS.getInputMin() - 0.2D, rightCPS.getInputMax());
		double rightHoldLength = speedRight / ThreadLocalRandom.current().nextDouble(rightCPS.getInputMin() - 0.02D, rightCPS.getInputMax());

		if(!Mouse.isButtonDown(1) && !rightDown) {
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
		}
		
		if (Mouse.isButtonDown(1) || rightDown) {
			if (!this.rightClickAllowed())
				return;

			if (System.currentTimeMillis() - lastClick > speedRight * 1000) {
				lastClick = System.currentTimeMillis();
				if (rightHold < lastClick){
					rightHold = lastClick;
				}
				int key = mc.gameSettings.keyBindUseItem.getKeyCode();
				KeyBinding.setKeyBindState(key, true);
				KeyBinding.onTick(key);
				rightDown = false;
			} else if (System.currentTimeMillis() - rightHold > rightHoldLength * 1000) {
				rightDown = true;
				KeyBinding.setKeyBindState(key, false);
			}
		}
	}
	
	private void megumiClick() {
		if (mc.currentScreen != null || !mc.inGameHasFocus)
			return;
		
		double speedRight = 1.0 / ThreadLocalRandom.current().nextGaussian() * rightCPS.getInputMax();
		double rightHoldLength = speedRight / ThreadLocalRandom.current().nextGaussian() * rightCPS.getInputMin();
		
		if(!Mouse.isButtonDown(1) && !rightDown) {
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
		}
		
		if (Mouse.isButtonDown(1) || rightDown) {
			if (!this.rightClickAllowed())
				return;
			
			if (System.currentTimeMillis() - lastClick > speedRight * 1000) {
				lastClick = System.currentTimeMillis();
				if (rightHold < lastClick){
					rightHold = lastClick;
				}
				int key = mc.gameSettings.keyBindUseItem.getKeyCode();
				KeyBinding.setKeyBindState(key, true);
				KeyBinding.onTick(key);
				rightDown = false;
			} else if (System.currentTimeMillis() - rightHold > rightHoldLength * 1000) {
				rightDown = true;
				KeyBinding.setKeyBindState(key, false);
			}
		}
	}

	private void ravenClick() {
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

	private boolean rightClickAllowed() {
		ItemStack item = mc.thePlayer.getHeldItem();
		if (item != null) {
			
			if (item.getItem() instanceof ItemSword) {
				return false;
			} else if(item.getItem() instanceof ItemFishingRod) {
				return false;	
			} else if (item.getItem() instanceof ItemBow) {
				return false;	
			}
			
			if (allowEat.isToggled()) {
				if ((item.getItem() instanceof ItemFood) || item.getItem() instanceof ItemPotion || item.getItem() instanceof ItemBucketMilk) {
					return false;
				}
			}

			if (onlyBlocks.isToggled()) {
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

	private void genRightTimings() {
		double clickSpeed = ClientUtil.ranModuleVal(rightCPS, this.rand) + 0.4D * this.rand.nextDouble();
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

}