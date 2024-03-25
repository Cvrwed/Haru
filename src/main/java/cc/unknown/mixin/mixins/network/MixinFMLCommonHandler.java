package cc.unknown.mixin.mixins.network;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraftforge.fml.common.FMLCommonHandler;

@Mixin(value = FMLCommonHandler.class, remap = false)
public class MixinFMLCommonHandler {

	@Inject(method = "getModName", at = @At("HEAD"), cancellable = true)
	public String getModName(final CallbackInfo ci) {
	    ci.cancel();
	    return "vanilla";
	}

}
