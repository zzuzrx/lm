package com.lm.util;
import java.util.Random;

public class RandomHelper {

	private RandomHelper() {
	}

	// 指数分布
	public static float expon(float rmean, Random rnd) {
		float u = rnd.nextFloat();
		while (u == 0.0f)
			u = rnd.nextFloat();
		return rmean * ((float) Math.log(u)) * (-1f);
	}

	//
	// public int irandi(int nvalue, float probd[]) {
	// int randInt = nvalue;
	// float u = rand();
	// for (int i = nvalue; i > 0; i--) {
	// if (u < probd[i]) {
	// randInt = i;
	// }
	// }
	// return randInt; // This is an indication of an error
	// }

	// 均匀分布
	public static float unifrm(float a, float b, Random rnd) {
		float u = rnd.nextFloat();
		return a + u * (b - a);
	}

	public static int discreteUniform(int a, int b, Random rnd) {
		return a + rnd.nextInt(b - a + 1);
	}

	// 泊松分布
	public static float erlang(int m, float mean, Random rnd) {
		float mean_exponential, sum;

		mean_exponential = mean / m;
		sum = (float) 0.0;
		for (int i = 0; i < m; i++)
			sum += expon(mean_exponential, rnd);
		return sum;
	}

	public static int randomInteger(float probDistrib[], Random rnd) {
		float u = rnd.nextFloat();
		for (int i = 0; i < probDistrib.length; i++) {
			if (u <= probDistrib[i])
				return i;
		}
		throw new AssertionError("improper probs");
	}

}