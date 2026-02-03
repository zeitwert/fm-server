// Shared configuration for projection charts to ensure vertical alignment

import { formatCompactCurrency } from "@/common/utils";

export const CHART_MARGIN = { top: 10, right: 30, left: 0, bottom: 0 };
export const Y_AXIS_WIDTH = 80;
export const SYNC_ID = "projection";

// Chart colors
export const COLORS = {
	originalValue: "#000000",
	timeValue: "#50aae1",
	maintenanceCosts: "#4b873c",
	restorationCosts: "#a0cd5f",
};

// Currency formatter for chart axes (compact notation with K/M suffixes)
export function formatCurrency(value: number): string {
	return formatCompactCurrency(value, 5);
}
