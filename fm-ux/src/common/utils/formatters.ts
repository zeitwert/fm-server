/**
 * Format a number with compact notation using K/M suffixes.
 * Scales: none (< 1000), K (thousands), M (millions).
 * Only switches to next scale when current scale would exceed maxDigits.
 *
 * @param value The number to format
 * @param maxDigits Maximum significant digits to display (default: 5)
 * @returns Formatted string with appropriate suffix
 */
export function formatCompactCurrency(value: number, maxDigits: number = 5): string {
	const absValue = Math.abs(value);

	// Create formatter with max significant digits
	const formatter = new Intl.NumberFormat("de-CH", {
		maximumSignificantDigits: maxDigits,
	});

	// Calculate thresholds based on maxDigits
	// Switch to M only when K scale would exceed maxDigits (e.g., 100'000K needs 6 digits)
	const mThreshold = 1_000 * Math.pow(10, maxDigits); // 100,000,000 for maxDigits=5

	if (absValue >= mThreshold) {
		return formatter.format(value / 1_000_000) + "M";
	}
	if (absValue >= 1_000) {
		return formatter.format(value / 1_000) + "k";
	}
	return formatter.format(value);
}
