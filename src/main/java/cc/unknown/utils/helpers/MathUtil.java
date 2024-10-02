package cc.unknown.utils.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import cc.unknown.utils.Loona;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.minecraft.util.StringUtils;

@UtilityClass
public class MathUtil implements Loona {

	@Getter private final Random rand = new Random();
	
    public Number simpleRandom(Number min, Number max) {
        if (min instanceof Integer && max instanceof Integer) {
            int x = min.intValue();
            int y = max.intValue();

            if (x == y) {
                return x;
            } else if (x > y) {
                return ThreadLocalRandom.current().nextInt(y, x + 1);
            }

            return ThreadLocalRandom.current().nextInt(x, y + 1);
        } else if (min instanceof Double && max instanceof Double) {
            double x = min.doubleValue();
            double y = max.doubleValue();

            if (x == y) {
                return x;
            } else if (x > y) {
                return ThreadLocalRandom.current().nextDouble(y, x);
            }

            return ThreadLocalRandom.current().nextDouble(x, y);
        } else {
            throw new IllegalArgumentException("Unsupported Number type");
        }
    }
	public long nextLong(long origin, long bound) {
		return origin == bound ? origin : ThreadLocalRandom.current().nextLong(origin, bound);
	}

	public ArrayList<String> toArrayList(String[] x) {
		return new ArrayList<>(Arrays.asList(x));
	}

	public List<String> StringListToList(String[] whytho) {
		List<String> f = new ArrayList<>();
		Collections.addAll(f, whytho);
		return f;
	}

	public double round(double n, int d) {
		if (d == 0) {
			return (double) Math.round(n);
		} else {
			double p = Math.pow(10.0D, d);
			return (double) Math.round(n * p) / p;
		}
	}

    public Number randomNumber(Number min, Number max) {
        if (min instanceof Integer && max instanceof Integer) {
            return ThreadLocalRandom.current().nextInt(min.intValue(), max.intValue() + 1);
        } else if (min instanceof Double && max instanceof Double) {
        	return (double) (ThreadLocalRandom.current().nextDouble() * (max.doubleValue() - min.doubleValue()) + min.doubleValue());
        } else if (min instanceof Float && max instanceof Float) {
            return (float) (ThreadLocalRandom.current().nextDouble() * (max.floatValue() - min.floatValue()) + min.floatValue());
        } else {
            throw new IllegalArgumentException("Unsupported Number type");
        }
    }
	
    public String str(String s) {
        char[] n = StringUtils.stripControlCodes(s).toCharArray();
        StringBuilder v = new StringBuilder();

        for (char c : n) {
           if (c < 127 && c > 20) {
              v.append(c);
           }
        }

        return v.toString();
     }
    
    public int randomClickDelay(int minCPS, int maxCPS) {
        return (int) (Math.random() * (1000 / minCPS - 1000 / maxCPS + 1) + 1000 / maxCPS);
    }
}