package cc.unknown.command.commands;

import java.util.ArrayList;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.UpdateEvent;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.utils.client.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

public class TacoCommand extends Command {

	private boolean toggle = false;
	private int image;
	private int deltaTime;
	private int running;

	private ResourceLocation[] tacoTextures = new ResourceLocation[] { new ResourceLocation("haru/img/taco/1.png"),
			new ResourceLocation("haru/img/taco/2.png"), new ResourceLocation("haru/img/taco/3.png"),
			new ResourceLocation("haru/img/taco/4.png"), new ResourceLocation("haru/img/taco/5.png"),
			new ResourceLocation("haru/img/taco/6.png"), new ResourceLocation("haru/img/taco/7.png"),
			new ResourceLocation("haru/img/taco/8.png"), new ResourceLocation("haru/img/taco/9.png"),
			new ResourceLocation("haru/img/taco/10.png"), new ResourceLocation("haru/img/taco/11.png"),
			new ResourceLocation("haru/img/taco/12.png") };

	public TacoCommand() {
		super("taco");
		Haru.instance.getEventBus().register(this);
	}

	@Override
	public void execute(String alias, String[] args) {
		toggle = !toggle;
	}

	@EventLink
	public void onRender2D(Render2DEvent event) {
		if (!toggle)
			return;
		running += 0.015f * deltaTime;
		ScaledResolution scaledResolution = new ScaledResolution(mc);
		running++;
		RenderUtil.drawImage(tacoTextures[image], (int) running, scaledResolution.getScaledHeight() - 80, 64, 32);
		if (scaledResolution.getScaledWidth() <= running)
			running = -64;
	}

	@EventLink
	public void onUpdate(UpdateEvent event) {
		if (!toggle) {
			image = 0;
			return;
		}
		image++;
		if (image >= tacoTextures.length)
			image = 0;
	}

	@Override
	public ArrayList<String> autocomplete(int arg, String[] args) {
		return null;
	}

}
