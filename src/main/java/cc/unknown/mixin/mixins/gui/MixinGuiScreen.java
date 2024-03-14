package cc.unknown.mixin.mixins.gui;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.Haru;
import cc.unknown.utils.interfaces.Loona;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mixin(GuiScreen.class)
@SideOnly(Side.CLIENT)
public abstract class MixinGuiScreen {

	@Shadow
	protected List<GuiButton> buttonList;

	@Shadow
	public int width;

	@Shadow
	public int height;

	@Inject(method = "sendChatMessage(Ljava/lang/String;Z)V", at = @At("HEAD"), cancellable = true)
	private void onChat(String msg, boolean addToChat, CallbackInfo ci) {
		if (msg.startsWith(Haru.instance.getCommandManager().getPrefix()) && msg.length() > 1) {
			if (Haru.instance.getCommandManager().execute(msg)) {
				Loona.mc.ingameGUI.getChatGUI().addToSentMessages(msg);
			}
			ci.cancel();
		}
	}

	@Inject(method = "actionPerformed", at = @At("RETURN"))
	protected void injectActionPerformed(GuiButton button, CallbackInfo callbackInfo) {
		this.injectedActionPerformed(button);
	}

	protected void injectedActionPerformed(GuiButton button) {

	}


}