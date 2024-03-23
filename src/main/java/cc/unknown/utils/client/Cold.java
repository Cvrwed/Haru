package cc.unknown.utils.client;

import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;

public class Cold {

    private long lastMs;
    private boolean checkedFinish;
    private long time;

    /**
     * Constructs a new Cold instance.
     */
    public Cold() {
        reset();
    }

    /**
     * Gets the elapsed time since the last update.
     * @return The elapsed time in milliseconds.
     */
    public long getTime() {
        return Math.max(0L, System.currentTimeMillis() - lastMs);
    }
    
    /**
     * Checks if the time has elapsed.
     * @return true if the time has elapsed, false otherwise.
     */
    public boolean hasFinished() {
        return isElapsed(time + lastMs, System::currentTimeMillis);
    }

    /**
     * Checks if the elapsed time has exceeded the specified limit.
     * @param ms The time limit in milliseconds.
     * @return true if the elapsed time has exceeded the limit, false otherwise.
     */
    public boolean elapsed(long ms) {
        return isElapsed(ms, () -> System.currentTimeMillis() - lastMs);
    }

    /**
     * Checks if the first finish event has occurred.
     * @return true if the first finish event has occurred, false otherwise.
     */
    public boolean firstFinish() {
        return checkAndSetFinish(() -> System.currentTimeMillis() >= (time + lastMs));
    }
    
    /**
     * Sets the cooldown time.
     * @param time The cooldown time in milliseconds.
     */
    public void setCooldown(long time) {
        this.lastMs = time;
    }

    /**
     * Starts the time counter.
     */
    public void start() {
        reset();
        checkedFinish = false;
    }
    
    private boolean checkAndSetFinish(BooleanSupplier condition) {
        if (condition.getAsBoolean() && !checkedFinish) {
            checkedFinish = true;
            return true;
        }
        return false;
    }

    /**
     * Checks if the specified time has elapsed and resets if necessary.
     * @param ms The time limit in milliseconds.
     * @param reset Indicates whether to reset the counter after the time has elapsed.
     * @return true if the time has elapsed, false otherwise.
     */
    public boolean elapsed(long ms, boolean reset) {
        if (elapsed(ms)) {
            if (reset) {
                reset();
            }
            return true;
        }
        return false;
    }
    
    private boolean isElapsed(long targetTime, LongSupplier currentTimeSupplier) {
        return currentTimeSupplier.getAsLong() >= targetTime;
    }

    /**
     * Resets the time counter.
     */
    public void reset() {
        lastMs = System.currentTimeMillis();
        checkedFinish = false;
    }
}