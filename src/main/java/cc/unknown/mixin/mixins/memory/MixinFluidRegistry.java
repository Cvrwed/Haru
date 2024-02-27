package cc.unknown.mixin.mixins.memory;

import java.util.Collections;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

@Mixin(FluidRegistry.class)
public class MixinFluidRegistry {
    @Shadow(remap = false) static Set<Fluid> bucketFluids;

    @Overwrite(remap = false)
    public static Set<Fluid> getBucketFluids() {
        return Collections.unmodifiableSet(bucketFluids);
    }
}