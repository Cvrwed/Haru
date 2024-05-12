package cc.unknown.utils.misc;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import cc.unknown.module.impl.Module;
import cc.unknown.utils.Loona;
import net.minecraft.client.settings.KeyBinding;

public enum KeybindUtil {
	instance;

	private final Map<String, Integer> keyMap = new HashMap<>();

	public void bind(Module mod, int bind) {
		mod.setKey(bind);
	}

	public void unbind(Module mod) {
		mod.setKey(0);
	}

	public int toInt(String keyCode) {
		return keyMap.getOrDefault(keyCode.toLowerCase(), 0);
	}
	
    public boolean isPressed(final KeyBinding key) {
        return Keyboard.isKeyDown(key.getKeyCode());
    }
    
    public void resetKeybinding(final KeyBinding key) {
        if (Loona.mc.currentScreen != null) {
            key.pressed = false;
        }
        else {
            key.pressed = isPressed(key);
        }
    }
    
    public void resetKeybindings(final KeyBinding... keys) {
        for (final KeyBinding key : keys) {
            resetKeybinding(key);
        }
    }

	KeybindUtil() {
		keyMap.put("a", 30);
		keyMap.put("b", 48);
		keyMap.put("c", 46);
		keyMap.put("d", 32);
		keyMap.put("e", 18);
		keyMap.put("f", 33);
		keyMap.put("g", 34);
		keyMap.put("h", 35);
		keyMap.put("i", 23);
		keyMap.put("j", 36);
		keyMap.put("k", 37);
		keyMap.put("l", 38);
		keyMap.put("m", 50);
		keyMap.put("n", 49);
		keyMap.put("o", 24);
		keyMap.put("p", 25);
		keyMap.put("q", 16);
		keyMap.put("r", 19);
		keyMap.put("s", 31);
		keyMap.put("t", 20);
		keyMap.put("u", 22);
		keyMap.put("v", 47);
		keyMap.put("w", 17);
		keyMap.put("x", 45);
		keyMap.put("y", 21);
		keyMap.put("z", 44);
		keyMap.put("0", 11);
		keyMap.put("1", 2);
		keyMap.put("2", 3);
		keyMap.put("3", 4);
		keyMap.put("4", 5);
		keyMap.put("5", 6);
		keyMap.put("6", 7);
		keyMap.put("7", 8);
		keyMap.put("8", 9);
		keyMap.put("9", 10);
		keyMap.put("numpad0", 82);
		keyMap.put("numpad1", 79);
		keyMap.put("numpad2", 80);
		keyMap.put("numpad3", 81);
		keyMap.put("numpad4", 75);
		keyMap.put("numpad5", 76);
		keyMap.put("numpad6", 77);
		keyMap.put("numpad7", 71);
		keyMap.put("numpad8", 72);
		keyMap.put("numpad9", 73);
		keyMap.put("rshift", 54);
		keyMap.put("lshift", 42);
		keyMap.put("lcontrol", 29);
		keyMap.put("tab", 15);
		keyMap.put("strg", 29);
		keyMap.put("alt", 56);
	}
}