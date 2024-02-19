package cc.unknown.mixin.mixins.network;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import cc.unknown.Haru;
import cc.unknown.module.impl.settings.Fixes;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;

@Mixin(value = FMLCommonHandler.class, remap = false)
public class MixinFMLCommonHandler {

	@Overwrite
	public String getModName() {
        Fixes cns = (Fixes) Haru.instance.getModuleManager().getModule(Fixes.class);
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
