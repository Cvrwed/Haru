package cc.unknown.mixin.mixins.network;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import cc.unknown.Haru;
import cc.unknown.module.impl.settings.Tweaks;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;

@Mixin(value = FMLCommonHandler.class, remap = false)
public class MixinFMLCommonHandler {
	
	/**
	 * Replaces all occurrences of "fml,forge" with "vanilla" in the input string.
	 * 
	 * @param input The input string to perform the replacement on.
	 * @return The input string with the client brand replaced from "fml,forge" to vanilla.
	 * @reason Spoofs the client brand to appear as vanilla, preventing client detection.
	 * @author Cvrwed
	 */

	@Overwrite
	public String getModName() {
        Tweaks cns = (Tweaks) Haru.instance.getModuleManager().getModule(Tweaks.class);
        if (cns != null && cns.isEnabled()) {
            return "vanilla";
        }
        List<String> modNames = Lists.newArrayListWithExpectedSize(3);
        modNames.add("fml");
        modNames.add("forge");

        if (Loader.instance().getFMLBrandingProperties().containsKey("snooperbranding")) {
            modNames.add(Loader.instance().getFMLBrandingProperties().get("snooperbranding"));
        }
        return Joiner.on(',').join(modNames);
	}

}
