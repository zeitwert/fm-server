package io.zeitwert.fm.util;

public class NumericUtils {

	/**
	 * Round to increasing stepsize with increasing value
	 * 
	 * @param value the value to round
	 * @return rounded value
	 */
	public static double roundProgressive(double value) {
		double absValue = Math.abs(value);
		if (absValue < 2000) {
			return 100 * Math.round(value / 100);
		} else if (absValue < 5000) {
			return 200 * Math.round(value / 200);
		} else if (absValue < 10000) {
			return 500 * Math.round(value / 500);
		}
		return 1000 * Math.round(value / 1000);
	}

}
