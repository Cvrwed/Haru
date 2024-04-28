package cc.unknown.mixin.mixins.gui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.achievement.GuiAchievement;

@Mixin(GuiAchievement.class)
public class MixinGuiAchievement {
	
	@Inject(method = "updateAchievementWindow", at = @At("HEAD"), cancellable = true)
	private void injectAchievements(CallbackInfo ci) {
		ci.cancel();
	}
}
