package cc.unknown.mixin.mixins.music;

import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import cc.unknown.mixin.interfaces.music.IJavaSoundAudioDevice;
import javazoom.jl.player.JavaSoundAudioDevice;

@Mixin(JavaSoundAudioDevice.class)
public class MixinJavaSoundAudioDevice implements IJavaSoundAudioDevice {
	
	@Shadow
	private SourceDataLine source = null;

	@Override
	public boolean setNewLine(float gain) {
		if (this.source != null) {
			FloatControl volcontrol = (FloatControl)this.source.getControl(FloatControl.Type.MASTER_GAIN);
			float newgain = Math.min(Math.max(gain, volcontrol.getMinimum()), volcontrol.getMaximum());
			volcontrol.setValue(newgain);
			return true;
		} 
		return false;
	}
}
