package cc.unknown.module.impl.other;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

@Register(name = "Inventory", category = Category.Other)
public class Inventory extends Module {
	
	private BooleanValue sprint = new BooleanValue("Sprint", false);
    private final KeyBinding[] moveKeys = new KeyBinding[]{mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSprint};
    
	public Inventory() {
		this.registerSetting(sprint);
	}
	
    @EventLink
    public void onTick(TickEvent e) {
        if (mc.currentScreen != null && mc.currentScreen instanceof GuiChat) {
            return;
        }
    	
        for (KeyBinding bind : moveKeys) {
            bind.pressed = GameSettings.isKeyDown(bind);
            
            if (sprint.isToggled() && PlayerUtil.isMoving() && (mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChest)) {
            	mc.gameSettings.keyBindSprint.pressed = true;
            	mc.thePlayer.setSprinting(true);
            }
        }
    }
    
    @Override
    public void onDisable() {
    	if (mc.currentScreen != null) {
    		for (KeyBinding bind : moveKeys) {
    			if (bind.pressed) {
    				bind.pressed = false;
    			}
    		}
    	}
    }
}
