package cc.unknown.mixin.mixins.music;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import cc.unknown.mixin.interfaces.music.IJavaSoundAudioDevice;
import cc.unknown.mixin.interfaces.music.IPlayer;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.JavaSoundAudioDevice;
import javazoom.jl.player.Player;

@Mixin(Player.class)
public class MixinPlayer implements IPlayer {
	
	@Shadow
	private AudioDevice audio;

	@Override
	public boolean setGain(float newgain) {
		if (this.audio instanceof JavaSoundAudioDevice) {
			JavaSoundAudioDevice jsaudio = (JavaSoundAudioDevice)this.audio;
			try {
				jsaudio.write(null, 0, 0);
			} catch (JavaLayerException ex) {
				ex.printStackTrace();
			}
			((IJavaSoundAudioDevice)jsaudio).setLineGain(newgain);
		} 
		return false;
	}

}
