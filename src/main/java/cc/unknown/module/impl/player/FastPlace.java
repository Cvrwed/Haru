package cc.unknown.module.impl.player;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemStack;

@Register(name = "FastPlace", category = Category.Player)
public class FastPlace extends Module {
	private SliderValue delaySlider = new SliderValue("Delay", 1, 0, 4, 0.5);
	private BooleanValue blockOnly = new BooleanValue("Blocks only", true);
	private BooleanValue projSeparate = new BooleanValue("Separate Projectile Delay", true);
	private BooleanValue pitchCheck = new BooleanValue("Pitch Check", false);
	private SliderValue projSlider = new SliderValue("Projectile Delay", 2, 0, 4, 0.5);

	public FastPlace() {
		this.registerSetting(delaySlider, blockOnly, projSeparate, pitchCheck, projSlider);
	}
	
	@EventLink
	public void onGui(ClickGuiEvent e) {
	    this.setSuffix("- [" + delaySlider.getInput() + " ticks]");
	}

	@Override
	public boolean canBeEnabled() {
		return mc.rightClickDelayTimer != 4;
	}

	@EventLink
	public void onTick(TickEvent e) {
		try {
			if (PlayerUtil.inGame() && mc.inGameHasFocus) {
				ItemStack item = mc.thePlayer.getHeldItem();
				if (item.getItem() instanceof ItemFishingRod && item != null) {
					return;
				}

				if (!pitchCheck.isToggled() || !(mc.thePlayer.rotationPitch < 70.0F)) {
					if (blockOnly.isToggled() && item != null) {
						if (item.getItem() instanceof ItemBlock) {
							rightDelay(delaySlider.getInputToInt());
						} else if ((item.getItem() instanceof ItemSnowball || item.getItem() instanceof ItemEgg)
								&& projSeparate.isToggled()) {
							rightDelay(projSlider.getInputToInt());
						}
					} else {
						rightDelay(delaySlider.getInputToInt());
					}
				}
			}
		} catch (NullPointerException ignore) {
		}
	}

	private void rightDelay(int x) {
		if (x == 0) {
			mc.rightClickDelayTimer = 0;
		} else if (x != 4 && mc.rightClickDelayTimer == 4) {
			mc.rightClickDelayTimer = x;
		}
	}
}
