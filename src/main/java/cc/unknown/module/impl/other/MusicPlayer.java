package cc.unknown.module.impl.other;

import java.util.HashMap;
import java.util.Map;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.event.impl.other.ShutdownEvent;
import cc.unknown.event.impl.other.StartGameEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.music.RadioPlayer;

public class MusicPlayer extends Module {
	public static RadioPlayer radioPlayer = new RadioPlayer();
	private static final Map<String, String> Urls = new HashMap<>();

	static {
		Urls.put("Casual", "https://streams.ilovemusic.de/iloveradio1.mp3");
		Urls.put("Dance", "https://streams.ilovemusic.de/iloveradio36.mp3");
		Urls.put("Chill Hop", "https://streams.ilovemusic.de/iloveradio17.mp3");
		Urls.put("Greatest Hits", "https://streams.ilovemusic.de/iloveradio16.mp3");
		Urls.put("The Sun", "https://streams.ilovemusic.de/iloveradio15.mp3");
	}

	private ModeValue mode = new ModeValue("Mode", "The Sun", "Casual", "Dance", "Chill Hop", "Greatest Hits",
			"The Sun");
	public SliderValue volume = new SliderValue("Volumen", 50, 0, 100, 1);

	public MusicPlayer() {
		super("MusicPlayer", ModuleCategory.Other);
		this.registerSetting(mode, volume);
	}

	@Override
	public void onEnable() {
		playMusic();
	}

	@Override
	public void onDisable() {
		new Thread(() -> {
			if (radioPlayer != null) {
				radioPlayer.stop();
				radioPlayer.setCurrent("");
			}
		}).start();
	}

	@EventLink
	public void guiUpdate(ClickGuiEvent e) {
		radioPlayer.setVolume();
		playMusic();
	}

	private void playMusic() {
		new Thread(() -> {
			String stationUrl = Urls.get(mode.getMode());

			if (stationUrl != null) {
				radioPlayer.stop();
				try {
					radioPlayer.start(stationUrl);
				} catch (Exception e) {
				}
			}
		}).start();
	}

	@EventLink
	public void onShutdown(ShutdownEvent e) {
		this.disable();
	}

	@EventLink
	public void onStartGame(StartGameEvent e) {
		this.disable();
	}
}