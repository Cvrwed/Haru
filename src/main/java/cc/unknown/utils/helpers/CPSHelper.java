package cc.unknown.utils.helpers;

import java.util.concurrent.atomic.AtomicLongArray;

public class CPSHelper {

    private static final int MAX_CPS = 500;
    private static final AtomicLongArray[] TIMESTAMP_BUFFERS = new AtomicLongArray[MouseButton.values().length];

    static {
        for (int i = 0; i < TIMESTAMP_BUFFERS.length; i++) {
            TIMESTAMP_BUFFERS[i] = new AtomicLongArray(MAX_CPS);
        }
    }

    public static void registerClick(MouseButton button) {
        int index = button.getIndex();
        int slot = (int) (System.currentTimeMillis() % MAX_CPS);
        TIMESTAMP_BUFFERS[index].set(slot, System.currentTimeMillis());
    }

    public static int getCPS(MouseButton button) {
        int index = button.getIndex();
        long currentTime = System.currentTimeMillis();
        int count = 0;

        for (int i = 0; i < MAX_CPS; i++) {
            long timestamp = TIMESTAMP_BUFFERS[index].get(i);
            if (timestamp > currentTime - 1000L) {
                count++;
            }
        }

        return count;
    }

    public enum MouseButton {
        LEFT(0), MIDDLE(1), RIGHT(2);

        private int index;

        MouseButton(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }
}
