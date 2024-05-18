package cc.unknown.module.impl.visuals;

import org.lwjgl.input.Keyboard;

import cc.unknown.Haru;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.DescValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.ui.clickgui.raven.HaruGui;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

@Register(name = "ClickGui", category = Category.Visuals, key = Keyboard.KEY_RSHIFT)
public class ClickGuiModule extends Module {
	
    private DescValue a = new DescValue("Color Modes");
    public ModeValue clientTheme = new ModeValue("Color", "Static", "Rainbow", "Pastel", "Memories", "Lilith", "Static", "Cantina");
    private DescValue b = new DescValue("Choose ur perfect waifu");
    public ModeValue waifuMode = new ModeValue("Waifu", "Uzaki", "Uzaki", "Megumin", "Ai", "Mai", "Kiwi", "Astolfo", "Ryo", "Hitori", "None");
    private DescValue c = new DescValue("BackGround Modes");
    public ModeValue backGroundMode = new ModeValue("BackGround", "None", "Gradient", "Normal", "None");
    private DescValue d = new DescValue("ClickGui Custom Colors");
	public SliderValue clickGuiColor = new SliderValue("ClickGui Color [H/S/B]", 0, 0, 350, 10);
	public SliderValue saturation = new SliderValue("Saturation [H/S/B]", 1.0, 0.0, 1.0, 0.1);
	public SliderValue brightness = new SliderValue("Brightness [H/S/B]", 1.0, 0.0, 1.0, 0.1);
    private final KeyBinding[] moveKeys = new KeyBinding[]{mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSprint, mc.gameSettings.keyBindSneak};

    public ClickGuiModule() {
        this.registerSetting(a, clientTheme, b, waifuMode, c, backGroundMode, d, clickGuiColor, saturation, brightness);
    }
    
    @Override
    public void onEnable() {
    	if (PlayerUtil.inGame() && mc.currentScreen != Haru.instance.getHaruGui()) {
    		mc.displayGuiScreen(Haru.instance.getHaruGui());
    	}
    }
    
    @Override
    public void onDisable() {
    	if (PlayerUtil.inGame() && mc.currentScreen instanceof HaruGui) {
    		mc.displayGuiScreen(null);
    	}
    }

    @EventLink
    public void onTick(TickEvent e) {
    	for (KeyBinding bind : moveKeys) {
            bind.pressed = GameSettings.isKeyDown(bind);
        }
    }
}