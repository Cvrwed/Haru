package cc.unknown.module.impl.combat;

import cc.unknown.event.impl.api.EventLink;
import cc.unknown.event.impl.move.UpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.AdvancedTimer;
import cc.unknown.utils.player.CombatUtil;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class AutoRod extends Module {

    private AdvancedTimer pushTimer = new AdvancedTimer(0);
    private AdvancedTimer rodPullTimer = new AdvancedTimer(0);
    private boolean rodInUse = false;
    private int switchBack = -1;
    
    private BooleanValue facingEnemy = new BooleanValue("Check enemy", true);
    private final SliderValue enemyDistance = new SliderValue("Distance enemy", 8, 1, 10, 1);
    private final SliderValue pushDelay = new SliderValue("Push delay", 100, 50, 1000, 50);
    private final SliderValue pullbackDelay = new SliderValue("Pullback delay", 500, 50, 1000, 50);

	public AutoRod() {
		super("AutoRod", ModuleCategory.Combat);
		this.registerSetting(facingEnemy, enemyDistance, pushDelay, pullbackDelay);
	}
	
    @EventLink
    public void onUpdate(UpdateEvent event) {
        boolean usingRod = (mc.thePlayer.isUsingItem() && mc.thePlayer.getHeldItem().getItem() == Items.fishing_rod) || rodInUse;
        if (usingRod) {
            if (rodPullTimer.hasTimeElapsed(pullbackDelay.getInputToLong(), true)) {
                if (switchBack != -1 && mc.thePlayer.inventory.currentItem != switchBack) {
                    mc.thePlayer.inventory.currentItem = switchBack;
                    mc.playerController.updateController();
                } else {
                    mc.thePlayer.stopUsingItem();
                }
                switchBack = -1;
                rodInUse = false;
            }
        } else {
            boolean rod = false;
            if (facingEnemy.isToggled()) {
	            Entity facingEntity = mc.objectMouseOver != null ? mc.objectMouseOver.entityHit : null;
	            if (facingEntity == null) {
	            	facingEntity = CombatUtil.raycastEntity(enemyDistance.getInput(), entity -> CombatUtil.canTarget(entity));
	            }
	            
	            if (CombatUtil.canTarget(facingEntity)) {
	            	rod = true;
	            }
            } else {
                rod = true;
            }
	            
            if (rod && pushTimer.hasTimeElapsed(pushDelay.getInputToLong(), true)) {
            	if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() != Items.fishing_rod) {
            		int rodSlot = findRod();
            		switchBack = mc.thePlayer.inventory.currentItem;
            		mc.thePlayer.inventory.currentItem = rodSlot;
            		mc.playerController.updateController();
            	}
            	rod();
            }
        }
            
    }
	
	private void rod() {
	    int rod = this.findRod();
	    int currentSlot = mc.thePlayer.inventory.currentItem;
	    if (mc.thePlayer.inventory.getStackInSlot(currentSlot) == null) {
	    	return;
	    }
	    mc.thePlayer.inventory.currentItem = rod;
	    mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventoryContainer.getSlot(rod).getStack());
	    rodInUse = true;
	}
	
	private int findRod() {
		for(int i = 36; i < 45; i++) {
	        ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
	        if (stack != null && stack.getItem() == Items.fishing_rod) {
	            return i;
	        }
	    }
	    return -1;
	}
}
