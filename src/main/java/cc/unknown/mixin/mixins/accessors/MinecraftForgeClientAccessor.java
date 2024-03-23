package cc.unknown.mixin.mixins.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.google.common.cache.LoadingCache;

import akka.japi.Pair;
import net.minecraft.client.renderer.RegionRenderCache;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

@Mixin(MinecraftForgeClient.class)
public interface MinecraftForgeClientAccessor {
    @Accessor(remap = false)
    static LoadingCache<Pair<World, BlockPos>, RegionRenderCache> getRegionCache() {
        throw new AssertionError();
    }
}