package cc.unknown.mixin.mixins.render;

import java.util.concurrent.Semaphore;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraftforge.fml.client.FMLClientHandler;

@Mixin(value = FMLClientHandler.class, remap = false)
public class MixinFMLClientHandler {

    @Redirect(method = "beginMinecraftLoading", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/client/SplashProgress;start()V"))
    private void beginMinecraftLoading() { }

    @Redirect(method = "haltGame", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/client/SplashProgress;finish()V"))
    private void haltGame() { }

    @Redirect(method = "finishMinecraftLoading", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/client/SplashProgress;finish()V"))
    private void finishMinecraftLoading() { }

    @Redirect(method = "onInitializationComplete", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/client/SplashProgress;finish()V"))
    private void onInitializationComplete() { }

    @Redirect(method = "processWindowMessages", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/Semaphore;tryAcquire()Z"))
    private boolean processWindowMessages(Semaphore i) { return false; }

    @Redirect(method = "processWindowMessages", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/Semaphore;release()V"))
    private void processWindowMessages2(Semaphore i) { }

}
