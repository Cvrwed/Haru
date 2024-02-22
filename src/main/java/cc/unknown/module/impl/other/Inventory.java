package cc.unknown.module.impl.other;

import cc.unknown.event.impl.api.EventLink;
import cc.unknown.event.impl.move.PostUpdateEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class InvMove extends Module {
	
	private BooleanValue sprint = new BooleanValue("Sprint", true);
    private final KeyBinding[] moveKeys = new KeyBinding[]{mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSprint};
    
	public InvMove() {
		super("Inventory", ModuleCategory.Other);
		this.registerSetting(sprint);
	}
	
	@EventLink
	public void onPost(PostUpdateEvent e) {
		if (mc.currentScreen != null) {
			if (mc.currentScreen instanceof GuiChat) {
				return;
			}
		}
	}
	
    @EventLink
    public void onTick(TickEvent e) {
        for (KeyBinding bind : moveKeys) {
            bind.pressed = GameSettings.isKeyDown(bind);
        }
        if (sprint.isToggled() && PlayerUtil.isMoving()) {
        	mc.gameSettings.keyBindSprint.pressed = true;
        	mc.thePlayer.setSprinting(true);
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
