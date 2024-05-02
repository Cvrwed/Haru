package cc.unknown.utils.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import cc.unknown.utils.Loona;
import net.minecraft.util.StringUtils;

public class MathHelper implements Loona {

	private static final Random rand = new Random();
	
	public static float wrapAngleTo90_float(float var) {
		var = var % 90.0F;

		if (var >= 90.0F) {
			var -= 90.0F;
		}

		if (var < -90.0F) {
			var += 90.0F;
		}

		return var;
	}

	public static int simpleRandom(final int min, final int max) {
		int x = min;
		int y = max;

		if (min == max) {
			return min;
		} else if (min > max) {
			x = max;
			y = min;
		}

		return (int) ThreadLocalRandom.current().nextInt(x, y);
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

	public static float randomFloat(float x, float v) {
		return (float) (Math.random() * (x - v) + v);
	}
	
	public static int randomInt(double x, double v) {
		return (int) (Math.random() * (x - v) + v);
	}
	
	public static double randomDouble(double x, double v) {
		return Math.random() * (x - v) + v;
	}
	
    public static String str(String s) {
        char[] n = StringUtils.stripControlCodes(s).toCharArray();
        StringBuilder v = new StringBuilder();

        for (char c : n) {
           if (c < 127 && c > 20) {
              v.append(c);
           }
        }

        return v.toString();
     }
}