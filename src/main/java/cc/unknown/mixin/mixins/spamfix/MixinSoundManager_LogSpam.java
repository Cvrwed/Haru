package cc.unknown.mixin.mixins.spamfix;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import net.minecraft.client.audio.SoundManager;

@Mixin(SoundManager.class)
public class MixinSoundManager_LogSpam {
    @Redirect(method = "playSound", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=Unable to play unknown soundEvent: {}", ordinal = 0)), at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;warn(Lorg/apache/logging/log4j/Marker;Ljava/lang/String;[Ljava/lang/Object;)V", ordinal = 0, remap = false))
    private void patcher$silenceWarning(Logger instance, Marker marker, String s, Object[] objects) { }
}