import type { CSSProperties } from "react";

/**
 * Style constants for reusable inline styles.
 *
 * Use these when CSS classes aren't suitable (e.g., computed values).
 * For static patterns, prefer CSS classes from global.css.
 */

/**
 * Creates a field container style with calculated width based on 12-column grid.
 * @param size - Number of columns (1-12)
 */
export function getFieldContainerStyle(size: number): CSSProperties {
	return {
		width: `${(size / 12) * 100}%`,
		padding: "0 8px",
		boxSizing: "border-box",
	};
}

/**
 * Creates a flex direction style for radio groups.
 * @param direction - Layout direction
 */
export function getFlexDirectionStyle(direction: "horizontal" | "vertical"): CSSProperties {
	return {
		display: "flex",
		flexDirection: direction === "vertical" ? "column" : "row",
	};
}

/**
 * CSS class name builders.
 */
export const classNames = {
	/**
	 * Returns the CSS class for a read-only field with optional text alignment.
	 */
	readonlyField: (align?: "left" | "center" | "right") => {
		const classes = ["af-readonly-field"];
		if (align && align !== "left") {
			classes.push(`af-text-align-${align}`);
		}
		return classes.join(" ");
	},

	/**
	 * Combines multiple class names, filtering out undefined/null values.
	 */
	combine: (...classes: (string | undefined | null | false)[]): string => {
		return classes.filter(Boolean).join(" ");
	},
};

/**
 * Style builders for dynamic styles that depend on runtime values.
 */
export const styleBuilders = {
	/**
	 * Creates text alignment style.
	 */
	textAlign: (align?: "left" | "center" | "right"): CSSProperties | undefined => {
		return align ? { textAlign: align } : undefined;
	},

	/**
	 * Creates a style for absolutely positioned panel toggle buttons.
	 */
	panelToggle: (collapsed: boolean): CSSProperties => ({
		position: "absolute",
		right: collapsed ? 0 : 8,
		top: collapsed ? -48 : 8,
		zIndex: 1,
	}),
};
