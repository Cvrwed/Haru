package cc.unknown.utils.helpers;

public class CPSHelper {

    private static final int MAX_CPS = 500;
    private static final BufferHelper[] TIMESTAMP_BUFFERS = new BufferHelper[MouseButton.values().length];

    static {
        for (int i = 0; i < TIMESTAMP_BUFFERS.length; i++) {
            TIMESTAMP_BUFFERS[i] = new BufferHelper(MAX_CPS);
        }
    }

    public static void registerClick(MouseButton button) {
        TIMESTAMP_BUFFERS[button.getIndex()].add(System.currentTimeMillis());
    }

    public static int getCPS(MouseButton button) {
        return TIMESTAMP_BUFFERS[button.getIndex()].getTimestampsSince(System.currentTimeMillis() - 1000L);
    }

    public enum MouseButton {
        LEFT(0), MIDDLE(1), RIGHT(2);

        private int index;

        MouseButton(int index) {
            this.index = index;
        }

        private int getIndex() {
            return index;
        }
    }
}
