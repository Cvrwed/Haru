package cc.unknown.utils.client;

public class Cold {

	long lastMs;

	public Cold() {
		reset();
	}

	public long getTime() {
		return Math.max(0L, System.currentTimeMillis() - lastMs);
	}
	
	public boolean elapsed(long ms) {
		return (System.currentTimeMillis() - this.lastMs > ms);
	}

	public boolean elapsed(long owo, boolean reset) {
		if (getTime() >= owo) {
			if (reset) {
				reset();
			}
			return true;
		}
		return false;
	}

	public void reset() {
		this.lastMs = System.currentTimeMillis();
	}
}