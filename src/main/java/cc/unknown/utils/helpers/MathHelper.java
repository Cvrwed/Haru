package cc.unknown.utils.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import cc.unknown.utils.interfaces.Loona;

public class MathHelper implements Loona {

	private static final Random rand = new Random();
	private static final float[] SIN_TABLE = new float[65536];
	private static final double field_181163_d;
	private static final double[] field_181165_f;
	private static final double[] field_181164_e;

	public static int randomInt(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}

	public static int floor_double(double value) {
		int i = (int) value;
		return value < (double) i ? i - 1 : i;
	}

	public static double clamp_double(double num, double min, double max) {
		return num < min ? min : (num > max ? max : num);
	}

	public static int clamp_int(int num, int min, int max) {
		return num < min ? min : (num > max ? max : num);
	}
	
	public static float sqrt_double(double value) {
		return (float) Math.sqrt(value);
	}

	public static float sqrt_float(float value) {
		return (float) Math.sqrt((double) value);
	}

	public static float clamp_float(float num, float min, float max) {
		return num < min ? min : (num > max ? max : num);
	}
	
	public static double wrapAngleTo180_double(double value) {
		value = value % 360.0D;

		if (value >= 180.0D) {
			value -= 360.0D;
		}

		if (value < -180.0D) {
			value += 360.0D;
		}

		return value;
	}

	public static float wrapAngleTo180_float(float value) {
		value = value % 360.0F;

		if (value >= 180.0F) {
			value -= 360.0F;
		}

		if (value < -180.0F) {
			value += 360.0F;
		}

		return value;
	}

	public static float wrapAngleTo90_float(float var) {
		var = var % 360.0F;

		if (var >= 90.0F) {
			var -= 360.0F;
		}

		if (var < -90.0F) {
			var += 360.0F;
		}

		return var;
	}

	public static double simpleRandom(final int min, final int max) {
		int x = min;
		int y = max;

		if (min == max) {
			return min;
		} else if (min > max) {
			x = max;
			y = min;
		}

		return ThreadLocalRandom.current().nextDouble(x, y);
	}

	public static double simpleRandom(double min, double max) {
		if (min == max) {
			return min;
		} else if (min > max) {
			final double d = min;
			min = max;
			max = d;
		}
		return ThreadLocalRandom.current().nextDouble(min, max);
	}

	public static float sin(float p_76126_0_) {
		return SIN_TABLE[(int) (p_76126_0_ * 10430.378F) & 65535];
	}

	public static float cos(float value) {
		return SIN_TABLE[(int) (value * 10430.378F + 16384.0F) & 65535];
	}

	public static long nextLong(long origin, long bound) {
		return origin == bound ? origin : ThreadLocalRandom.current().nextLong(origin, bound);
	}

	public static Random rand() {
		return rand;
	}

	public static ArrayList<String> toArrayList(String[] x) {
		return new ArrayList<>(Arrays.asList(x));
	}

	public static List<String> StringListToList(String[] whytho) {
		List<String> f = new ArrayList<>();
		Collections.addAll(f, whytho);
		return f;
	}

	public static double round(double n, int d) {
		if (d == 0) {
			return (double) Math.round(n);
		} else {
			double p = Math.pow(10.0D, d);
			return (double) Math.round(n * p) / p;
		}
	}

	public static int randomInt(double x, double v) {
		return (int) (Math.random() * (v - x) + x);
	}

	public static double randomDouble(double x, double v) {
		return Math.random() * (x - v) + v;
	}

	public static double func_181159_b(double p_181159_0_, double p_181159_2_) {
		double d0 = p_181159_2_ * p_181159_2_ + p_181159_0_ * p_181159_0_;

		if (Double.isNaN(d0)) {
			return Double.NaN;
		} else {
			boolean flag = p_181159_0_ < 0.0D;

			if (flag) {
				p_181159_0_ = -p_181159_0_;
			}

			boolean flag1 = p_181159_2_ < 0.0D;

			if (flag1) {
				p_181159_2_ = -p_181159_2_;
			}

			boolean flag2 = p_181159_0_ > p_181159_2_;

			if (flag2) {
				double d1 = p_181159_2_;
				p_181159_2_ = p_181159_0_;
				p_181159_0_ = d1;
			}

			double d9 = func_181161_i(d0);
			p_181159_2_ = p_181159_2_ * d9;
			p_181159_0_ = p_181159_0_ * d9;
			double d2 = field_181163_d + p_181159_0_;
			int i = (int) Double.doubleToRawLongBits(d2);
			double d3 = field_181164_e[i];
			double d4 = field_181165_f[i];
			double d5 = d2 - field_181163_d;
			double d6 = p_181159_0_ * d4 - p_181159_2_ * d5;
			double d7 = (6.0D + d6 * d6) * d6 * 0.16666666666666666D;
			double d8 = d3 + d7;

			if (flag2) {
				d8 = (Math.PI / 2D) - d8;
			}

			if (flag1) {
				d8 = Math.PI - d8;
			}

			if (flag) {
				d8 = -d8;
			}

			return d8;
		}
	}

	public static double func_181161_i(double p_181161_0_) {
		double d0 = 0.5D * p_181161_0_;
		long i = Double.doubleToRawLongBits(p_181161_0_);
		i = 6910469410427058090L - (i >> 1);
		p_181161_0_ = Double.longBitsToDouble(i);
		p_181161_0_ = p_181161_0_ * (1.5D - d0 * p_181161_0_ * p_181161_0_);
		return p_181161_0_;
	}
	
    public static long randomClickDelay(int minCPS, int maxCPS) {
        return (long) (Math.random() * (1000.0 / minCPS - 1000.0 / maxCPS + 1) + 1000.0 / maxCPS);
    }

	static {
		for (int i = 0; i < 65536; ++i) {
			SIN_TABLE[i] = (float) Math.sin((double) i * Math.PI * 2.0D / 65536.0D);
		}

		field_181163_d = Double.longBitsToDouble(4805340802404319232L);
		field_181164_e = new double[257];
		field_181165_f = new double[257];

		for (int k = 0; k < 257; ++k) {
			double d1 = (double) k / 256.0D;
			double d0 = Math.asin(d1);
			field_181165_f[k] = Math.cos(d0);
			field_181164_e[k] = d0;
		}
	}
}