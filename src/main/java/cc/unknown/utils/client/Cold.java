package cc.unknown.utils.client;

import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;

public class Cold {

    // Last time in milliseconds
    private long lastMs;
    
    // Current time in milliseconds
    private long time;
    
    // Flag to track if the timer has been checked for completion
    private boolean checkedFinish;

    /**
     * Constructs an AdvancedTimer object with a specified cooldown duration.
     *
     * @param lasts The duration of the cooldown in milliseconds
     */
    public Cold(long lasts) {
        this.lastMs = lasts;
    }
    
    public Cold() {
    	lastMs = System.currentTimeMillis();
    }

    // Resets the timer to the current time and clears the checkedFinish flag
    public void start() {
        reset();
        checkedFinish = false;
    }

    /**
     * Checks if the timer has finished for the first time.
     *
     * @return true if the timer has finished for the first time, false otherwise
     */
    public boolean firstFinish() {
        return checkAndSetFinish(() -> System.currentTimeMillis() >= (time + lastMs));
    }

    /**
     * Sets the cooldown time to a specified duration.
     *
     * @param time The duration of the cooldown in milliseconds
     */
    public void setCooldown(long time) {
        this.lastMs = time;
    }

    /**
     * Checks if the timer has finished.
     *
     * @return true if the timer has finished, false otherwise
     */
    public boolean hasFinished() {
        return isElapsed(time + lastMs, System::currentTimeMillis);
    }

    /**
     * Checks if the timer has finished with an additional delay.
     *
     * @param delay The additional delay in milliseconds
     * @return true if the timer has finished with the delay, false otherwise
     */
    public boolean finished(long delay) {
        return isElapsed(time, () -> System.currentTimeMillis() - delay);
    }

    /**
     * Checks if a delay has been completed.
     *
     * @param l The delay duration in milliseconds
     * @return true if the delay has been completed, false otherwise
     */
    public boolean isDelayComplete(long l) {
        return isElapsed(lastMs, () -> System.currentTimeMillis() - l);
    }

    /**
     * Checks if a specified time has been reached.
     *
     * @param currentTime The specified time in milliseconds
     * @return true if the specified time has been reached, false otherwise
     */
    public boolean reached(long currentTime) {
        return isElapsed(time, () -> Math.max(0L, System.currentTimeMillis() - currentTime));
    }

    // Resets the timer to the current time
    public void reset() {
        this.time = System.currentTimeMillis();
    }

    // Gets the elapsed time since the timer was started or reset
    public long getTime() {
        return Math.max(0L, System.currentTimeMillis() - time);
    }
    
    public boolean getCum(long hentai) {
    	return System.currentTimeMillis() - lastMs >= hentai;
    }

    /**
     * Checks if a specified time has elapsed.
     *
     * @param owo   The specified time in milliseconds
     * @param reset Indicates whether to reset the timer after the time has elapsed
     * @return true if the specified time has elapsed, false otherwise
     */
    public boolean hasTimeElapsed(long owo, boolean reset) {
        if (getTime() >= owo) {
            if (reset) {
                reset();
            }
            return true;
        }
        return false;
    }

    // Private method to check if a specified time has elapsed
    private boolean checkAndSetFinish(BooleanSupplier condition) {
        if (condition.getAsBoolean() && !checkedFinish) {
            checkedFinish = true;
            return true;
        }
        return false;
    }

    // Private method to check if a specified time has elapsed
    private boolean isElapsed(long targetTime, LongSupplier currentTimeSupplier) {
        return currentTimeSupplier.getAsLong() >= targetTime;
    }

}