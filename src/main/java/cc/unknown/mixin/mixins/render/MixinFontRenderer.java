package cc.unknown.mixin.mixins.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import cc.unknown.Haru;
import cc.unknown.command.commands.NickCommand;
import net.minecraft.client.gui.FontRenderer;

@Mixin(FontRenderer.class)
public class MixinFontRenderer {

	@ModifyVariable(method = "renderString", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private String renderString(String string) {
		if (string == null)
			return null;

		NickCommand nick = (NickCommand) Haru.instance.getCommandManager().getCommand(NickCommand.class);

		if (nick != null && nick.toggle.get()) {
			string = nick.getFakeName(string);
		}

		return string;
	}

	@ModifyVariable(method = "getStringWidth", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private String getStringWidth(String string) {
		if (string == null)
			return null;

		NickCommand nick = (NickCommand) Haru.instance.getCommandManager().getCommand(NickCommand.class);

		if (nick != null && nick.toggle.get()) {
			string = nick.getFakeName(string);
		}

		return string;
	}
}