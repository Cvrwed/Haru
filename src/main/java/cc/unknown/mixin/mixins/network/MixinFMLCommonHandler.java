package cc.unknown.mixin.mixins.network;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.google.common.collect.Lists;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;

@Mixin(value = FMLCommonHandler.class, remap = false)
public class MixinFMLCommonHandler {

	@Overwrite
	public String getModName() {
	    List<String> modNames = Lists.newArrayListWithExpectedSize(1);
	    modNames.add("vanilla");

	    if (Loader.instance().getFMLBrandingProperties().containsKey("snooperbranding")) {
	        modNames.add(Loader.instance().getFMLBrandingProperties().get("snooperbranding"));
	    }

	    return modNames.get(0);
	}

}