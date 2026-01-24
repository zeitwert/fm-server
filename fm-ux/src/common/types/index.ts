// Re-export Enumerated from its own file
export { type Enumerated } from "./Enumerated";

/**
 * Common form field props shared across all Af components.
 */
export interface AfFieldProps {
	/** React Hook Form field name (supports dot notation for nested fields) */
	name: string;
	/** Field label */
	label?: string;
	/** Adds asterisk to label (Zod schema handles actual validation) */
	required?: boolean;
	/** Disables the input */
	disabled?: boolean;
	/** Read-only mode (shows static value instead of input) */
	readOnly?: boolean;
	/** Tooltip help text */
	helpText?: string;
	/** Grid column span (1-24, default 24 = full width) */
	size?:
		| 1
		| 2
		| 3
		| 4
		| 5
		| 6
		| 7
		| 8
		| 9
		| 10
		| 11
		| 12
		| 13
		| 14
		| 15
		| 16
		| 17
		| 18
		| 19
		| 20
		| 21
		| 22
		| 23
		| 24;
}
