import type { ProjectionPeriod, BuildingElement } from "../types";

// =============================================================================
// CONSTANTS FROM ASPOSE DOCUMENT GENERATOR
// =============================================================================

/**
 * Points per millimeter (standard PostScript/PDF conversion)
 * From DocumentGenerationServiceImpl.kt: POINTS_PER_MM = 2.834647454889553
 */
export const POINTS_PER_MM = 2.834647454889553;

/**
 * A4 Landscape dimensions in points (842 × 595pt = 297mm × 210mm)
 */
export const A4_LANDSCAPE = {
	width: 297 * POINTS_PER_MM, // ~842pt
	height: 210 * POINTS_PER_MM, // ~595pt
	widthMm: 297,
	heightMm: 210,
};

/**
 * Page margins in mm (matching original template)
 */
export const PAGE_MARGINS = {
	top: 15,
	right: 20,
	bottom: 20,
	left: 20,
};

/**
 * Cover photo dimensions from DocumentGenerationServiceImpl.kt
 * CoverFotoWidth = 400, CoverFotoHeight = 230 (points)
 * Position: top=170pt from top margin, offset from right margin
 */
export const COVER_PHOTO = {
	width: 400, // points
	height: 230, // points
	widthMm: 400 / POINTS_PER_MM, // ~141mm
	heightMm: 230 / POINTS_PER_MM, // ~81mm
	topOffset: 170, // points from top margin
	rightOffset: 20, // points from right margin
};

/**
 * Location map dimensions from DocumentGenerationServiceImpl.kt
 * builder.insertImage(imageContent, 360.0, 360.0)
 */
export const LOCATION_MAP = {
	size: 360, // points (square)
	sizeMm: 360 / POINTS_PER_MM, // ~127mm
};

/**
 * One-pager rating dot positioning from DocumentGenerationServiceImpl.kt
 * Vertical: getRatingLineVOffset = 98.8 * POINTS_PER_MM + 11.84 * lineNr
 * Horizontal: getRatingHOffset = 90.3mm + ratingDelta * 49.2mm
 */
export const RATING_POSITION = {
	baseVerticalMm: 98.8, // mm from page top
	lineSpacingPt: 11.84, // points per line
	baseHorizontalMm: 90.3, // mm for rating 100
	horizontalRangeMm: 49.2, // mm range for ratings 50-100 (124.5 - 75.3)
	dotSize: 8, // points (connector shape size)
};

/**
 * Optimal renovation marker character (Wingdings circle)
 * From DocumentGenerationServiceImpl.kt: OptimumRenovationMarker = Character.toString(110.toChar())
 */
export const RENOVATION_MARKER = "●"; // Unicode bullet for HTML (replaces Wingdings)

// =============================================================================
// CONDITION COLOR MAPPING
// =============================================================================

/**
 * Color thresholds matching backend BuildingEvaluationServiceImpl
 * Maps condition values (0-100) to display colors
 */
export const CONDITION_COLORS = {
	veryBad: "#E54F29", // condition < 50
	bad: "#FAA724", // condition 50-69
	ok: "#78C06B", // condition 70-84
	good: "#338721", // condition >= 85
	unknown: "#999999", // null/undefined
};

/**
 * Get color for a condition value (matches backend logic)
 * @param condition - Z/N value (0-100)
 */
export function getConditionColor(condition: number | undefined | null): string {
	if (condition == null) return CONDITION_COLORS.unknown;
	if (condition < 50) return CONDITION_COLORS.veryBad;
	if (condition < 70) return CONDITION_COLORS.bad;
	if (condition < 85) return CONDITION_COLORS.ok;
	return CONDITION_COLORS.good;
}

// =============================================================================
// COST CALCULATIONS
// =============================================================================

/**
 * Calculate total restoration costs for a year range
 * @param periods - Projection periods
 * @param startYearOffset - Start year offset from first period (0 = first year)
 * @param endYearOffset - End year offset (inclusive)
 */
export function calculateRestorationCosts(
	periods: ProjectionPeriod[],
	startYearOffset: number,
	endYearOffset: number
): number {
	return periods
		.slice(startYearOffset, endYearOffset + 1)
		.reduce((sum, p) => sum + (p.restorationCosts || 0), 0);
}

/**
 * Calculate average maintenance costs for a year range
 * @param periods - Projection periods
 * @param startYearOffset - Start year offset from first period (0 = first year)
 * @param endYearOffset - End year offset (inclusive)
 */
export function calculateAverageMaintenance(
	periods: ProjectionPeriod[],
	startYearOffset: number,
	endYearOffset: number
): number {
	const slice = periods.slice(startYearOffset, endYearOffset + 1);
	if (slice.length === 0) return 0;
	const total = slice.reduce((sum, p) => sum + (p.maintenanceCosts || 0), 0);
	return Math.round(total / slice.length);
}

/**
 * Short-term restoration costs (years 0-1)
 */
export function getShortTermCosts(periods: ProjectionPeriod[]): number {
	return calculateRestorationCosts(periods, 0, 1);
}

/**
 * Mid-term restoration costs (years 2-5)
 */
export function getMidTermCosts(periods: ProjectionPeriod[]): number {
	return calculateRestorationCosts(periods, 2, 5);
}

/**
 * Long-term restoration costs (years 6-25)
 */
export function getLongTermCosts(periods: ProjectionPeriod[]): number {
	return calculateRestorationCosts(periods, 6, Math.min(25, periods.length - 1));
}

/**
 * Average maintenance costs for first 5 years
 */
export function getAverageMaintenanceCosts(periods: ProjectionPeriod[]): number {
	return calculateAverageMaintenance(periods, 0, 4);
}

// =============================================================================
// AGGREGATED PERIODS (with total and cumulative costs)
// =============================================================================

export interface AggregatedPeriod extends ProjectionPeriod {
	totalCosts: number;
	aggrCosts: number;
}

/**
 * Add totalCosts (maintenance + restoration) and cumulative aggrCosts
 * @param periods - Projection periods
 */
export function calculateAggregatedPeriods(periods: ProjectionPeriod[]): AggregatedPeriod[] {
	let cumulative = 0;
	return periods.map((p) => {
		const totalCosts = (p.maintenanceCosts || 0) + (p.restorationCosts || 0);
		cumulative += totalCosts;
		return {
			...p,
			totalCosts,
			aggrCosts: cumulative,
		};
	});
}

// =============================================================================
// ELEMENT CALCULATIONS
// =============================================================================

/**
 * Calculate restoration timeframe category for an element
 * @param restorationYear - Year of restoration
 * @param startYear - Projection start year
 * @returns "short" (0-1), "mid" (2-5), or "long" (6+)
 */
export function getRestorationTimeframe(
	restorationYear: number | undefined,
	startYear: number
): "short" | "mid" | "long" | null {
	if (!restorationYear) return null;
	const delta = restorationYear - startYear;
	if (delta <= 1) return "short";
	if (delta <= 5) return "mid";
	return "long";
}

/**
 * Calculate Z/N (Zeitwert/Neuwert) ratio from elements
 * Weighted average of element conditions by weight
 */
export function calculateZNRatio(elements: BuildingElement[]): number {
	const validElements = elements.filter((e) => e.weight != null && e.condition != null);
	if (validElements.length === 0) return 0;

	const totalWeight = validElements.reduce((sum, e) => sum + (e.weight || 0), 0);
	if (totalWeight === 0) return 0;

	const weightedSum = validElements.reduce(
		(sum, e) => sum + (e.weight || 0) * (e.condition || 0),
		0
	);
	return Math.round(weightedSum / totalWeight);
}

// =============================================================================
// FORMATTING UTILITIES
// =============================================================================

/**
 * Format number with Swiss locale (e.g., 1'234'567)
 */
export function formatNumber(value: number | undefined | null): string {
	if (value == null) return "-";
	return new Intl.NumberFormat("de-CH", {
		maximumFractionDigits: 0,
	}).format(Math.round(value));
}

/**
 * Format currency with CHF postfix
 */
export function formatCHF(value: number | undefined | null): string {
	if (value == null) return "-";
	return `${formatNumber(value)} CHF`;
}

/**
 * Format percentage value
 */
export function formatPercent(value: number | undefined | null): string {
	if (value == null) return "-";
	return `${formatNumber(value)}%`;
}

/**
 * Format date in German format (DD.MM.YYYY)
 * @param date - Date to format, defaults to current date
 */
export function formatDateGerman(date: Date = new Date()): string {
	return date.toLocaleDateString("de-DE", {
		day: "2-digit",
		month: "2-digit",
		year: "numeric",
	});
}

// =============================================================================
// POSITIONING CALCULATIONS (for One-Pager rating dots)
// =============================================================================

/**
 * Calculate vertical offset for a rating line in the one-pager
 * @param lineNr - Line number (0 = title/total, 1+ = elements)
 */
export function getRatingLineVerticalOffset(lineNr: number): number {
	return RATING_POSITION.baseVerticalMm * POINTS_PER_MM + RATING_POSITION.lineSpacingPt * lineNr;
}

/**
 * Calculate horizontal offset for a rating value in the one-pager
 * @param rating - Condition value (0-100)
 */
export function getRatingHorizontalOffset(rating: number): number {
	const ratingDelta = Math.min(100 - rating, 50) / 50;
	return (
		RATING_POSITION.baseHorizontalMm * POINTS_PER_MM +
		ratingDelta * RATING_POSITION.horizontalRangeMm * POINTS_PER_MM
	);
}

/**
 * Convert points to CSS mm
 */
export function ptToMm(points: number): string {
	return `${(points / POINTS_PER_MM).toFixed(2)}mm`;
}
