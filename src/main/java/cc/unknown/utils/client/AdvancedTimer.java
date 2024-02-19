package cc.unknown.utils.client;

import java.util.function.LongSupplier;

public class AdvancedTimer {

    private long lastMs;
    private long time;
    private boolean checkedFinish;

    public AdvancedTimer(long lasts) {
        this.lastMs = lasts;
    }

    public void start() {
        reset();
        checkedFinish = false;
    }
    
    public boolean firstFinish() {
        if ((System.currentTimeMillis() >= (time + lastMs)) && !checkedFinish) {
            checkedFinish = true;
            return true;
        }
        return false;
    }

    public void setCooldown(long time) {
        this.lastMs = time;
    }
    
    public boolean hasFinished() {
        return isElapsed(time + lastMs, () -> System.currentTimeMillis());
    }
    
    public boolean finished(long delay) {
        return isElapsed(time, () -> System.currentTimeMillis() - delay);
    }

    public boolean isDelayComplete(long l) {
        return isElapsed(lastMs, () -> System.currentTimeMillis() - l);
    }

    public boolean reached(long currentTime) {
        return isElapsed(time, () -> Math.max(0L, System.currentTimeMillis() - currentTime));
    }

    public void reset() {
        this.time = System.currentTimeMillis();
    }

    public long getTime() {
        return Math.max(0L, System.currentTimeMillis() - this.time);
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

    public boolean hasTimeElapsed(long time) {
        return isElapsed(lastMs, () -> System.currentTimeMillis() - time);
    }

    private boolean isElapsed(long targetTime, LongSupplier currentTimeSupplier) {
        return currentTimeSupplier.getAsLong() >= targetTime;
    }
}
