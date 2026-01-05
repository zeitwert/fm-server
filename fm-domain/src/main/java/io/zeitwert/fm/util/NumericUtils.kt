package io.zeitwert.fm.util

import kotlin.math.abs

object NumericUtils {

	/**
	 * Round to increasing stepsize with increasing value
	 *
	 * @param value the value to round
	 * @return rounded value
	 */
	fun roundProgressive(value: Double): Double {
		val absValue = abs(value)
		if (absValue < 2000) {
			return (100 * Math.round(value / 100)).toDouble()
		} else if (absValue < 5000) {
			return (200 * Math.round(value / 200)).toDouble()
		} else if (absValue < 10000) {
			return (500 * Math.round(value / 500)).toDouble()
		}
		return (1000 * Math.round(value / 1000)).toDouble()
	}

}
