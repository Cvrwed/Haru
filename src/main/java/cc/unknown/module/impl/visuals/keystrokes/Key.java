package cc.unknown.module.impl.visuals.keystrokes;

import net.minecraft.client.settings.KeyBinding;

public class Key {
	private final String name;
	private final KeyBinding key;
	private final int x;
	private final int y;
	private final int width;
	private final int height;

	public Key(final String name, final KeyBinding key, final int x, final int y, final int width, final int height) {
		this.name = name;
		this.key = key;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public String getName() {
		return this.name;
	}

	public boolean isDown() {
		return this.key.isKeyDown();
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}
}
