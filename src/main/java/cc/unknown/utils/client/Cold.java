package cc.unknown.utils.client;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cold {

    private long ms;

    public Cold(long ms) {
        this.ms = ms;
    }
    
    public Cold() {
    	ms = System.currentTimeMillis();
    }

    public void start() {
        reset();
    }

    public boolean hasFinished() {
        return System.currentTimeMillis() >= ms;
    }
    
    public boolean finished(long delay) {
        return System.currentTimeMillis() - delay >= ms;
    }

    public boolean reached(final long currentTime) {
        return Math.max(0L, System.currentTimeMillis() - ms) >= currentTime;
    }
    
    public boolean reached(final long lastTime, final long currentTime) {
        return Math.max(0L, System.currentTimeMillis() - ms + lastTime) >= currentTime;
    }
    public void reset() {
        this.ms = System.currentTimeMillis();
    }

    public long getTime() {
        return Math.max(0L, System.currentTimeMillis() - ms);
    }
    
    public boolean getCum(long hentai) {
    	return getTime() - ms >= hentai;
    }

    public boolean hasTimeElapsed(long owo, boolean reset) {
        if (getTime() >= owo) {
            if (reset) {
                reset();
            }
            return true;
        }
        return false;
    }

}