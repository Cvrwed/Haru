package cc.unknown.event.impl.move;

import cc.unknown.event.Event;

public class HitSlowDownEvent extends Event {
    private double slowDown;
    private boolean sprint;
    
    /**
     * Constructs a HitSlowDownEvent with the specified slow down value and sprint status.
     *
     * @param slowDown The slow down value associated with the event.
     * @param sprint   The sprint status associated with the event.
     */
    public HitSlowDownEvent(double slowDown, boolean sprint) {
        this.slowDown = slowDown;
        this.sprint = sprint;
    }

    /**
     * Gets the slow down value associated with the event.
     *
     * @return The slow down value associated with the event.
     */
    public double getSlowDown() {
        return slowDown;
    }

    /**
     * Sets the slow down value associated with the event.
     *
     * @param slowDown The slow down value to set.
     */
    public void setSlowDown(double slowDown) {
        this.slowDown = slowDown;
    }

    /**
     * Checks if the sprint status of the event is true.
     *
     * @return true if the sprint status is true, false otherwise.
     */
    public boolean isSprint() {
        return sprint;
    }

    /**
     * Sets the sprint status associated with the event.
     *
     * @param sprint The sprint status to set.
     */
    public void setSprint(boolean sprint) {
        this.sprint = sprint;
    }
}
