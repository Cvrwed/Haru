package cc.unknown.mixin.mixins.spamfix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.utils.Loona;
import net.minecraft.client.settings.GameSettings;

@Mixin(targets = "net.minecraft.client.gui.GuiScreenOptionsSounds$Button")
public class MixinGuiScreenOptionsSounds_PacketSpam implements Loona {

    @Redirect(method = "mouseDragged(Lnet/minecraft/client/Minecraft;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/GameSettings;saveOptions()V"))
    private void patcher$cancelSaving(GameSettings instance) { }

    @Inject(method = "mouseReleased(II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/audio/SoundHandler;playSound(Lnet/minecraft/client/audio/ISound;)V"))
    private void patcher$save(int mouseX, int mouseY, CallbackInfo ci) {
        mc.gameSettings.saveOptions();
    }
}